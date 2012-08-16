package net.makimono.dictionary.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import net.makimono.dictionary.converter.KanaConverter;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public abstract class AbstractJaxbIndexer<ROOT, IT> implements Indexer {

	private final String contextPath;
	private KanaConverter kanaConverter = new KanaConverter();

	protected AbstractJaxbIndexer(String contextPath) {
		this.contextPath = contextPath;
	}

	protected KanaConverter getRomajiConverter() {
		return kanaConverter;
	}

	public void createIndex(File gzipXmlFile, Directory luceneDirectory) throws Exception {
		System.out.println("Parsing " + gzipXmlFile.getName());

		Reader reader = new InvalidXMLCharacterFilterReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(gzipXmlFile)), "UTF-8"));
		Unmarshaller unmarshaller = JAXBContext.newInstance(contextPath).createUnmarshaller();

		XMLInputFactory factory = XMLInputFactory.newFactory();
		XMLStreamReader streamReader = new XmlLangTranslatorStreamReader(factory.createXMLStreamReader(reader));

		@SuppressWarnings("unchecked")
		ROOT root = (ROOT) unmarshaller.unmarshal(streamReader);

		System.out.println("Finished parsing " + gzipXmlFile.getName());

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));
		config.setSimilarity(new NoLengthNormSimilarity());
		IndexWriter indexWriter = new IndexWriter(luceneDirectory, config);

		List<IT> list = getIteratable(root);
		final int size = list.size();
		int processedCount = 0;
		double progress = 0;

		for (IT it : list) {
			Document document = createDocument(it);
			if (document != null) {
				indexWriter.addDocument(document);
			}

			processedCount++;
			double newProgress = Math.round(100.0 / size * processedCount);
			if (newProgress > progress || processedCount == size) {
				progress = newProgress;
				System.out.println("Current progress: " + progress + "%");
			}
		}

		indexWriter.forceMerge(1);
		indexWriter.commit();
		System.out.println("Finished optimizing index");
		indexWriter.close();
		System.out.println("Index closed");
	}

	protected abstract List<IT> getIteratable(ROOT root);

	protected abstract Document createDocument(IT it) throws Exception;

}
