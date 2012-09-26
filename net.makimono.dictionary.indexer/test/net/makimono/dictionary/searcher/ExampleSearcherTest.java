package net.makimono.dictionary.searcher;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import net.makimono.dictionary.model.ExampleEntry;
import net.makimono.dictionary.model.Language;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ExampleSearcherTest {

	private ExampleSearcher searcher;

	@Before
	public void setUp() throws Exception {
		searcher = new ExampleSearcher(new File("res/indexes/example"));
	}

	@After
	public void cleanUp() throws Exception {
		searcher.close();
		searcher = null;
	}

	@Test
	public void testEntryForDocument() throws Exception {
		Document document = new Document();
		document.add(new Field(ExampleFieldName.SENTENCE_DE.name(), "Das ist ein Testsatz", Store.YES, Index.ANALYZED));
		document.add(new Field(ExampleFieldName.SENTENCE_JA.name(), "これは文です。", Store.YES, Index.ANALYZED));

		ExampleEntry entry = searcher.getEntryForDocument(document);
		assertEquals("Das ist ein Testsatz", entry.getMeaning(Language.de).getValue());
		assertEquals("これは文です。", entry.getJapaneseMeaning().getValue());
	}

	@Test
	public void testSearch() throws Exception {
		List<ExampleEntry> entries = searcher.search("Hände sauber");
		assertFalse(entries.isEmpty());

		ExampleEntry entry = entries.get(0);
		assertEquals("君の手は清潔ですか。", entry.getMeaning(Language.ja).getValue());
		assertEquals("Sind deine Hände sauber?", entry.getMeaning(Language.de).getValue());
		assertEquals("¿Tenéis las manos limpias?", entry.getMeaning(Language.es).getValue());
		assertEquals("Are your hands free of dirt?", entry.getMeaning(Language.en).getValue());
		assertEquals("Tes mains sont propres ?", entry.getMeaning(Language.fr).getValue());
	}

	@Test
	public void testJapaneseWordSearch() throws Exception {
		List<ExampleEntry> entries = searcher.search("語");
		assertFalse(entries.isEmpty());
		for (ExampleEntry entry : entries) {
			assertTrue("Meaning: '" + entry.getJapaneseMeaning() + "'", entry.getJapaneseMeaning().getValue().contains("語"));
		}
	}
}
