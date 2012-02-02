package jiten.searcher;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import jiten.model.Entry;

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
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public class Searcher implements Closeable {
	private static final int MAX_SIZE = 100;
	private static final String[] FIELDS = new String[] { "expression", "reading", "sense-en", "sense-de", "sense-ru", "sense-fr" };

	private QueryParser queryParser;
	private Directory directory;
	private IndexSearcher indexSearcher;

	public Searcher(Directory directory) {
		this.directory = directory;
	}

	private IndexSearcher getIndexSearcher() throws IOException {
		if (indexSearcher == null) {
			indexSearcher = new IndexSearcher(IndexReader.open(directory, true));
		}
		return indexSearcher;
	}

	private QueryParser getQueryParser() {
		if (queryParser == null) {
			queryParser = new MultiFieldQueryParser(Version.LUCENE_35, FIELDS, new SimpleAnalyzer(Version.LUCENE_35));
			queryParser.setDefaultOperator(Operator.AND);
		}
		return queryParser;
	}

	public ArrayList<Entry> search(String queryString) throws IOException, ParseException {
		if (queryString == null || queryString.equals("")) {
			return new ArrayList<Entry>(0);
		}

		Query query = getQueryParser().parse(queryString);

		TopScoreDocCollector collector = TopScoreDocCollector.create(MAX_SIZE, true);
		IndexSearcher searcher = getIndexSearcher();
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		ArrayList<Entry> entries = new ArrayList<Entry>();
		for (int i = 0; i < hits.length; ++i) {
			Entry entry = getByDocId(hits[i].doc);
			entries.add(entry);
		}

		return entries;
	}

	public void close() throws IOException {
		queryParser = null;

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
		if (directory != null) {
			try {
				directory.close();
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
}
