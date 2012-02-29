package net.makimono.searcher;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.makimono.model.Dialect;
import net.makimono.model.DictionaryEntry;
import net.makimono.model.FieldOfApplication;
import net.makimono.model.Language;
import net.makimono.model.Miscellaneous;
import net.makimono.model.PartOfSpeech;

import org.apache.lucene.queryParser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DictionarySearcherTest {
	private DictionarySearcher searcher;

	@Before
	public void setUp() throws Exception {
		searcher = new DictionarySearcher(new File("res/indexes/dictionary"));
	}

	@After
	public void cleanUp() throws Exception {
		searcher.close();
		searcher = null;
	}

	@Test
	public void searchEmpty() throws Exception {
		List<DictionaryEntry> entries;

		entries = searcher.search("");
		assertTrue(entries.isEmpty());

		entries = searcher.search(null);
		assertTrue(entries.isEmpty());
	}

	@Test(expected = ParseException.class)
	public void testBadQuery() throws Exception {
		searcher.search("*abc");
	}

	@Test
	public void getByDocId() throws Exception {
		List<DictionaryEntry> entries = searcher.search("Einkaufswagen");
		assertEquals(1, entries.size());

		DictionaryEntry entry = entries.get(0);
		searcher.close();
		searcher = new DictionarySearcher(new File("res/indexes/dictionary"));

		DictionaryEntry entryByDocId = searcher.getByDocId(entry.getDocId());

		assertEquals(entry, entryByDocId);
	}

	@Test
	public void searchEnglish() throws Exception {
		List<DictionaryEntry> entries = searcher.search("Himalaya");
		assertEquals(1, entries.size());

		DictionaryEntry entry = entries.get(0);
		assertEquals("ヒマラヤ", entry.getReadings().get(0));
	}

	@Test
	public void searchGerman() throws Exception {
		List<DictionaryEntry> entries = searcher.search("Einkaufswagen");
		assertEquals(1, entries.size());

		DictionaryEntry entry = entries.get(0);
		assertEquals("ショッピングカート", entry.getReadings().get(0));
	}

	@Test
	public void searchFrench() throws Exception {
		List<DictionaryEntry> entries = searcher.search("parapluie");
		assertFalse(entries.isEmpty());

		DictionaryEntry entry = entries.get(0);
		assertEquals("傘", entry.getExpressions().get(0));
	}

	@Test
	public void searchRussian() throws Exception {
		List<DictionaryEntry> entries = searcher.search("горчица");
		assertFalse(entries.isEmpty());

		DictionaryEntry entry = entries.get(0);
		assertEquals("からし", entry.getReadings().get(0));
	}

	@Test
	public void searchKatakana() throws Exception {
		List<DictionaryEntry> entries = searcher.search("ショッピングカート");
		assertEquals(1, entries.size());

		DictionaryEntry entry = entries.get(0);
		assertEquals("Einkaufswagen", entry.getSenses().get(0).getGlossString(Language.de).toString());
	}

	@Test
	public void searchKanji() throws Exception {
		List<DictionaryEntry> entries = searcher.search("向日葵");
		assertEquals(1, entries.size());

		DictionaryEntry entry = entries.get(0);
		assertEquals("sunflower (Helianthus annuus)", entry.getSenses().get(0).getGlossString(Language.en).toString());
	}

	@Test
	public void testPartOfSpeech() throws Exception {
		DictionaryEntry entry = searchUniqueEntry("うみのいえ");
		assertEquals(1976570, entry.getId());
		assertEquals(1, entry.getSenses().get(0).getPartsOfSpeech().size());
		assertEquals(PartOfSpeech.JMdict_n, entry.getSenses().get(0).getPartsOfSpeech().iterator().next());
	}

	@Test
	public void testMiscellaneous() throws Exception {
		DictionaryEntry entry = searchUniqueEntry("明かん");
		assertEquals(1000230, entry.getId());
		assertEquals(1, entry.getSenses().get(0).getMiscellaneous().size());
		assertEquals(Miscellaneous.JMdict_uk, entry.getSenses().get(0).getMiscellaneous().iterator().next());
	}

	@Test
	public void testFieldOfApplication() throws Exception {
		DictionaryEntry entry = searchUniqueEntry("アーカイバー");
		assertEquals(1013370, entry.getId());
		assertEquals(1, entry.getSenses().get(0).getFieldsOfApplication().size());
		assertEquals(FieldOfApplication.JMdict_comp, entry.getSenses().get(0).getFieldsOfApplication().iterator().next());
	}

	@Test
	public void testDialect() throws Exception {
		DictionaryEntry entry = searchUniqueEntry("明かん");
		assertEquals(1000230, entry.getId());
		assertEquals(1, entry.getSenses().get(0).getDialects().size());
		assertEquals(Dialect.JMdict_ksb, entry.getSenses().get(0).getDialects().iterator().next());
	}

	private DictionaryEntry searchUniqueEntry(String queryString) throws IOException, ParseException {
		List<DictionaryEntry> entries = searcher.search(queryString);
		assertEquals(1, entries.size());
		return entries.get(0);
	}

	@Test
	public void suggestEmpty() throws Exception {
		Set<String> suggestions = searcher.suggest(null);
		assertTrue(suggestions.isEmpty());

		suggestions = searcher.suggest("");
		assertTrue(suggestions.isEmpty());
	}

	@Test
	public void suggestMinLength() throws Exception {
		Set<String> suggestions = searcher.suggest("ho");
		assertTrue(suggestions.isEmpty());
		suggestions = searcher.suggest("hot");
		assertFalse(suggestions.isEmpty());

		suggestions = searcher.suggest("に");
		assertTrue(suggestions.isEmpty());
		suggestions = searcher.suggest("にほ");
		assertFalse(suggestions.isEmpty());

		suggestions = searcher.suggest("本");
		assertFalse(suggestions.isEmpty());
	}

	@Test
	public void suggestEnglish() throws Exception {
		Set<String> suggestions = searcher.suggest("motorboa");
		assertEquals(1, suggestions.size());
		assertEquals("motorboat", suggestions.iterator().next());
	}

	@Test
	public void suggestRussian() throws Exception {
		Set<String> suggestions = searcher.suggest("горч");
		assertEquals(1, suggestions.size());
		assertEquals("горчица", suggestions.iterator().next());
	}

	@Test
	public void suggestExpression() throws Exception {
		Set<String> suggestions = searcher.suggest("時差ぼ");
		assertEquals(1, suggestions.size());
		assertEquals("時差ぼけ", suggestions.iterator().next());
	}

	@Test
	public void suggestHiragana() throws Exception {
		Set<String> suggestions = searcher.suggest("じさぼ");
		assertEquals(1, suggestions.size());
		assertEquals("じさぼけ", suggestions.iterator().next());
	}

	@Test
	public void suggestKatakana() throws Exception {
		Set<String> suggestions = searcher.suggest("アップルパ");
		assertEquals(1, suggestions.size());
		assertEquals("アップルパイ", suggestions.iterator().next());
	}

	@Test
	public void testOnlyEnglish() throws Exception {
		assertFalse(searcher.search("Abfalleimer").isEmpty());
		searcher.setLanguages(Arrays.asList(Language.en));
		assertTrue(searcher.search("Abfalleimer").isEmpty());
	}

	@Test
	public void testReadingRestriction() throws Exception {
		DictionaryEntry entry = searchUniqueEntry("打付ける");

		assertEquals(Arrays.asList("ぶつける", "ぶっつける", "うちつける", "ぶちつける"), entry.getReadings("打付ける"));
		assertEquals(Arrays.asList("うちつける", "ぶちつける"), entry.getReadings("打ち付ける"));
		assertEquals(Arrays.asList("ぶっつける"), entry.getReadings("打っ付ける"));
	}
}