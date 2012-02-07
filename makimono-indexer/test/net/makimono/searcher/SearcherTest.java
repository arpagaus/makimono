package net.makimono.searcher;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import net.makimono.model.Dialect;
import net.makimono.model.Entry;
import net.makimono.model.FieldOfApplication;
import net.makimono.model.Language;
import net.makimono.model.Miscellaneous;
import net.makimono.model.PartOfSpeech;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SearcherTest {
	private Searcher searcher;

	@Before
	public void setUp() throws Exception {
		searcher = new Searcher(FSDirectory.open((new File("res/index"))));
	}

	@After
	public void cleanUp() throws Exception {
		searcher.close();
	}

	@Test
	public void searchEmpty() throws Exception {
		List<Entry> entries;

		entries = searcher.search("");
		assertTrue(entries.isEmpty());

		entries = searcher.search(null);
		assertTrue(entries.isEmpty());
	}

	@Test(expected = AlreadyClosedException.class)
	public void testAlreadyClosed() throws Exception {
		UUID uuid = UUID.randomUUID();
		searcher.search(uuid.toString());
		searcher.close();
		searcher.search(uuid.toString());
	}

	@Test(expected = ParseException.class)
	public void testBadQuery() throws Exception {
		searcher.search("*abc");
	}

	@Test
	public void getByDocId() throws Exception {
		List<Entry> entries = searcher.search("Einkaufswagen");
		assertEquals(1, entries.size());

		Entry entry = entries.get(0);
		searcher.close();
		searcher = new Searcher(FSDirectory.open((new File("res/index"))));

		Entry entryByDocId = searcher.getByDocId(entry.getDocId());

		assertEquals(entry, entryByDocId);
	}

	@Test
	public void searchEnglish() throws Exception {
		List<Entry> entries = searcher.search("Himalaya");
		assertEquals(1, entries.size());

		Entry entry = entries.get(0);
		assertEquals("ヒマラヤ", entry.getReadings().get(0));
	}

	@Test
	public void searchGerman() throws Exception {
		List<Entry> entries = searcher.search("Einkaufswagen");
		assertEquals(1, entries.size());

		Entry entry = entries.get(0);
		assertEquals("ショッピングカート", entry.getReadings().get(0));
	}

	@Test
	public void searchFrench() throws Exception {
		List<Entry> entries = searcher.search("parapluie");
		assertFalse(entries.isEmpty());

		Entry entry = entries.get(0);
		assertEquals("傘", entry.getExpressions().get(0));
	}

	@Test
	public void searchRussian() throws Exception {
		List<Entry> entries = searcher.search("горчица");
		assertFalse(entries.isEmpty());

		Entry entry = entries.get(0);
		assertEquals("からし", entry.getReadings().get(0));
	}

	@Test
	public void searchKatakana() throws Exception {
		List<Entry> entries = searcher.search("ショッピングカート");
		assertEquals(1, entries.size());

		Entry entry = entries.get(0);
		assertEquals("Einkaufswagen", entry.getSenses().get(0).getGlossString(Language.de).toString());
	}

	@Test
	public void searchKanji() throws Exception {
		List<Entry> entries = searcher.search("向日葵");
		assertEquals(1, entries.size());

		Entry entry = entries.get(0);
		assertEquals("sunflower (Helianthus annuus)", entry.getSenses().get(0).getGlossString(Language.en).toString());
	}

	@Test
	public void testPartOfSpeech() throws Exception {
		Entry entry = searchUniqueEntry("うみのいえ");
		assertEquals(1976570, entry.getId());
		assertEquals(1, entry.getSenses().get(0).getPartsOfSpeech().size());
		assertEquals(PartOfSpeech.JMdict_n, entry.getSenses().get(0).getPartsOfSpeech().iterator().next());
	}

	@Test
	public void testMiscellaneous() throws Exception {
		Entry entry = searchUniqueEntry("明かん");
		assertEquals(1000230, entry.getId());
		assertEquals(1, entry.getSenses().get(0).getMiscellaneous().size());
		assertEquals(Miscellaneous.JMdict_uk, entry.getSenses().get(0).getMiscellaneous().iterator().next());
	}

	@Test
	public void testFieldOfApplication() throws Exception {
		Entry entry = searchUniqueEntry("アーカイバー");
		assertEquals(1013370, entry.getId());
		assertEquals(1, entry.getSenses().get(0).getFieldsOfApplication().size());
		assertEquals(FieldOfApplication.JMdict_comp, entry.getSenses().get(0).getFieldsOfApplication().iterator().next());
	}

	@Test
	public void testDialect() throws Exception {
		Entry entry = searchUniqueEntry("明かん");
		assertEquals(1000230, entry.getId());
		assertEquals(1, entry.getSenses().get(0).getDialects().size());
		assertEquals(Dialect.JMdict_ksb, entry.getSenses().get(0).getDialects().iterator().next());
	}

	private Entry searchUniqueEntry(String queryString) throws IOException, ParseException {
		List<Entry> entries = searcher.search(queryString);
		assertEquals(1, entries.size());
		return entries.get(0);
	}
}
