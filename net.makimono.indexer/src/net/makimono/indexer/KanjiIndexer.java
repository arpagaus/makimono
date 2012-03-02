package net.makimono.indexer;

import java.nio.ByteBuffer;
import java.util.List;

import net.makimono.searcher.KanjiDictionaryFields;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

import au.edu.monash.csse.kanjidic.model.CpValue;
import au.edu.monash.csse.kanjidic.model.Kanjidic2;
import au.edu.monash.csse.kanjidic.model.Meaning;
import au.edu.monash.csse.kanjidic.model.RadValue;
import au.edu.monash.csse.kanjidic.model.Reading;
import au.edu.monash.csse.kanjidic.model.ReadingMeaning;

public class KanjiIndexer extends AbstractJaxbIndexer<Kanjidic2, au.edu.monash.csse.kanjidic.model.Character> {

	private static final int FREQ_MAX = 2500;

	public KanjiIndexer() {
		super(Kanjidic2.class.getPackage().getName());
	}

	@Override
	protected List<au.edu.monash.csse.kanjidic.model.Character> getIteratable(Kanjidic2 root) {
		return root.getCharacter();
	}

	@Override
	public Document createDocument(au.edu.monash.csse.kanjidic.model.Character character) throws Exception {
		ReadingMeaning rm = character.getReadingMeaning();
		if (rm == null || rm.getRmgroup() == null || rm.getRmgroup().getMeaning().isEmpty() || rm.getRmgroup().getReading().isEmpty()) {
			return null;
		}

		Integer codePoint = getCodePoint(character);
		if (codePoint == null) {
			return null;
		}

		Document document = new Document();
		String literal = String.valueOf(Character.toChars(codePoint));
		document.add(new Field(KanjiDictionaryFields.LITERAL.name(), literal, Store.YES, Index.NOT_ANALYZED));
		document.add(new Field(KanjiDictionaryFields.CODE_POINT.name(), ByteBuffer.allocate(4).putInt(codePoint).array()));

		Byte strokeCount = character.getMisc().getStrokeCount().get(0);
		document.add(new Field(KanjiDictionaryFields.STROKE_COUNT.name(), new byte[] { strokeCount }));

		short radical = -1;
		for (RadValue r : character.getRadical().getRadValue()) {
			if (r.getRadType().equalsIgnoreCase("classical")) {
				radical = r.getValue();
			}
		}
		document.add(new Field(KanjiDictionaryFields.RADICAL.name(), ByteBuffer.allocate(2).putShort(radical).array()));

		addReadings(document, rm.getRmgroup().getReading());
		addMeanings(document, rm.getRmgroup().getMeaning());

		short freq = character.getMisc().getFreq();
		if (freq > 0.0) {
			document.setBoost(1.0f + (100.0f / FREQ_MAX * (FREQ_MAX + 1 - Math.min(freq, FREQ_MAX))));
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
			document.add(new Field("MEANING_" + lang, m.getValue(), Store.YES, Index.ANALYZED));
		}
	}

	private void addReadings(Document document, List<Reading> readings) {
		for (Reading r : readings) {
			if (r.getRType().equalsIgnoreCase("ja_on")) {
				document.add(new Field(KanjiDictionaryFields.ONYOMI.name(), r.getValue(), Store.YES, Index.NOT_ANALYZED));
			} else if (r.getRType().equalsIgnoreCase("ja_kun")) {
				document.add(new Field(KanjiDictionaryFields.KUNYOMI.name(), r.getValue(), Store.YES, Index.NOT_ANALYZED));
			}
		}
	}
}
