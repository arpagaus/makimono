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
		assertEquals("Das ist ein Testsatz", entry.getSentence(Language.de));
		assertEquals("これは文です。", entry.getSentence(Language.ja));
	}

	@Test
	public void testSearch() throws Exception {
		List<ExampleEntry> entries = searcher.search("Hände sauber");
		assertFalse(entries.isEmpty());

		ExampleEntry entry = entries.get(0);
		assertEquals("君の手は清潔ですか。", entry.getSentence(Language.ja));
		assertEquals("Sind deine Hände sauber?", entry.getSentence(Language.de));
		assertEquals("¿Tienes las manos limpias?", entry.getSentence(Language.es));
		assertEquals("Are your hands free of dirt?", entry.getSentence(Language.en));
		assertEquals("Tes mains sont-elles propres ?", entry.getSentence(Language.fr));
	}
}
