package jiten.searcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import jiten.model.Entry;

import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;

public class SearcherTest {
	Searcher searcher;

	@Before
	public void setUp() throws Exception {
		searcher = new Searcher(FSDirectory.open((new File("res/index"))));
	}

	@Test
	public void searchEmpty() throws Exception {
		List<Entry> entries;

		entries = searcher.search("");
		assertTrue(entries.isEmpty());

		entries = searcher.search(null);
		assertTrue(entries.isEmpty());
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

	@Test
	public void searchHiragana() throws Exception {
		List<Entry> entries = searcher.search("うみのいえ");
		assertEquals(1, entries.size());

		Entry entry = entries.get(0);
		assertEquals(1976570, entry.getId());
	}
}
