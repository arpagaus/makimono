package net.makimono.indexer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.makimono.model.Dialect;
import net.makimono.model.FieldOfApplication;
import net.makimono.model.Language;
import net.makimono.model.Miscellaneous;
import net.makimono.model.PartOfSpeech;
import net.makimono.searcher.DictionaryFields;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.store.Directory;

import au.edu.monash.csse.jmdict.model.Entry;
import au.edu.monash.csse.jmdict.model.Gloss;
import au.edu.monash.csse.jmdict.model.JMdict;
import au.edu.monash.csse.jmdict.model.KEle;
import au.edu.monash.csse.jmdict.model.KePri;
import au.edu.monash.csse.jmdict.model.REle;
import au.edu.monash.csse.jmdict.model.RePri;
import au.edu.monash.csse.jmdict.model.ReRestr;
import au.edu.monash.csse.jmdict.model.Sense;

public class DictionaryIndexer extends AbstractJaxbIndexer<JMdict, Entry> {

	private static final Map<String, String> JMDICT_ENTITY_REFERENCES = new HashMap<String, String>();

	static {
		InputStream stream = DictionaryIndexer.class.getClassLoader().getResourceAsStream(DictionaryIndexer.class.getPackage().getName().replaceAll("\\.", "/") + "/JMdictReferences.properties");
		Properties p = new Properties();
		try {
			p.load(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (java.util.Map.Entry<Object, Object> e : p.entrySet()) {
			JMDICT_ENTITY_REFERENCES.put(e.getValue().toString(), e.getKey().toString());
		}
	}

	public DictionaryIndexer() {
		super(JMdict.class.getPackage().getName());
	}

	private Map<String, Integer> languageCount = new HashMap<String, Integer>();

	@Override
	public void createIndex(File gzipXmlFile, Directory luceneDirectory) throws Exception {
		super.createIndex(gzipXmlFile, luceneDirectory);
		System.out.println("languageCount=" + languageCount);
	}

	@Override
	protected List<Entry> getIteratable(JMdict root) {
		return root.getEntry();
	}

	@Override
	protected Document createDocument(Entry entry) throws Exception {
		Document document = new Document();

		for (KEle kanjiElement : entry.getKEle()) {
			Field expression = new Field(DictionaryFields.EXPRESSION.name(), kanjiElement.getKeb(), Store.NO, Index.NOT_ANALYZED);
			document.add(expression);
		}

		for (REle readingElement : entry.getREle()) {
			Field reading = new Field(DictionaryFields.READING.name(), readingElement.getReb(), Store.NO, Index.NOT_ANALYZED);
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
		return document;
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

	net.makimono.model.Entry transformEntry(Entry jmdictEntry) throws Exception {
		net.makimono.model.Entry entry = new net.makimono.model.Entry();
		entry.setId(Integer.valueOf(jmdictEntry.getEntSeq()));

		for (KEle kEle : jmdictEntry.getKEle()) {
			entry.getExpressions().add(kEle.getKeb());
		}

		for (int i = 0; i < jmdictEntry.getREle().size(); i++) {
			REle rEle = jmdictEntry.getREle().get(i);
			entry.getReadings().add(rEle.getReb());

			for (ReRestr r : rEle.getReRestr()) {
				int expressionIndex = entry.getExpressions().indexOf(r.getvalue());
				if (expressionIndex >= 0) {
					entry.addReadingRestriction(i, expressionIndex);
				}
			}
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
