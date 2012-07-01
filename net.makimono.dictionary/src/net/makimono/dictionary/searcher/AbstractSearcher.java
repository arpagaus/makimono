package net.makimono.dictionary.searcher;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.makimono.dictionary.model.Entry;
import net.makimono.dictionary.model.Language;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReader.FieldOption;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

public abstract class AbstractSearcher<T extends Entry> implements Closeable, Searcher<T> {
	private static final int MAX_SUGGESTION = 10;
	private static final int MAX_SIZE = 100;

	private static final float MAX_BOOST = 1f;
	private static final float MIN_BOOST = 0.0001f;

	private List<Language> languages;
	private boolean romajiSearchEnabled = true;

	private Directory dictionaryDirectory;
	private IndexSearcher indexSearcher;

	public AbstractSearcher(File dictionaryPath) throws IOException {
		this.dictionaryDirectory = new SimpleFSDirectory(dictionaryPath);
		languages = Arrays.asList(Language.values());
	}

	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}

	protected List<Language> getLanguages() {
		return languages;
	}

	public void setRomajiSearchEnabled(boolean romajiSearchEnabled) {
		this.romajiSearchEnabled = romajiSearchEnabled;
	}

	public boolean isRomajiSearchEnabled() {
		return romajiSearchEnabled;
	}

	protected IndexSearcher getIndexSearcher() throws IOException {
		if (indexSearcher == null) {
			indexSearcher = new IndexSearcher(IndexReader.open(dictionaryDirectory, true));
		}
		return indexSearcher;
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

	public List<T> search(String queryString) throws IOException {
		if (queryString == null || queryString.equals("")) {
			return Collections.emptyList();
		}
		queryString = queryString.toLowerCase();

		Set<String> tokens = extractTokens(queryString);

		BooleanQuery booleanQuery = new BooleanQuery();
		for (String fieldName : getIndexedFieldNames()) {
			IndexFieldName field = getIndexFieldName(fieldName);
			if (includeFieldInQuery(field)) {
				if (field.isAnalyzed()) {
					for (String token : tokens) {
						TermQuery termQuery = new TermQuery(new Term(field.name(), token));
						termQuery.setBoost(MIN_BOOST);
						booleanQuery.add(termQuery, Occur.SHOULD);
					}
				} else {
					TermQuery termQuery = new TermQuery(new Term(field.name(), queryString));
					termQuery.setBoost(MAX_BOOST);
					booleanQuery.add(termQuery, Occur.SHOULD);
				}
			}
		}
		return getEntriesForQuery(booleanQuery);
	}

	protected List<T> getEntriesForQuery(Query query) throws IOException {
		List<T> entries = new ArrayList<T>();
		TopDocs topDocs = getIndexSearcher().search(query, MAX_SIZE);
		for (ScoreDoc d : topDocs.scoreDocs) {
			T entry = getEntryByDocId(d.doc);
			if (!entries.contains(entry)) {
				entries.add(entry);
			}
		}
		return entries;
	}

	private boolean includeFieldInQuery(IndexFieldName field) {
		return (!field.isMeaning() || getLanguages().contains(field.getLanguage())) && (isRomajiSearchEnabled() || !field.isRomaji());
	}

	protected Collection<String> getIndexedFieldNames() throws IOException {
		return getIndexSearcher().getIndexReader().getFieldNames(FieldOption.INDEXED);
	}

	protected abstract T getEntryByDocId(int doc) throws IOException;

	protected Set<String> extractTokens(String queryString) throws IOException {
		Set<String> token = new HashSet<String>();
		TokenStream tokenStream = new SimpleAnalyzer(Version.LUCENE_35).tokenStream(null, new StringReader(queryString));
		while (tokenStream.incrementToken()) {
			token.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
		}
		return token;
	}

	public TreeSet<String> suggest(String prefix) throws IOException {
		TreeSet<String> suggestions = new TreeSet<String>();
		if (isQualifiedForSuggestions(prefix)) {
			prefix = prefix.toLowerCase();

			Set<IndexFieldName> fields = new HashSet<IndexFieldName>();
			for (String fieldName : getIndexedFieldNames()) {
				IndexFieldName field = getIndexFieldName(fieldName);
				if (!field.isMeaning() && (!field.isRomaji() || isRomajiSearchEnabled())) {
					fields.add(field);
				} else if (languages.contains(field.getLanguage())) {
					fields.add(field);
				}
			}

			IndexReader reader = getIndexSearcher().getIndexReader();
			for (IndexFieldName field : fields) {
				TermEnum terms = reader.terms(new Term(field.name(), prefix));
				do {
					if (terms.term() != null && terms.term().text().startsWith(prefix)) {
						suggestions.add(terms.term().text().trim());
					} else {
						break;
					}
				} while (terms.next() && suggestions.size() < MAX_SUGGESTION);
			}
		}
		return suggestions;
	}

	protected abstract IndexFieldName getIndexFieldName(String fieldName);

	private boolean isQualifiedForSuggestions(String prefix) {
		if (prefix == null || prefix.length() == 0) {
			return false;
		}

		if (containsJapanese(prefix)) {
			return prefix.length() >= 1;
		} else {
			return prefix.length() >= 3;
		}
	}

	private boolean containsJapanese(String string) {
		for (Character c : string.toCharArray()) {
			UnicodeBlock block = UnicodeBlock.of(c);
			if (block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || block == UnicodeBlock.KATAKANA || block == UnicodeBlock.HIRAGANA) {
				return true;
			}
		}
		return false;
	}
}
