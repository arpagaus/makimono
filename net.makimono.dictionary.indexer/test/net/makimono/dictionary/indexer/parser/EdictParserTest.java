package net.makimono.dictionary.indexer.parser;

import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import net.makimono.dictionary.indexer.parser.EdictParser;
import net.makimono.dictionary.model.DictionaryEntry;
import net.makimono.dictionary.model.FieldOfApplication;
import net.makimono.dictionary.model.Language;
import net.makimono.dictionary.model.Meaning;
import net.makimono.dictionary.model.Miscellaneous;
import net.makimono.dictionary.model.PartOfSpeech;
import net.makimono.dictionary.model.Sense;

import org.junit.Test;

public class EdictParserTest {
	private static final String TEST_LINE = "出入口 [でいりぐち] /(n) entrada y salida/(P)/";
	private static final String TEST_LINE_NO_KANJI = "スペイン /(n) España (eng: Spain)/";

	private EdictParser parser = new EdictParser(Language.es);

	@Test
	public void testExpression() {
		DictionaryEntry entry = parser.parseLine(TEST_LINE);
		assertEquals("出入口", entry.getExpressions().get(0));

		entry = parser.parseLine(TEST_LINE_NO_KANJI);
		assertTrue(entry.getExpressions().isEmpty());
	}

	@Test
	public void testReading() {
		DictionaryEntry entry = parser.parseLine(TEST_LINE);
		assertEquals("でいりぐち", entry.getReadings().get(0));

		entry = parser.parseLine(TEST_LINE_NO_KANJI);
		assertEquals("スペイン", entry.getReadings().get(0));
	}

	@Test
	public void testPartsOfSpeech() {
		DictionaryEntry entry = parser.parseLine("樹立 [じゅりつ] /(n,vs) establecimiento/fundación/(P)/");
		TreeSet<PartOfSpeech> partsOfSpeech = entry.getSenses().get(0).getPartsOfSpeech();
		assertTrue(partsOfSpeech.contains(PartOfSpeech.JMdict_n));
		assertTrue(partsOfSpeech.contains(PartOfSpeech.JMdict_vs));
	}

	@Test
	public void testMeaning() {
		final List<Language> languages = Collections.singletonList(Language.es);

		DictionaryEntry entry = parser.parseLine(TEST_LINE);
		assertEquals("entrada y salida", entry.getMeaningSummary(languages));

		entry = parser.parseLine(TEST_LINE_NO_KANJI);
		assertEquals("España (eng: Spain)", entry.getMeaningSummary(languages));

		entry = parser.parseLine("スリップ /(n,vs) talón/recibo/comprobante/factura/vale/(P)/");
		final ArrayList<Meaning> meanings = entry.getSenses().get(0).getMeanings();
		assertTrue(meanings.contains(new Meaning("talón", Language.es)));
		assertTrue(meanings.contains(new Meaning("recibo", Language.es)));
		assertTrue(meanings.contains(new Meaning("comprobante", Language.es)));
		assertTrue(meanings.contains(new Meaning("factura", Language.es)));
		assertTrue(meanings.contains(new Meaning("vale", Language.es)));
		assertEquals(5, meanings.size());
	}

	@Test
	public void testPartOfSpeech() {
		DictionaryEntry entry = parser.parseLine("お喋り [おしゃべり] /(n,vs,adj) parlanchín/locuaz/(P)/");
		Sense sense = entry.getSenses().get(0);
		assertTrue(sense.getPartsOfSpeech().contains(PartOfSpeech.JMdict_n));
		assertTrue(sense.getPartsOfSpeech().contains(PartOfSpeech.JMdict_vs));

		assertEquals("[parlanchín, locuaz]", sense.getMeanings().toString());
	}

	@Test
	public void testMiscellaneous() {
		DictionaryEntry entry = parser.parseLine("為れる [される] /(v1) (uk) forma pasiva u honorífica del verbo \"suru\"/");
		Sense sense = entry.getSenses().get(0);
		assertTrue(sense.getMiscellaneous().contains(Miscellaneous.JMdict_uk));

		assertEquals("forma pasiva u honorífica del verbo \"suru\"", sense.getMeanings().get(0).getValue());
	}

	@Test
	public void testFieldsOfApplication() {
		DictionaryEntry entry = parser.parseLine("無限遠点 [むげんえんてん] /(n) (comp) infinito/");
		Sense sense = entry.getSenses().get(0);
		assertTrue(sense.getFieldsOfApplication().contains(FieldOfApplication.JMdict_comp));

		assertEquals("infinito", sense.getMeanings().get(0).getValue());
	}

	@Test
	public void testEmptyLine() {
		DictionaryEntry entry = parser.parseLine(null);
		assertNull(entry);

		entry = parser.parseLine("");
		assertNull(entry);
	}

	@Test
	public void testParse() throws Exception {
		Reader reader = new StringReader(TEST_LINE + "\n" + TEST_LINE_NO_KANJI);
		Map<String, DictionaryEntry> entries = parser.parse(reader);

		assertEquals(2, entries.size());
		assertTrue(entries.keySet().contains("出入口"));
		assertTrue(entries.keySet().contains("スペイン"));
	}
}
