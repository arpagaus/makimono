package net.makimono.dictionary.searcher;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.makimono.dictionary.indexer.KanjiIndexer;
import net.makimono.dictionary.indexer.parser.KradfileParser;
import net.makimono.dictionary.model.KanjiEntry;
import net.makimono.dictionary.model.Language;
import net.makimono.dictionary.model.Meaning;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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

	@Test(expected = ArithmeticException.class)
	public void moduloZero() {
		@SuppressWarnings("unused")
		int a = 3 % 0;
	}

	@Test
	public void getKanjiEntryForDocument() throws Exception {
		Document document = new KanjiIndexer(new Properties()).createDocument(testKanji);
		KanjiEntry entry = searcher.getKanjiEntryForDocument(document);

		assertEquals("語", entry.getLiteral());
		assertEquals(0x8a9e, entry.getCodePoint());
		assertEquals(2, entry.getGrade());
		assertEquals(301, entry.getFrequency());
		assertEquals(4, entry.getJlpt());

		assertEquals(14, entry.getStrokeCount());

		assertEquals(149, entry.getRadical());
		assertEquals('言', entry.getRadicalKanji());
		assertEquals("ことば", entry.getRadicalKana());

		assertEquals(asArrayList("かた.る", "かた.らう"), entry.getKunYomi());
		assertEquals(asArrayList("ゴ"), entry.getOnYomi());

		assertEquals(asArrayList("や"), entry.getNanori());
		assertEquals(asArrayList("yu3", "yu4"), entry.getPinyin());
		assertEquals(asArrayList("어"), entry.getHangul());

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
		assertEquals(asArrayList("zai1"), entry.getPinyin());

		assertEquals(6, entry.getStrokeCount());
		assertEquals(62, entry.getRadical());
		assertEquals('戈', entry.getRadicalKanji());
		assertEquals("かのほこ", entry.getRadicalKana());

		assertEquals("[ノ, 十, 土, 弋]", entry.getRadicals().toString());
	}

	@Test
	public void getStrokePaths() throws Exception {
		KanjiEntry entry = searcher.getKanjiEntry("語");
		assertNotNull(entry);

		assertEquals(14, entry.getStrokePaths().size());
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

		entries = searcher.getKanjiEntries("日日");
		assertEquals(1, entries.size());
	}

	@Test
	public void searchExpression() throws Exception {
		List<KanjiEntry> entries = searcher.search("お休み");

		assertEquals(1, entries.size());
		assertEquals("休", entries.get(0).getLiteral());
	}

	@Test
	public void findAllRadicals() throws Exception {
		for (short i = 1; i <= 214; i++) {
			KanjiEntry entry = new KanjiEntry();
			entry.setRadical(i);

			KanjiEntry radical = searcher.getKanjiEntry(String.valueOf(entry.getRadicalKanji()));
			assertNotNull("Radical " + i, radical);
			assertEquals("Radical " + i, entry.getRadicalKanji(), radical.getLiteral().charAt(0));
			assertFalse(radical.getMeanings().isEmpty());

			assertNotNull("Radical " + i, entry.getRadicalKana());
			assertTrue("Radical " + i, entry.getRadicalKana().length() > 0);
		}
	}

	@Test
	public void findAllKradfileRadicals() throws Exception {
		Properties properties = new Properties();
		InputStream input = new FileInputStream("../net.makimono.dictionary/assets/radicals.xml");
		properties.loadFromXML(input);
		input.close();

		for (Object row : properties.values()) {
			String[] radicals = row.toString().split(";");
			for (String radical : radicals) {
				KanjiEntry entry = searcher.getKanjiEntry(radical);
				assertNotNull("Missing radical " + radical, entry);
				assertNotNull(entry.getLiteral());
			}
		}
	}

	@Test
	public void searchKunYomi() throws Exception {
		List<KanjiEntry> entries = searcher.search("みなみ");
		assertEquals(1, entries.size());
		assertEquals("南", entries.get(0).getLiteral());

		entries = searcher.search("minami");
		assertEquals(1, entries.size());
		assertEquals("南", entries.get(0).getLiteral());

		searcher.setRomajiSearchEnabled(false);
		entries = searcher.search("minami");
		assertEquals(0, entries.size());
	}

	@Test
	public void searchKunYomi_WithoutPunctiation() throws Exception {
		List<KanjiEntry> entries = searcher.search("つくる");
		assertTrue(entries.isEmpty());

		entries = searcher.search("つく.る");
		assertTrue(entries.isEmpty());

		Set<String> kanjis = new TreeSet<String>();
		entries = searcher.search("つく");
		for (KanjiEntry e : entries) {
			if (e.getKunYomi().contains("つく.る")) {
				kanjis.add(e.getLiteral());
			}
		}

		assertEquals("作, 做, 刅, 創, 戧, 為, 爲, 造", StringUtils.join(kanjis, ", "));
	}

	@Test
	public void searchOnYomi() throws Exception {
		List<KanjiEntry> entries = searcher.search("エイ");
		assertFalse(entries.isEmpty());
		for (KanjiEntry entry : entries) {
			assertTrue(entry.getOnYomi().contains("エイ"));
		}

		entries = searcher.search("ほん");
		assertFalse(entries.isEmpty());
	}

	@Test
	public void searchSpanish() throws Exception {
		List<KanjiEntry> entries = searcher.search("contador de cosas planas");
		assertFalse(entries.isEmpty());
		assertEquals("葉", entries.get(0).getLiteral());

		entries = searcher.search("follaje");
		assertEquals(1, entries.size());
		assertEquals("葉", entries.get(0).getLiteral());
	}

	@Test
	public void searchByRadicals() throws Exception {
		List<KanjiEntry> entries = searcher.searchByRadicals(Arrays.asList("｜", "ノ", "二", "丶", "廾", "井"), null, null);
		assertEquals(1, entries.size());
		assertEquals(searcher.getKanjiEntry("丼"), entries.get(0));

		entries = searcher.searchByRadicals(Arrays.asList("ハ", "二", "已"), 21, null);
		assertEquals(1, entries.size());
		assertEquals("饌", entries.get(0).getLiteral());
	}

	@Test
	public void getSelectableRadicals() throws Exception {
		Set<String> radicals = searcher.getSelectableRadicals(Arrays.asList("龠"), null, 17);
		assertTrue(radicals.containsAll(Arrays.asList("�", "冊", "一", "龠", "口")));

		radicals = searcher.getSelectableRadicals(Collections.<String> emptySet(), 1, 33);
		assertNull(radicals);
	}

	@Ignore
	@Test
	public void buildRadicalSearchFile() throws Exception {
		Map<String, Set<String>> map = new KradfileParser(new File("res/kradfile-u.gz")).getKanjiRadicals();
		Set<String> radicals = new HashSet<String>();
		for (Set<String> set : map.values()) {
			radicals.addAll(set);
		}

		Properties properties = new Properties();
		Set<String> missingRadicals = new HashSet<String>();

		for (String radical : radicals) {
			KanjiEntry entry = searcher.getKanjiEntry(radical);
			if (entry == null) {
				missingRadicals.add(radical);
				continue;
			}
			String strokes = String.valueOf(entry.getStrokeCount());
			Object existingRadicals = properties.get(strokes);
			if (existingRadicals == null) {
				properties.put(strokes, radical);
			} else {
				properties.put(strokes, existingRadicals.toString() + ";" + radical);
			}
		}

		FileOutputStream fileOutputStream = new FileOutputStream("radicals.xml");
		properties.storeToXML(fileOutputStream, "Stroke count radical mapping", "UTF-8");
		fileOutputStream.close();

		System.out.println(missingRadicals);
		for (String s : missingRadicals) {
			System.out.printf("0x%h", Character.codePointAt(s, 0));
			System.out.println();
		}
	}
}
