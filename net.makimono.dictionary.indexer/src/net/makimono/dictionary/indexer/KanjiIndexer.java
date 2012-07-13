package net.makimono.dictionary.indexer;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.makimono.dictionary.indexer.parser.KradfileParser;
import net.makimono.dictionary.searcher.KanjiFieldName;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.CompressionTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.NumericField;

import au.edu.monash.csse.kanjidic.model.CpValue;
import au.edu.monash.csse.kanjidic.model.Kanjidic2;
import au.edu.monash.csse.kanjidic.model.Meaning;
import au.edu.monash.csse.kanjidic.model.RadValue;
import au.edu.monash.csse.kanjidic.model.Reading;
import au.edu.monash.csse.kanjidic.model.ReadingMeaning;

public class KanjiIndexer extends AbstractJaxbIndexer<Kanjidic2, au.edu.monash.csse.kanjidic.model.Character> {
	private static final int FREQ_MAX = 2500;
	public static final int RADICAL_UNICODE_OFFSET = 0x2F00;

	private Map<Integer, String> strokePaths = new HashMap<Integer, String>();
	private Map<String, Set<String>> kanjiRadicals = new HashMap<String, Set<String>>();

	public KanjiIndexer(Properties properties) {
		super(Kanjidic2.class.getPackage().getName());

		try {
			String kanjivgProperty = properties.getProperty("kanjivg");
			if (StringUtils.isNotBlank(kanjivgProperty)) {
				KanjiVgIndexer kanjiVgIndexer = new KanjiVgIndexer();
				this.strokePaths = kanjiVgIndexer.getStrokePaths(new File(kanjivgProperty));
			}

			String kradfileProperty = properties.getProperty("kradfile");
			if (StringUtils.isNotBlank(kradfileProperty)) {
				KradfileParser kradfileParser = new KradfileParser(new File(kradfileProperty));
				this.kanjiRadicals = kradfileParser.getKanjiRadicals();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected List<au.edu.monash.csse.kanjidic.model.Character> getIteratable(Kanjidic2 root) {
		return root.getCharacter();
	}

	@Override
	public Document createDocument(au.edu.monash.csse.kanjidic.model.Character character) throws Exception {
		ReadingMeaning rm = character.getReadingMeaning();
		if (rm == null || rm.getRmgroup() == null) {
			return null;
		}

		Integer codePoint = getCodePoint(character);
		if (codePoint == null) {
			return null;
		}

		Document document = new Document();
		String literal = String.valueOf(Character.toChars(codePoint));
		document.add(new Field(KanjiFieldName.LITERAL.name(), literal, Store.YES, Index.NOT_ANALYZED));
		document.add(new Field(KanjiFieldName.CODE_POINT.name(), ByteBuffer.allocate(4).putInt(codePoint).array()));

		Byte jlpt = character.getMisc().getJlpt();
		if (jlpt != null) {
			document.add(new Field(KanjiFieldName.JLPT.name(), new byte[] { jlpt }));
		}

		Byte grade = character.getMisc().getGrade();
		if (grade != null) {
			document.add(new Field(KanjiFieldName.GRADE.name(), new byte[] { grade }));
		}

		Byte strokeCount = character.getMisc().getStrokeCount().get(0);
		NumericField strokeCountField = new NumericField(KanjiFieldName.STROKE_COUNT.name(), Store.YES, true);
		strokeCountField.setIntValue(strokeCount);
		document.add(strokeCountField);

		for (RadValue r : character.getRadical().getRadValue()) {
			if (r.getRadType().equalsIgnoreCase("classical")) {
				document.add(new Field(KanjiFieldName.MAIN_RADICAL.name(), ByteBuffer.allocate(2).putShort(r.getValue()).array()));
			}
		}

		addReadings(document, rm.getRmgroup().getReading());
		addMeanings(document, rm.getRmgroup().getMeaning());

		for (String nanori : rm.getNanori()) {
			document.add(new Field(KanjiFieldName.NANORI.name(), nanori, Store.YES, Index.NO));
		}

		short freq = character.getMisc().getFreq();
		if (freq > 0.0) {
			document.add(new Field(KanjiFieldName.FREQUENCY.name(), ByteBuffer.allocate(2).putShort(freq).array()));
			document.setBoost(1.0f + (100.0f / FREQ_MAX * (FREQ_MAX + 1 - Math.min(freq, FREQ_MAX))));
		}

		if (strokePaths.containsKey(codePoint)) {
			byte[] binary = CompressionTools.compress(strokePaths.get(codePoint).getBytes("UTF-8"));
			document.add(new Field(KanjiFieldName.STROKE_PATHS.name(), binary));
		}

		if (kanjiRadicals.containsKey(literal)) {
			for (String radical : kanjiRadicals.get(literal)) {
				document.add(new Field(KanjiFieldName.RADICAL.name(), radical, Store.YES, Index.NOT_ANALYZED));
			}
		}

		return document;
	}

	private Integer getCodePoint(au.edu.monash.csse.kanjidic.model.Character character) {
		Integer codePoint = null;
		for (CpValue v : character.getCodepoint().getCpValue()) {
			if (v.getCpType().equals("ucs")) {
				codePoint = Integer.decode("0x" + v.getValue()).intValue();
			}
		}
		return codePoint;
	}

	private void addMeanings(Document document, List<Meaning> meanings) {
		for (Meaning m : meanings) {
			String lang;
			if (m.getMLang() == null) {
				lang = "EN";
			} else {
				lang = m.getMLang().toUpperCase();
			}
			document.add(new Field("MEANING_" + lang, m.getValue(), Store.YES, Index.NOT_ANALYZED));
			document.add(new Field("MEANING_ANALYZED_" + lang, m.getValue(), Store.NO, Index.ANALYZED));
		}
	}

	private void addReadings(Document document, List<Reading> readings) {
		for (Reading reading : readings) {
			String cleanValue = cleanReadingString(reading.getValue());

			if (reading.getRType().equalsIgnoreCase("ja_on")) {
				document.add(new Field(KanjiFieldName.ONYOMI.name(), reading.getValue(), Store.YES, Index.NO));

				document.add(new Field(KanjiFieldName.ONYOMI.name(), cleanValue, Store.NO, Index.NOT_ANALYZED));

				String romaji = getRomajiConverter().convertKanaToRomajiSimple(cleanValue);
				document.add(new Field(KanjiFieldName.ROMAJI.name(), romaji, Store.NO, Index.NOT_ANALYZED));

				String hiragana = getRomajiConverter().convertKatakanaToHiragana(cleanValue);
				document.add(new Field(KanjiFieldName.ONYOMI.name(), hiragana, Store.NO, Index.NOT_ANALYZED));

			} else if (reading.getRType().equalsIgnoreCase("ja_kun")) {
				document.add(new Field(KanjiFieldName.KUNYOMI.name(), reading.getValue(), Store.YES, Index.NO));

				document.add(new Field(KanjiFieldName.KUNYOMI.name(), cleanValue, Store.NO, Index.NOT_ANALYZED));

				String romaji = getRomajiConverter().convertKanaToRomajiSimple(cleanValue);
				document.add(new Field(KanjiFieldName.ROMAJI.name(), romaji, Store.NO, Index.NOT_ANALYZED));

			} else if (reading.getRType().equalsIgnoreCase("pinyin")) {
				document.add(new Field(KanjiFieldName.PINYIN.name(), reading.getValue(), Store.YES, Index.NO));

			} else if (reading.getRType().equalsIgnoreCase("korean_h")) {
				document.add(new Field(KanjiFieldName.HANGUL.name(), reading.getValue(), Store.YES, Index.NO));
			}
		}
	}

	String cleanReadingString(String value) {
		String cleanValue = StringUtils.substringBefore(value, ".");
		cleanValue = cleanValue.replaceAll("\\p{Punct}", "");
		return cleanValue;
	}
}
