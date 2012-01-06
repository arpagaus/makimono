import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import jiten.model.Language;

import org.apache.lucene.analysis.Analyzer;
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
import au.edu.monash.csse.jmdict.model.KePri;
import au.edu.monash.csse.jmdict.model.REle;
import au.edu.monash.csse.jmdict.model.RePri;
import au.edu.monash.csse.jmdict.model.Sense;

public class Indexer {

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

		new Indexer().startIndexing(jmdictFile, new File(args[1]));
	}

	long uncompressedBytes = 0;
	long compressedBytes = 0;

	private void startIndexing(File jmdictFile, File indexDirectory) throws Exception {
		System.out.println("Parsing JMdict");

		GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(jmdictFile));
		Unmarshaller unmarshaller = JAXBContext.newInstance(JMdict.class.getPackage().getName()).createUnmarshaller();
		unmarshaller.setSchema(null);

		XMLInputFactory factory = XMLInputFactory.newFactory();
		factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
		XMLStreamReader streamReader = new XmlLangTranslatorStreamReader(factory.createXMLStreamReader(inputStream));
		JMdict jmdict = (JMdict) unmarshaller.unmarshal(streamReader);

		System.out.println("Finished parsing JMdict");

		Directory directory = new SimpleFSDirectory(indexDirectory);
		IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_35, getAnalyzer()));

		final int size = jmdict.getEntry().size();
		int processedCount = 0;
		double progress = 0;

		Map<String, Integer> languageCount = new HashMap<String, Integer>();

		for (Entry entry : jmdict.getEntry()) {
			Document document = new Document();

			for (KEle kanjiElement : entry.getKEle()) {
				Field expression = new Field("expression", kanjiElement.getKeb(), Store.NO, Index.ANALYZED);
				expression.setBoost(getBoostForExpression(kanjiElement.getKePri()));
				document.add(expression);
			}

			for (REle readingElement : entry.getREle()) {
				Field reading = new Field("reading", readingElement.getReb(), Store.NO, Index.ANALYZED);
				reading.setBoost(getBoostForReading(readingElement.getRePri()));
				document.add(reading);
			}

			for (Sense sense : entry.getSense()) {
				for (Gloss gloss : sense.getGloss()) {
					String glossValue = cleanGloss(gloss.getvalue());
					gloss.setvalue(glossValue);

					glossValue = glossValue.replaceAll("\\(.*\\)", "");

					String lang = gloss.getXmlLang();
					languageCount.put(lang, (languageCount.get(lang) == null ? 0 : languageCount.get(lang)) + 1);
					document.add(new Field("sense-" + lang, glossValue, Store.NO, Index.ANALYZED));
				}
			}

			byte[] compressByteArray = getCompressedEntry(transformEntry(entry));
			document.add(new Field("entry", compressByteArray));

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

		indexWriter.forceMerge(1);
		indexWriter.commit();
		System.out.println("Finished optimizing index");
		indexWriter.close();
		System.out.println("Index closed");
	}

	private Analyzer getAnalyzer() {
		return new SimpleAnalyzer(Version.LUCENE_35);
	}

	private byte[] getCompressedEntry(jiten.model.Entry jitenEntry) throws IOException {
		ByteArrayOutputStream serializedBinary = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(serializedBinary);
		objectOutputStream.writeObject(jitenEntry);
		objectOutputStream.close();

		byte[] originalByteArray = serializedBinary.toByteArray();
		uncompressedBytes = uncompressedBytes + originalByteArray.length;
		byte[] compressByteArray = CompressionTools.compress(originalByteArray);
		compressedBytes = compressedBytes + compressByteArray.length;
		return compressByteArray;
	}

	private String cleanGloss(String value) {
		return value.replace("(n) ", "");
	}

	private float getBoostForReading(List<RePri> rePri) {
		float boost = 1.0f;
		for (RePri r : rePri) {
			boost = boost * getBoost(r.getvalue());
		}
		return boost;
	}

	private float getBoostForExpression(List<KePri> kePri) {
		float boost = 1.0f;
		for (KePri k : kePri) {
			boost = boost * getBoost(k.getvalue());
		}
		return boost;
	}

	/**
	 * <p>
	 * This and the equivalent re_pri field are provided to record information
	 * about the relative priority of the entry, and consist of codes indicating
	 * the word appears in various references which can be taken as an
	 * indication of the frequency with which the word is used. This field is
	 * intended for use either by applications which want to concentrate on
	 * entries of a particular priority, or to generate subset files. The
	 * current values in this field are:
	 * </p>
	 * <ul>
	 * <li>news1/2: appears in the "wordfreq" file compiled by Alexandre Girardi
	 * from the Mainichi Shimbun. (See the Monash ftp archive for a copy.) Words
	 * in the first 12,000 in that file are marked "news1" and words in the
	 * second 12,000 are marked "news2".</li>
	 * <li>ichi1/2: appears in the "Ichimango goi bunruishuu", Senmon Kyouiku
	 * Publishing, Tokyo, 1998. (The entries marked "ichi2" were demoted from
	 * ichi1 because they were observed to have low frequencies in the WWW and
	 * newspapers.)</li>
	 * <li>spec1 and spec2: a small number of words use this marker when they
	 * are detected as being common, but are not included in other lists.</li>
	 * <li>gai1/2: common loanwords, based on the wordfreq file.</li>
	 * <li>
	 * nfxx: this is an indicator of frequency-of-use ranking in the wordfreq
	 * file. "xx" is the number of the set of 500 words in which the entry can
	 * be found, with "01" assigned to the first 500, "02" to the second, and so
	 * on. (The entries with news1, ichi1, spec1 and gai1 values are marked with
	 * a "(P)" in the EDICT and EDICT2 files.)</li>
	 * </ul>
	 * <p>
	 * The reason both the kanji and reading elements are tagged is because on
	 * occasions a priority is only associated with a particular kanji/reading
	 * pair.
	 * </p>
	 * 
	 * @return
	 */
	private float getBoost(String priority) {
		int value = 1; // Between 1 and 100
		if (priority.equalsIgnoreCase("gai1")) {
			value = 20;
		} else if (priority.equalsIgnoreCase("gai2")) {
			value = 40;
		} else if (priority.equalsIgnoreCase("ichi1")) {
			value = 100;
		} else if (priority.equalsIgnoreCase("ichi2")) {
			value = 80;
		} else if (priority.equalsIgnoreCase("news1")) {
			value = 90;
		} else if (priority.equalsIgnoreCase("news2")) {
			value = 70;
		} else if (priority.equalsIgnoreCase("spec1")) {
			value = 90;
		} else if (priority.equalsIgnoreCase("spec2")) {
			value = 70;
		} else if (priority.toLowerCase().startsWith("nf")) {
			value = 101 - Math.max(0, Integer.parseInt(priority.substring(2)));
		} else {
			System.err.println("Failed to get boos for '" + priority + "'");
		}
		float boost = 10000.0f / 100 * value;
		return boost;
	}

	private jiten.model.Entry transformEntry(Entry entry) {
		jiten.model.Entry jitenEntry = new jiten.model.Entry();

		jitenEntry.setId(Integer.valueOf(entry.getEntSeq()));

		for (KEle kEle : entry.getKEle()) {
			jitenEntry.getExpressions().add(kEle.getKeb());
		}

		for (REle rEle : entry.getREle()) {
			jitenEntry.getReadings().add(rEle.getReb());
		}

		for (Sense sense : entry.getSense()) {
			jiten.model.Sense jitenSense = new jiten.model.Sense();
			jitenEntry.getSenses().add(jitenSense);
			for (Gloss gloss : sense.getGloss()) {
				jiten.model.Gloss jitenGloss = new jiten.model.Gloss();
				jitenGloss.setLanguage(Language.valueOf(gloss.getXmlLang()));
				jitenGloss.setValue(gloss.getvalue());

				jitenSense.getGlosses().add(jitenGloss);
			}
		}

		return jitenEntry;
	}
}
