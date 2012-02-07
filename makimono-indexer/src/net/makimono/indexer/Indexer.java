package net.makimono.indexer;

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

import net.makimono.model.Dialect;
import net.makimono.model.FieldOfApplication;
import net.makimono.model.Language;
import net.makimono.model.Miscellaneous;
import net.makimono.model.PartOfSpeech;
import net.makimono.searcher.Fields;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.DefaultSimilarity;
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

	@SuppressWarnings("serial")
	private static final Map<String, String> JMDICT_ENTITY_REFERENCES = new HashMap<String, String>() {
		{

			put("martial arts term", "MA");
			put("rude or X-rated term (not displayed in educational software)", "X");
			put("abbreviation", "abbr");
			put("adjective (keiyoushi)", "adj-i");
			put("adjectival nouns or quasi-adjectives (keiyodoshi)", "adj-na");
			put("nouns which may take the genitive case particle `no'", "adj-no");
			put("pre-noun adjectival (rentaishi)", "adj-pn");
			put("`taru' adjective", "adj-t");
			put("noun or verb acting prenominally", "adj-f");
			put("former adjective classification (being removed)", "adj");
			put("adverb (fukushi)", "adv");
			put("adverb taking the `to' particle", "adv-to");
			put("archaism", "arch");
			put("ateji (phonetic) reading", "ateji");
			put("auxiliary", "aux");
			put("auxiliary verb", "aux-v");
			put("auxiliary adjective", "aux-adj");
			put("Buddhist term", "Buddh");
			put("chemistry term", "chem");
			put("children's language", "chn");
			put("colloquialism", "col");
			put("computer terminology", "comp");
			put("conjunction", "conj");
			put("counter", "ctr");
			put("derogatory", "derog");
			put("exclusively kanji", "eK");
			put("exclusively kana", "ek");
			put("Expressions (phrases, clauses, etc.)", "exp");
			put("familiar language", "fam");
			put("female term or language", "fem");
			put("food term", "food");
			put("geometry term", "geom");
			put("gikun (meaning as reading)  or jukujikun (special kanji reading)", "gikun");
			put("honorific or respectful (sonkeigo) language", "hon");
			put("humble (kenjougo) language", "hum");
			put("word containing irregular kanji usage", "iK");
			put("idiomatic expression", "id");
			put("word containing irregular kana usage", "ik");
			put("interjection (kandoushi)", "int");
			put("irregular okurigana usage", "io");
			put("irregular verb", "iv");
			put("linguistics terminology", "ling");
			put("manga slang", "m-sl");
			put("male term or language", "male");
			put("male slang", "male-sl");
			put("mathematics", "math");
			put("military", "mil");
			put("noun (common) (futsuumeishi)", "n");
			put("adverbial noun (fukushitekimeishi)", "n-adv");
			put("noun, used as a suffix", "n-suf");
			put("noun, used as a prefix", "n-pref");
			put("noun (temporal) (jisoumeishi)", "n-t");
			put("numeric", "num");
			put("word containing out-dated kanji", "oK");
			put("obsolete term", "obs");
			put("obscure term", "obsc");
			put("out-dated or obsolete kana usage", "ok");
			put("onomatopoeic or mimetic word", "on-mim");
			put("pronoun", "pn");
			put("poetical term", "poet");
			put("polite (teineigo) language", "pol");
			put("prefix", "pref");
			put("proverb", "proverb");
			put("particle", "prt");
			put("physics terminology", "physics");
			put("rare", "rare");
			put("sensitive", "sens");
			put("slang", "sl");
			put("suffix", "suf");
			put("word usually written using kanji alone", "uK");
			put("word usually written using kana alone", "uk");
			put("Ichidan verb", "v1");
			put("Nidan verb with 'u' ending (archaic)", "v2a-s");
			put("Yondan verb with `hu/fu' ending (archaic)", "v4h");
			put("Yondan verb with `ru' ending (archaic)", "v4r");
			put("Godan verb (not completely classified)", "v5");
			put("Godan verb - -aru special class", "v5aru");
			put("Godan verb with `bu' ending", "v5b");
			put("Godan verb with `gu' ending", "v5g");
			put("Godan verb with `ku' ending", "v5k");
			put("Godan verb - Iku/Yuku special class", "v5k-s");
			put("Godan verb with `mu' ending", "v5m");
			put("Godan verb with `nu' ending", "v5n");
			put("Godan verb with `ru' ending", "v5r");
			put("Godan verb with `ru' ending (irregular verb)", "v5r-i");
			put("Godan verb with `su' ending", "v5s");
			put("Godan verb with `tsu' ending", "v5t");
			put("Godan verb with `u' ending", "v5u");
			put("Godan verb with `u' ending (special class)", "v5u-s");
			put("Godan verb - Uru old class verb (old form of Eru)", "v5uru");
			put("Godan verb with `zu' ending", "v5z");
			put("Ichidan verb - zuru verb (alternative form of -jiru verbs)", "vz");
			put("intransitive verb", "vi");
			put("Kuru verb - special class", "vk");
			put("irregular nu verb", "vn");
			put("irregular ru verb, plain form ends with -ri", "vr");
			put("noun or participle which takes the aux. verb suru", "vs");
			put("su verb - precursor to the modern suru", "vs-c");
			put("suru verb - special class", "vs-s");
			put("suru verb - irregular", "vs-i");
			put("Kyoto-ben", "kyb");
			put("Osaka-ben", "osb");
			put("Kansai-ben", "ksb");
			put("Kantou-ben", "ktb");
			put("Tosa-ben", "tsb");
			put("Touhoku-ben", "thb");
			put("Tsugaru-ben", "tsug");
			put("Kyuushuu-ben", "kyu");
			put("Ryuukyuu-ben", "rkb");
			put("Nagano-ben", "nab");
			put("transitive verb", "vt");
			put("vulgar expression or word", "vulg");
		}
	};

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
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));
		config.setSimilarity(new DefaultSimilarity() {
			@Override
			public float computeNorm(String field, FieldInvertState state) {
				return state.getBoost();
			}
		});
		IndexWriter indexWriter = new IndexWriter(directory, config);

		final int size = jmdict.getEntry().size();
		int processedCount = 0;
		double progress = 0;

		Map<String, Integer> languageCount = new HashMap<String, Integer>();

		for (Entry entry : jmdict.getEntry()) {
			Document document = new Document();

			for (KEle kanjiElement : entry.getKEle()) {
				Field expression = new Field(Fields.EXPRESSION.name(), kanjiElement.getKeb(), Store.NO, Index.NOT_ANALYZED);
				document.add(expression);
			}

			for (REle readingElement : entry.getREle()) {
				Field reading = new Field(Fields.READING.name(), readingElement.getReb(), Store.NO, Index.NOT_ANALYZED);
				document.add(reading);
			}

			for (Sense sense : entry.getSense()) {
				for (Gloss gloss : sense.getGloss()) {
					String glossValue = cleanGloss(gloss.getvalue());
					gloss.setvalue(glossValue);

					glossValue = glossValue.replaceAll("\\(.*\\)", "");
					glossValue = glossValue.toLowerCase();

					String lang = gloss.getXmlLang().toUpperCase();
					languageCount.put(lang, (languageCount.get(lang) == null ? 0 : languageCount.get(lang)) + 1);

					document.add(new Field("SENSE_" + lang, glossValue, Store.NO, Index.NOT_ANALYZED));
					document.add(new Field("SENSE_ANALYZED_" + lang, glossValue, Store.NO, Index.ANALYZED));
				}
			}

			byte[] compressByteArray = getSerializedEntry(transformEntry(entry));
			document.add(new Field("entry", compressByteArray));
			document.setBoost((float) getBoostForEntry(entry));
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

	private byte[] getSerializedEntry(net.makimono.model.Entry entry) throws IOException {
		ByteArrayOutputStream serializedBinary = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(serializedBinary);
		net.makimono.model.Entry.writeEntry(objectOutputStream, entry);
		objectOutputStream.close();

		return serializedBinary.toByteArray();
	}

	private String cleanGloss(String value) {
		return value.replace("(n) ", "");
	}

	float getBoostForEntry(Entry entry) {
		float boost = 1f;
		for (KEle element : entry.getKEle()) {
			for (KePri priority : element.getKePri()) {
				boost = Math.max(boost, getBoost(priority.getvalue()));
			}
		}
		for (REle element : entry.getREle()) {
			for (RePri priority : element.getRePri()) {
				boost = Math.max(boost, getBoost(priority.getvalue()));
			}
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
	float getBoost(String priority) {
		if (priority.equalsIgnoreCase("ichi1") || priority.equalsIgnoreCase("spec1")) {
			return 100;
		}
		if (priority.equalsIgnoreCase("ichi2") || priority.equalsIgnoreCase("spec2")) {
			return 75;
		} else if (priority.toLowerCase().startsWith("nf")) {
			return (51 - Integer.parseInt(priority.substring(2))) + 62.5f;
		}
		return -1;
	}

	private net.makimono.model.Entry transformEntry(Entry jmdictEntry) throws Exception {
		net.makimono.model.Entry entry = new net.makimono.model.Entry();

		entry.setId(Integer.valueOf(jmdictEntry.getEntSeq()));

		for (KEle kEle : jmdictEntry.getKEle()) {
			entry.getExpressions().add(kEle.getKeb());
		}

		for (REle rEle : jmdictEntry.getREle()) {
			entry.getReadings().add(rEle.getReb());
		}

		for (Sense jmdictSense : jmdictEntry.getSense()) {
			net.makimono.model.Sense sense = new net.makimono.model.Sense();
			entry.getSenses().add(sense);

			sense.getPartsOfSpeech().addAll(transformEnum(PartOfSpeech.class, jmdictSense.getPos()));
			sense.getDialects().addAll(transformEnum(Dialect.class, jmdictSense.getDial()));
			sense.getMiscellaneous().addAll(transformEnum(Miscellaneous.class, jmdictSense.getMisc()));
			sense.getFieldsOfApplication().addAll(transformEnum(FieldOfApplication.class, jmdictSense.getField()));

			for (Gloss jmdictGloss : jmdictSense.getGloss()) {
				net.makimono.model.Gloss gloss = new net.makimono.model.Gloss();
				gloss.setLanguage(Language.valueOf(jmdictGloss.getXmlLang()));
				gloss.setValue(jmdictGloss.getvalue());

				sense.getGlosses().add(gloss);
			}
		}

		return entry;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T extends Enum> List<T> transformEnum(Class<T> e, List<?> list) throws Exception {
		List<T> result = new ArrayList<T>();
		for (Object o : list) {
			Object value = o.getClass().getMethod("getvalue").invoke(o);
			result.add((T) Enum.valueOf(e, resolveEnumStringForEntityReference(value.toString())));
		}
		return result;
	}

	private String resolveEnumStringForEntityReference(String entityReference) {
		return "JMdict_" + JMDICT_ENTITY_REFERENCES.get(entityReference).replaceAll("-", "_");
	}
}
