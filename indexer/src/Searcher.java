import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.util.Collections;

import jiten.model.Entry;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.CompressionTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

/**
 * <p>
 * Searches in the given JMdict index
 * </p>
 * <p>
 * When using Windows UTF-8 won't be properly printed on the console. <a href=
 * "http://paranoid-engineering.blogspot.com/2008/05/getting-unicode-output-in-eclipse.html"
 * >Check this workaround</a>.
 * </p>
 */
public class Searcher {

	private static final String[] FIELDS = new String[] { "expression", "reading", "sense-en", "sense-de", "sense-ru", "sense-fr" };

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Usage: Searcher [path to index] [saercher string]");
			System.exit(1);
		}

		String query = args[1];
		System.out.println("Searching for '" + query + "'");

		Directory directory = new SimpleFSDirectory(new File(args[0]));
		Query q = new MultiFieldQueryParser(Version.LUCENE_35, FIELDS, new StandardAnalyzer(Version.LUCENE_35, Collections.emptySet())).parse(query);

		long time = System.currentTimeMillis();

		IndexSearcher searcher = new IndexSearcher(IndexReader.open(directory, true));
		TopScoreDocCollector collector = TopScoreDocCollector.create(300, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			byte[] binaryValue = CompressionTools.decompress(d.getBinaryValue("entry"));
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(binaryValue));

			Entry entry = (Entry) inputStream.readObject();
			System.out.println(entry);
			// System.out.println(searcher.explain(q, docId));
		}

		System.out.println("Found " + collector.getTotalHits() + " hits in " + (System.currentTimeMillis() - time) + "ms");
	}
}
