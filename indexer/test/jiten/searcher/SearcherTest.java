package jiten.searcher;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import jiten.model.Dialect;
import jiten.model.Entry;
import jiten.model.FieldOfApplication;
import jiten.model.Miscellaneous;
import jiten.model.PartOfSpeech;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
		assertEquals(1103970, entry.getId());
	}

	@Test
	public void searchGerman() throws Exception {
		List<Entry> entries = searcher.search("Einkaufswagen");
		assertEquals(1, entries.size());

		Entry entry = entries.get(0);
		assertEquals(1062770, entry.getId());
	}

	@Test
	public void searchFrench() throws Exception {
		List<Entry> entries = searcher.search("parapluie");
		assertFalse(entries.isEmpty());

		Entry entry = entries.get(0);
		assertEquals(1301940, entry.getId());
	}

	@Test
	public void searchRussian() throws Exception {
		List<Entry> entries = searcher.search("горчица");
		assertFalse(entries.isEmpty());

		Entry entry = entries.get(0);
		assertEquals(1202390, entry.getId());
	}

	@Test
	public void searchKatakana() throws Exception {
		List<Entry> entries = searcher.search("ショッピングカート");
		assertEquals(1, entries.size());

		Entry entry = entries.get(0);
		assertEquals(1062770, entry.getId());
	}

	@Test
	public void searchKanji() throws Exception {
		List<Entry> entries = searcher.search("向日葵");
		assertEquals(1, entries.size());

		Entry entry = entries.get(0);
		assertEquals(1277290, entry.getId());
	}

	private Entry searchUniqueEntry(String queryString) throws IOException, ParseException {
		List<Entry> entries = searcher.search(queryString);
		assertEquals(1, entries.size());
		return entries.get(0);
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

	@Test
	@Ignore
	public void testBoost() throws Exception {
		List<Entry> entries = searcher.search("benutzen");
		for (Entry e : entries) {
			System.out.println(e.getId());
		}
	}
}
