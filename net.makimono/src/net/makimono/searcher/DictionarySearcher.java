package net.makimono.searcher;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.makimono.model.DictionaryEntry;
import net.makimono.model.Language;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

public class DictionarySearcher implements Closeable {
	private static final int MAX_SIZE = 20;

	private List<Language> languages;

	private Directory dictionaryDirectory;
	private IndexSearcher indexSearcher;

	public DictionarySearcher(File dictionaryPath) throws IOException {
		this.dictionaryDirectory = new SimpleFSDirectory(dictionaryPath);
		languages = Arrays.asList(Language.values());
	}

	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}

	private IndexSearcher getIndexSearcher() throws IOException {
		if (indexSearcher == null) {
			indexSearcher = new IndexSearcher(IndexReader.open(dictionaryDirectory, true));
		}
		return indexSearcher;
	}

	public List<DictionaryEntry> search(String queryString) throws IOException, ParseException {
		if (queryString == null || queryString.equals("")) {
			return new ArrayList<DictionaryEntry>(0);
		}
		queryString = queryString.toLowerCase();

		BooleanQuery booleanQuery = new BooleanQuery();
		for (DictionaryFields field : DictionaryFields.values()) {
			if (!field.isSenseField() || languages.contains(field.getLanguage())) {
				PrefixQuery prefixQuery = new PrefixQuery(new Term(field.name(), queryString));
				if (field.isAnalyzedField()) {
					prefixQuery.setBoost(Float.MIN_NORMAL);
				} else {
					TermQuery termQuery = new TermQuery(new Term(field.name(), queryString));
					termQuery.setBoost(Short.MAX_VALUE);
					booleanQuery.add(termQuery, Occur.SHOULD);
				}
				booleanQuery.add(prefixQuery, Occur.SHOULD);

			}
		}

		ArrayList<DictionaryEntry> entries = new ArrayList<DictionaryEntry>();
		IndexSearcher searcher = getIndexSearcher();
		TopDocs topDocs = searcher.search(booleanQuery, MAX_SIZE);
		for (ScoreDoc d : topDocs.scoreDocs) {
			DictionaryEntry entry = getByDocId(d.doc);
			if (!entries.contains(entry)) {
				entries.add(entry);
			}
		}
		return entries;
	}

	public void close() throws IOException {
		IOException exception = null;
		if (indexSearcher != null) {
			try {
				indexSearcher.close();
			} catch (IOException e) {
				exception = e;
			}
			try {
				indexSearcher.getIndexReader().close();
			} catch (IOException e) {
				if (exception == null) {
					exception = e;
				}
			} finally {
				indexSearcher = null;
			}
		}
		if (dictionaryDirectory != null) {
			try {
				dictionaryDirectory.close();
			} catch (IOException e) {
				if (exception == null) {
					exception = e;
				}
			}
		}

		if (exception != null) {
			throw exception;
		}
	}

	public DictionaryEntry getByDocId(int docId) throws IOException {
		Document document = getIndexSearcher().doc(docId);
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(document.getBinaryValue("entry")));
			DictionaryEntry entry = DictionaryEntry.readEntry(inputStream);
			entry.setDocId(docId);
			return entry;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public TreeSet<String> suggest(String prefix) throws IOException {
		TreeSet<String> suggestions = new TreeSet<String>();
		if (isQualifiedForSuggestions(prefix)) {
			Set<DictionaryFields> fields = new HashSet<DictionaryFields>();
			if (containsKanji(prefix)) {
				fields.add(DictionaryFields.EXPRESSION);
			} else if (isKana(prefix)) {
				fields.add(DictionaryFields.READING);
			} else {
				for (DictionaryFields field : DictionaryFields.values()) {
					if (field.isSenseField() && languages.contains(field.getLanguage())) {
						fields.add(field);
					}
				}
			}

			IndexReader reader = getIndexSearcher().getIndexReader();
			for (DictionaryFields field : fields) {
				TermEnum terms = reader.terms(new Term(field.name(), prefix));
				do {
					if (terms.term().text().toLowerCase().startsWith(prefix.toLowerCase())) {
						suggestions.add(terms.term().text().trim());
					} else {
						break;
					}
				} while (terms.next() && suggestions.size() < MAX_SIZE);
			}
		}
		return suggestions;
	}

	private boolean isQualifiedForSuggestions(String prefix) {
		if (prefix == null || prefix.length() == 0) {
			return false;
		}

		if (isKana(prefix)) {
			return prefix.length() >= 2;
		} else if (containsKanji(prefix)) {
			return prefix.length() >= 1;
		} else {
			return prefix.length() >= 3;
		}
	}

	private boolean isKana(String string) {
		string = string.replaceAll("\\s*", "");
		for (Character c : string.toCharArray()) {
			UnicodeBlock block = UnicodeBlock.of(c);
			if (block != UnicodeBlock.HIRAGANA && block != UnicodeBlock.KATAKANA) {
				return false;
			}
		}
		return true;
	}

	private boolean containsKanji(String string) {
		for (Character c : string.toCharArray()) {
			if (UnicodeBlock.of(c) == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
				return true;
			}
		}
		return false;
	}
}
