package net.makimono.searcher;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.Character.UnicodeBlock;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.makimono.model.Language;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

public abstract class AbstractSearcher implements Closeable, Searcher {
	private static final int MAX_SUGGESTION = 10;

	private List<Language> languages;

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

	public TreeSet<String> suggest(String prefix) throws IOException {
		TreeSet<String> suggestions = new TreeSet<String>();
		if (isQualifiedForSuggestions(prefix)) {
			Set<IndexFieldName> fields = new HashSet<IndexFieldName>();
			if (containsJapanese(prefix)) {
				for (IndexFieldName field : getFieldNames()) {
					if (!field.isMeaning()) {
						fields.add(field);
					}
				}
			} else {
				for (IndexFieldName field : getFieldNames()) {
					if (languages.contains(field.getLanguage())) {
						fields.add(field);
					}
				}
			}

			IndexReader reader = getIndexSearcher().getIndexReader();
			for (IndexFieldName field : fields) {
				TermEnum terms = reader.terms(new Term(field.name(), prefix));
				do {
					if (terms.term() != null && terms.term().text().toLowerCase().startsWith(prefix.toLowerCase())) {
						suggestions.add(terms.term().text().trim());
					} else {
						break;
					}
				} while (terms.next() && suggestions.size() < MAX_SUGGESTION);
			}
		}
		return suggestions;
	}

	protected abstract Set<? extends IndexFieldName> getFieldNames();

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
