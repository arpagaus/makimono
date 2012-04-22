package net.makimono.indexer.parser;

import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.makimono.model.DictionaryEntry;
import net.makimono.model.Language;
import net.makimono.model.Meaning;

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
