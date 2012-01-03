import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.PrintStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.CompressionTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import au.edu.monash.csse.jmdict.model.Entry;
import au.edu.monash.csse.jmdict.model.JMdict;

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

	private static final String ENCODING = "UTF-8";

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Usage: Searcher [path to index] [saercher string]");
			System.exit(1);
		}
		boolean xmlOutput = args.length == 3 && args[2].equalsIgnoreCase("-xml");

		Directory directory = new SimpleFSDirectory(new File(args[0]));
		Query q = new QueryParser(Version.LUCENE_30, "keyword", new SimpleAnalyzer(Version.LUCENE_30)).parse(args[1]);

		IndexSearcher searcher = new IndexSearcher(directory, true);
		TopScoreDocCollector collector = TopScoreDocCollector.create(300, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		JMdict dict = new JMdict();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			byte[] binaryValue = CompressionTools.decompress(d.getBinaryValue("entry"));
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(binaryValue));
			Entry entry = (Entry) inputStream.readObject();
			dict.getEntry().add(entry);

			if (!xmlOutput) {
				System.out.println(ReflectionToStringBuilder.toString(entry, HierarchicalStyle.getInstance()));
			}
		}

		if (xmlOutput) {
			Marshaller marshaller = JAXBContext.newInstance(JMdict.class.getPackage().getName()).createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
			PrintStream printStream = new PrintStream(System.out, true, ENCODING);
			marshaller.marshal(dict, printStream);
		}

		System.out.println("Found " + collector.getTotalHits() + " hits");
	}

}
