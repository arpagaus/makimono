package net.makimono.searcher;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.makimono.indexer.KanjiIndexer;
import net.makimono.model.KanjiEntry;
import net.makimono.model.Language;
import net.makimono.model.Meaning;

import org.apache.lucene.document.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.edu.monash.csse.kanjidic.model.Kanjidic2;

public class KanjiSearcherTest {

	private KanjiSearcher searcher;
	private au.edu.monash.csse.kanjidic.model.Character testKanji;

	@Before
	public void setUp() throws Exception {
		searcher = new KanjiSearcher(new File("res/indexes/kanji"));

		InputStream inputStream = KanjiSearcherTest.class.getClassLoader().getResourceAsStream(KanjiSearcherTest.class.getPackage().getName().replaceAll("\\.", "/") + "/test-kanji.xml");
		Unmarshaller unmarshaller = JAXBContext.newInstance(Kanjidic2.class.getPackage().getName()).createUnmarshaller();
		testKanji = (au.edu.monash.csse.kanjidic.model.Character) unmarshaller.unmarshal(inputStream);
	}

	@After
	public void cleanUp() throws Exception {
		searcher.close();
		searcher = null;
	}

	@Test
	public void getKanjiEntryForDocument() throws Exception {
		Document document = new KanjiIndexer().createDocument(testKanji);
		KanjiEntry entry = searcher.getKanjiEntryForDocument(document);

		assertEquals("語", entry.getLiteral());
		assertEquals(0x8a9e, entry.getCodePoint());
		assertEquals(2, entry.getGrade());
		assertEquals(301, entry.getFrequency());
		assertEquals(4, entry.getJlpt());

		assertEquals(149, entry.getRadical());
		assertEquals(14, entry.getStrokeCount());

		assertEquals(asArrayList("ゴ"), entry.getOnYomi());
		assertEquals(asArrayList("かた.る", "かた.らう"), entry.getKunYomi());

		List<Meaning> meanings = entry.getMeanings();
		assertTrue(meanings.contains(new Meaning("word", Language.en)));
		assertTrue(meanings.contains(new Meaning("speech", Language.en)));
		assertTrue(meanings.contains(new Meaning("language", Language.en)));

		assertTrue(meanings.contains(new Meaning("langage", Language.fr)));
		assertTrue(meanings.contains(new Meaning("mot", Language.fr)));
		assertTrue(meanings.contains(new Meaning("raconter", Language.fr)));

		assertTrue(meanings.contains(new Meaning("palabra", Language.es)));
		assertTrue(meanings.contains(new Meaning("discurso", Language.es)));
		assertTrue(meanings.contains(new Meaning("lenguaje", Language.es)));
		assertTrue(meanings.contains(new Meaning("hablar", Language.es)));
		assertTrue(meanings.contains(new Meaning("conversar", Language.es)));
		assertTrue(meanings.contains(new Meaning("narrar", Language.es)));

		assertTrue(meanings.contains(new Meaning("palavra", Language.pt)));
		assertTrue(meanings.contains(new Meaning("discurso", Language.pt)));
		assertTrue(meanings.contains(new Meaning("língua", Language.pt)));

		assertEquals(15, meanings.size());
	}

	private static ArrayList<String> asArrayList(String... string) {
		return new ArrayList<String>(Arrays.asList(string));
	}

	@Test
	public void getKanjiEntry() throws Exception {
		KanjiEntry entry = searcher.getKanjiEntry("𢦏");
		assertNotNull(entry);

		assertEquals("𢦏", entry.getLiteral());
		assertEquals(0x2298F, entry.getCodePoint());
		assertEquals(62, entry.getRadical());
		assertEquals(6, entry.getStrokeCount());
	}

	@Test
	public void decomposeEmptyString() throws Exception {
		List<KanjiEntry> entries = searcher.getKanjiEntries("");
		assertTrue(entries.isEmpty());

		entries = searcher.getKanjiEntries(null);
		assertTrue(entries.isEmpty());
	}

	@Test
	public void decomposeString() throws Exception {
		List<KanjiEntry> entries = searcher.getKanjiEntries("日本語");

		assertEquals(3, entries.size());
		assertEquals("日", entries.get(0).getLiteral());
		assertEquals("本", entries.get(1).getLiteral());
		assertEquals("語", entries.get(2).getLiteral());
	}
}
