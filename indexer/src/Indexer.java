import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.CompressionTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import au.edu.monash.csse.jmdict.model.Entry;
import au.edu.monash.csse.jmdict.model.Gloss;
import au.edu.monash.csse.jmdict.model.JMdict;
import au.edu.monash.csse.jmdict.model.KEle;
import au.edu.monash.csse.jmdict.model.REle;
import au.edu.monash.csse.jmdict.model.Sense;

public class Indexer {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("Usage: Indexer [path to JMdict.gz file] [path to index destination]");
			System.exit(1);
		}

		File jmdictFile = new File(args[0]);
		if (!jmdictFile.exists()) {
			System.err.println("Failed to find file: " + args[0]);
			System.exit(1);
		}

		GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(jmdictFile));
		Unmarshaller unmarshaller = JAXBContext.newInstance(JMdict.class.getPackage().getName()).createUnmarshaller();
		unmarshaller.setSchema(null);

		XMLInputFactory factory = XMLInputFactory.newFactory();
		factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
		XMLStreamReader streamReader = new XmlLangTranslatorStreamReader(factory.createXMLStreamReader(inputStream));
		JMdict jmdict = (JMdict) unmarshaller.unmarshal(streamReader);

		System.out.println("Finished parsing JMdict");

		Directory directory = new SimpleFSDirectory(new File(args[1]));
		IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_30, new SimpleAnalyzer(Version.LUCENE_30)));

		final int size = jmdict.getEntry().size();
		int processedCount = 0;
		double progress = 0;

		long uncompressedBytes = 0;
		long compressedBytes = 0;

		Map<String, Integer> languageCount = new HashMap<String, Integer>();

		for (Entry entry : jmdict.getEntry()) {
			Document document = new Document();
			ByteArrayOutputStream serializedBinary = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(serializedBinary);
			objectOutputStream.writeObject(entry);
			objectOutputStream.close();

			byte[] originalByteArray = serializedBinary.toByteArray();
			uncompressedBytes = uncompressedBytes + originalByteArray.length;
			byte[] compressByteArray = CompressionTools.compress(originalByteArray);
			compressedBytes = compressedBytes + compressByteArray.length;

			document.add(new Field("entry", compressByteArray));

			for (KEle kanjiElement : entry.getKEle()) {
				document.add(new Field("kanjiElement", kanjiElement.getKeb(), Store.NO, Index.ANALYZED));
			}

			for (REle readingElement : entry.getREle()) {
				document.add(new Field("readingElement", readingElement.getReb(), Store.NO, Index.ANALYZED));
			}

			for (Sense sense : entry.getSense()) {
				for (Gloss gloss : sense.getGloss()) {
					String lang = gloss.getXmlLang();
					languageCount.put(lang, (languageCount.get(lang) == null ? 0 : languageCount.get(lang)) + 1);
					document.add(new Field("gloss" + lang, gloss.getvalue(), Store.NO, Index.ANALYZED));
				}
			}

			indexWriter.addDocument(document);

			processedCount++;
			double newProgress = Math.round(100.0 / size * processedCount);
			if (newProgress > progress || processedCount == size) {
				progress = newProgress;
				System.out.println("Current progress: " + progress + "% (compression rate: " + Math.round(100.0 / uncompressedBytes * compressedBytes) + "%)");
			}
		}

		System.out.printf("Created index with %d entries (" + languageCount + ")", processedCount);
		System.out.println();

		indexWriter.optimize();
		System.out.println("Finished optimizing index");
		indexWriter.close();
		System.out.println("Index closed");
	}
}
