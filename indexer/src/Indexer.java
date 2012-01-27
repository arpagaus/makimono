import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import jiten.model.Dialect;
import jiten.model.Language;
import jiten.model.PartOfSpeech;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import au.edu.monash.csse.jmdict.model.Dial;
import au.edu.monash.csse.jmdict.model.Entry;
import au.edu.monash.csse.jmdict.model.Gloss;
import au.edu.monash.csse.jmdict.model.JMdict;
import au.edu.monash.csse.jmdict.model.KEle;
import au.edu.monash.csse.jmdict.model.KePri;
import au.edu.monash.csse.jmdict.model.Pos;
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

	private static final Map<String, String> JMDICT_ENTITY_REFERENCES = new HashMap<String, String>();

	static {
		JMDICT_ENTITY_REFERENCES.put("martial arts term", "MA");
		JMDICT_ENTITY_REFERENCES.put("rude or X-rated term (not displayed in educational software)", "X");
		JMDICT_ENTITY_REFERENCES.put("abbreviation", "abbr");
		JMDICT_ENTITY_REFERENCES.put("adjective (keiyoushi)", "adj-i");
		JMDICT_ENTITY_REFERENCES.put("adjectival nouns or quasi-adjectives (keiyodoshi)", "adj-na");
		JMDICT_ENTITY_REFERENCES.put("nouns which may take the genitive case particle `no'", "adj-no");
		JMDICT_ENTITY_REFERENCES.put("pre-noun adjectival (rentaishi)", "adj-pn");
		JMDICT_ENTITY_REFERENCES.put("`taru' adjective", "adj-t");
		JMDICT_ENTITY_REFERENCES.put("noun or verb acting prenominally", "adj-f");
		JMDICT_ENTITY_REFERENCES.put("former adjective classification (being removed)", "adj");
		JMDICT_ENTITY_REFERENCES.put("adverb (fukushi)", "adv");
		JMDICT_ENTITY_REFERENCES.put("adverb taking the `to' particle", "adv-to");
		JMDICT_ENTITY_REFERENCES.put("archaism", "arch");
		JMDICT_ENTITY_REFERENCES.put("ateji (phonetic) reading", "ateji");
		JMDICT_ENTITY_REFERENCES.put("auxiliary", "aux");
		JMDICT_ENTITY_REFERENCES.put("auxiliary verb", "aux-v");
		JMDICT_ENTITY_REFERENCES.put("auxiliary adjective", "aux-adj");
		JMDICT_ENTITY_REFERENCES.put("Buddhist term", "Buddh");
		JMDICT_ENTITY_REFERENCES.put("chemistry term", "chem");
		JMDICT_ENTITY_REFERENCES.put("children's language", "chn");
		JMDICT_ENTITY_REFERENCES.put("colloquialism", "col");
		JMDICT_ENTITY_REFERENCES.put("computer terminology", "comp");
		JMDICT_ENTITY_REFERENCES.put("conjunction", "conj");
		JMDICT_ENTITY_REFERENCES.put("counter", "ctr");
		JMDICT_ENTITY_REFERENCES.put("derogatory", "derog");
		JMDICT_ENTITY_REFERENCES.put("exclusively kanji", "eK");
		JMDICT_ENTITY_REFERENCES.put("exclusively kana", "ek");
		JMDICT_ENTITY_REFERENCES.put("Expressions (phrases, clauses, etc.)", "exp");
		JMDICT_ENTITY_REFERENCES.put("familiar language", "fam");
		JMDICT_ENTITY_REFERENCES.put("female term or language", "fem");
		JMDICT_ENTITY_REFERENCES.put("food term", "food");
		JMDICT_ENTITY_REFERENCES.put("geometry term", "geom");
		JMDICT_ENTITY_REFERENCES.put("gikun (meaning as reading)  or jukujikun (special kanji reading)", "gikun");
		JMDICT_ENTITY_REFERENCES.put("honorific or respectful (sonkeigo) language", "hon");
		JMDICT_ENTITY_REFERENCES.put("humble (kenjougo) language", "hum");
		JMDICT_ENTITY_REFERENCES.put("word containing irregular kanji usage", "iK");
		JMDICT_ENTITY_REFERENCES.put("idiomatic expression", "id");
		JMDICT_ENTITY_REFERENCES.put("word containing irregular kana usage", "ik");
		JMDICT_ENTITY_REFERENCES.put("interjection (kandoushi)", "int");
		JMDICT_ENTITY_REFERENCES.put("irregular okurigana usage", "io");
		JMDICT_ENTITY_REFERENCES.put("irregular verb", "iv");
		JMDICT_ENTITY_REFERENCES.put("linguistics terminology", "ling");
		JMDICT_ENTITY_REFERENCES.put("manga slang", "m-sl");
		JMDICT_ENTITY_REFERENCES.put("male term or language", "male");
		JMDICT_ENTITY_REFERENCES.put("male slang", "male-sl");
		JMDICT_ENTITY_REFERENCES.put("mathematics", "math");
		JMDICT_ENTITY_REFERENCES.put("military", "mil");
		JMDICT_ENTITY_REFERENCES.put("noun (common) (futsuumeishi)", "n");
		JMDICT_ENTITY_REFERENCES.put("adverbial noun (fukushitekimeishi)", "n-adv");
		JMDICT_ENTITY_REFERENCES.put("noun, used as a suffix", "n-suf");
		JMDICT_ENTITY_REFERENCES.put("noun, used as a prefix", "n-pref");
		JMDICT_ENTITY_REFERENCES.put("noun (temporal) (jisoumeishi)", "n-t");
		JMDICT_ENTITY_REFERENCES.put("numeric", "num");
		JMDICT_ENTITY_REFERENCES.put("word containing out-dated kanji", "oK");
		JMDICT_ENTITY_REFERENCES.put("obsolete term", "obs");
		JMDICT_ENTITY_REFERENCES.put("obscure term", "obsc");
		JMDICT_ENTITY_REFERENCES.put("out-dated or obsolete kana usage", "ok");
		JMDICT_ENTITY_REFERENCES.put("onomatopoeic or mimetic word", "on-mim");
		JMDICT_ENTITY_REFERENCES.put("pronoun", "pn");
		JMDICT_ENTITY_REFERENCES.put("poetical term", "poet");
		JMDICT_ENTITY_REFERENCES.put("polite (teineigo) language", "pol");
		JMDICT_ENTITY_REFERENCES.put("prefix", "pref");
		JMDICT_ENTITY_REFERENCES.put("proverb", "proverb");
		JMDICT_ENTITY_REFERENCES.put("particle", "prt");
		JMDICT_ENTITY_REFERENCES.put("physics terminology", "physics");
		JMDICT_ENTITY_REFERENCES.put("rare", "rare");
		JMDICT_ENTITY_REFERENCES.put("sensitive", "sens");
		JMDICT_ENTITY_REFERENCES.put("slang", "sl");
		JMDICT_ENTITY_REFERENCES.put("suffix", "suf");
		JMDICT_ENTITY_REFERENCES.put("word usually written using kanji alone", "uK");
		JMDICT_ENTITY_REFERENCES.put("word usually written using kana alone", "uk");
		JMDICT_ENTITY_REFERENCES.put("Ichidan verb", "v1");
		JMDICT_ENTITY_REFERENCES.put("Nidan verb with 'u' ending (archaic)", "v2a-s");
		JMDICT_ENTITY_REFERENCES.put("Yondan verb with `hu/fu' ending (archaic)", "v4h");
		JMDICT_ENTITY_REFERENCES.put("Yondan verb with `ru' ending (archaic)", "v4r");
		JMDICT_ENTITY_REFERENCES.put("Godan verb (not completely classified)", "v5");
		JMDICT_ENTITY_REFERENCES.put("Godan verb - -aru special class", "v5aru");
		JMDICT_ENTITY_REFERENCES.put("Godan verb with `bu' ending", "v5b");
		JMDICT_ENTITY_REFERENCES.put("Godan verb with `gu' ending", "v5g");
		JMDICT_ENTITY_REFERENCES.put("Godan verb with `ku' ending", "v5k");
		JMDICT_ENTITY_REFERENCES.put("Godan verb - Iku/Yuku special class", "v5k-s");
		JMDICT_ENTITY_REFERENCES.put("Godan verb with `mu' ending", "v5m");
		JMDICT_ENTITY_REFERENCES.put("Godan verb with `nu' ending", "v5n");
		JMDICT_ENTITY_REFERENCES.put("Godan verb with `ru' ending", "v5r");
		JMDICT_ENTITY_REFERENCES.put("Godan verb with `ru' ending (irregular verb)", "v5r-i");
		JMDICT_ENTITY_REFERENCES.put("Godan verb with `su' ending", "v5s");
		JMDICT_ENTITY_REFERENCES.put("Godan verb with `tsu' ending", "v5t");
		JMDICT_ENTITY_REFERENCES.put("Godan verb with `u' ending", "v5u");
		JMDICT_ENTITY_REFERENCES.put("Godan verb with `u' ending (special class)", "v5u-s");
		JMDICT_ENTITY_REFERENCES.put("Godan verb - Uru old class verb (old form of Eru)", "v5uru");
		JMDICT_ENTITY_REFERENCES.put("Godan verb with `zu' ending", "v5z");
		JMDICT_ENTITY_REFERENCES.put("Ichidan verb - zuru verb (alternative form of -jiru verbs)", "vz");
		JMDICT_ENTITY_REFERENCES.put("intransitive verb", "vi");
		JMDICT_ENTITY_REFERENCES.put("Kuru verb - special class", "vk");
		JMDICT_ENTITY_REFERENCES.put("irregular nu verb", "vn");
		JMDICT_ENTITY_REFERENCES.put("irregular ru verb, plain form ends with -ri", "vr");
		JMDICT_ENTITY_REFERENCES.put("noun or participle which takes the aux. verb suru", "vs");
		JMDICT_ENTITY_REFERENCES.put("su verb - precursor to the modern suru", "vs-c");
		JMDICT_ENTITY_REFERENCES.put("suru verb - special class", "vs-s");
		JMDICT_ENTITY_REFERENCES.put("suru verb - irregular", "vs-i");
		JMDICT_ENTITY_REFERENCES.put("Kyoto-ben", "kyb");
		JMDICT_ENTITY_REFERENCES.put("Osaka-ben", "osb");
		JMDICT_ENTITY_REFERENCES.put("Kansai-ben", "ksb");
		JMDICT_ENTITY_REFERENCES.put("Kantou-ben", "ktb");
		JMDICT_ENTITY_REFERENCES.put("Tosa-ben", "tsb");
		JMDICT_ENTITY_REFERENCES.put("Touhoku-ben", "thb");
		JMDICT_ENTITY_REFERENCES.put("Tsugaru-ben", "tsug");
		JMDICT_ENTITY_REFERENCES.put("Kyuushuu-ben", "kyu");
		JMDICT_ENTITY_REFERENCES.put("Ryuukyuu-ben", "rkb");
		JMDICT_ENTITY_REFERENCES.put("Nagano-ben", "nab");
		JMDICT_ENTITY_REFERENCES.put("transitive verb", "vt");
		JMDICT_ENTITY_REFERENCES.put("vulgar expression or word", "vulg");
	}

	private void startIndexing(File jmdictFile, File indexDirectory) throws Exception {
		System.out.println("Parsing JMdict");

		GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(jmdictFile));
		Unmarshaller unmarshaller = JAXBContext.newInstance(JMdict.class.getPackage().getName()).createUnmarshaller();
		unmarshaller.setSchema(null);

		XMLInputFactory factory = XMLInputFactory.newFactory();
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

			byte[] compressByteArray = getSerializedEntry(transformEntry(entry));
			document.add(new Field("entry", compressByteArray));

			indexWriter.addDocument(document);

			processedCount++;
			double newProgress = Math.round(100.0 / size * processedCount);
			if (newProgress > progress || processedCount == size) {
				progress = newProgress;
				System.out.println("Current progress: " + progress + "%");
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

	private byte[] getSerializedEntry(jiten.model.Entry jitenEntry) throws IOException {
		ByteArrayOutputStream serializedBinary = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(serializedBinary);
		jiten.model.Entry.writeEntry(objectOutputStream, jitenEntry);
		objectOutputStream.close();

		return serializedBinary.toByteArray();
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

			jitenSense.getPartsOfSpeech().addAll(transformPartOfSpeech(sense.getPos()));
			jitenSense.getDialects().addAll(transformDialects(sense.getDial()));

			for (Gloss gloss : sense.getGloss()) {
				jiten.model.Gloss jitenGloss = new jiten.model.Gloss();
				jitenGloss.setLanguage(Language.valueOf(gloss.getXmlLang()));
				jitenGloss.setValue(gloss.getvalue());

				jitenSense.getGlosses().add(jitenGloss);
			}
		}

		return jitenEntry;
	}

	private List<PartOfSpeech> transformPartOfSpeech(List<Pos> pos) {
		ArrayList<PartOfSpeech> jitenPos = new ArrayList<PartOfSpeech>();
		for (Pos p : pos) {
			PartOfSpeech partOfSpeech = PartOfSpeech.valueOf(resolveEnumStringForEntityReference(p.getvalue()));
			jitenPos.add(partOfSpeech);
		}
		return jitenPos;
	}

	private List<Dialect> transformDialects(List<Dial> dial) {
		ArrayList<Dialect> jitenDialects = new ArrayList<Dialect>();
		for (Dial d : dial) {
			Dialect dialect = Dialect.valueOf(resolveEnumStringForEntityReference(d.getvalue()));
			jitenDialects.add(dialect);
		}
		return jitenDialects;
	}

	private String resolveEnumStringForEntityReference(String entityReference) {
		return "JMdict_" + JMDICT_ENTITY_REFERENCES.get(entityReference).replaceAll("-", "_");
	}
}
