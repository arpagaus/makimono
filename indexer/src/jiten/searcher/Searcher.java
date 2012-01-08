package jiten.searcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import jiten.model.Entry;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public class Searcher {
	private static final int MAX_SIZE = 100;
	private static final String[] FIELDS = new String[] { "expression", "reading", "sense-en", "sense-de", "sense-ru", "sense-fr" };

	private QueryParser queryParser;
	private Directory directory;
	private IndexSearcher indexSearcher;
	private Inflater decompressor;

	public Searcher(Directory directory) {
		this.directory = directory;
	}

	private Inflater getDecompressor() {
		if (decompressor == null) {
			decompressor = new Inflater();
		}
		decompressor.reset();
		return decompressor;
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
		}
		return queryParser;
	}

	public List<Entry> search(String queryString) throws IOException, ParseException {
		if (queryString == null || queryString.equals("")) {
			return Collections.emptyList();
		}

		Query query = getQueryParser().parse(queryString);

		TopScoreDocCollector collector = TopScoreDocCollector.create(MAX_SIZE, true);
		IndexSearcher searcher = getIndexSearcher();
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		List<Entry> entries = new ArrayList<Entry>();
		for (int i = 0; i < hits.length; ++i) {
			Entry entry = getByDocId(hits[i].doc);
			entries.add(entry);
		}

		return entries;
	}

	private byte[] decompress(byte[] value) throws DataFormatException {
		// Multiply with 2 because the compression is expected to be max 50%
		ByteArrayOutputStream bos = new ByteArrayOutputStream(value.length * 2);
		Inflater decompressor = getDecompressor();

		decompressor.setInput(value);

		final byte[] buf = new byte[1024];
		while (!decompressor.finished()) {
			int count = decompressor.inflate(buf);
			bos.write(buf, 0, count);
		}

		return bos.toByteArray();
	}

	public void close() {
		queryParser = null;

		if (indexSearcher != null) {
			try {
				indexSearcher.close();
			} catch (IOException e) {
			}
			try {
				indexSearcher.getIndexReader().close();
			} catch (IOException e) {
			} finally {
				indexSearcher = null;
			}
		}
		if (directory != null) {
			try {
				directory.close();
			} catch (IOException e) {
			}
		}

		if (decompressor != null) {
			decompressor.end();
			decompressor = null;
		}
	}

	public Entry getByDocId(int docId) throws IOException {
		Document document = getIndexSearcher().doc(docId);
		try {
			byte[] binaryValue = decompress(document.getBinaryValue("entry"));
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(binaryValue));

			Entry entry = (Entry) inputStream.readObject();
			entry.setDocId(docId);
			return entry;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
