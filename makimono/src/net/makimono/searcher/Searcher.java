package net.makimono.searcher;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.makimono.model.Entry;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.Lookup.LookupResult;
import org.apache.lucene.search.suggest.fst.FSTLookup;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

public class Searcher implements Closeable {
	private static final int MAX_SIZE = 20;

	private QueryParser queryParserNotAnalyzed;
	private QueryParser queryParserAnalyzed;
	private Directory dictionaryDirectory;
	private IndexSearcher indexSearcher;

	private Lookup lookup;

	public Searcher(File dictionaryPath) throws IOException {
		this.dictionaryDirectory = new SimpleFSDirectory(dictionaryPath);
	}

	private Lookup getLookup() throws IOException {
		if (lookup == null) {
			lookup = new FSTLookup();
			DictionaryTermFreqIterator iterator = new DictionaryTermFreqIterator(getIndexSearcher().getIndexReader().terms());
			iterator.setIncludedFields(Arrays.asList(Fields.ALL_NOT_ANALYZED_FIELDS));
			lookup.build(iterator);
		}
		return lookup;
	}

	private IndexSearcher getIndexSearcher() throws IOException {
		if (indexSearcher == null) {
			indexSearcher = new IndexSearcher(IndexReader.open(dictionaryDirectory, true));
		}
		return indexSearcher;
	}

	private QueryParser getQueryParserAnalyzed() {
		if (queryParserAnalyzed == null) {
			queryParserAnalyzed = new MultiFieldQueryParser(Version.LUCENE_35, Fields.ALL_ANALYZED_FIELDS, new SimpleAnalyzer(Version.LUCENE_35));
			queryParserAnalyzed.setDefaultOperator(Operator.AND);
		}
		return queryParserAnalyzed;
	}

	private QueryParser getQueryParserNotAnalyzed() {
		if (queryParserNotAnalyzed == null) {
			queryParserNotAnalyzed = new MultiFieldQueryParser(Version.LUCENE_35, Fields.ALL_NOT_ANALYZED_FIELDS, new SimpleAnalyzer(Version.LUCENE_35));
			queryParserNotAnalyzed.setDefaultOperator(Operator.AND);
		}
		return queryParserNotAnalyzed;
	}

	public ArrayList<Entry> search(String queryString) throws IOException, ParseException {
		if (queryString == null || queryString.equals("")) {
			return new ArrayList<Entry>(0);
		}

		ArrayList<Entry> entries = new ArrayList<Entry>();
		searchTopDocs(entries, getQueryParserNotAnalyzed().parse(queryString));
		searchTopDocs(entries, getQueryParserAnalyzed().parse(queryString));

		return entries;
	}

	private void searchTopDocs(ArrayList<Entry> entries, Query query) throws IOException {
		int limitCount = MAX_SIZE - entries.size();
		if (limitCount <= 0) {
			return;
		}

		IndexSearcher searcher = getIndexSearcher();
		TopDocs topDocs = searcher.search(query, limitCount);
		for (ScoreDoc d : topDocs.scoreDocs) {
			Entry entry = getByDocId(d.doc);
			if (!entries.contains(entry)) {
				entries.add(entry);
			}
			if (entries.size() >= MAX_SIZE) {
				return;
			}
		}
	}

	public void close() throws IOException {
		queryParserNotAnalyzed = null;

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

	public Entry getByDocId(int docId) throws IOException {
		Document document = getIndexSearcher().doc(docId);
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(document.getBinaryValue("entry")));
			Entry entry = Entry.readEntry(inputStream);
			entry.setDocId(docId);
			return entry;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ArrayList<String> suggest(String input) throws IOException {
		if (input == null || input.length() == 0) {
			return new ArrayList<String>(0);
		}

		List<LookupResult> results = getLookup().lookup(input, false, 100);
		ArrayList<String> suggestions = new ArrayList<String>(results.size());
		for (LookupResult r : results) {
			suggestions.add(r.key);
			System.out.println(r);
		}
		return suggestions;
	}
}
