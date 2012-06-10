package net.makimono.dictionary.converter;

import static org.junit.Assert.*;

import net.makimono.dictionary.converter.KanaConverter;

import org.junit.Test;

public class KanaConverterTest {

	private KanaConverter converter = new KanaConverter();

	@Test
	public void testEmptyHiragana() {
		String romaji = converter.convertKanaToRomaji("");
		assertEquals("", romaji);

		romaji = converter.convertKanaToRomaji(null);
		assertNull(romaji);
	}

	@Test
	public void testHepburnHiragana() {
		String romaji = converter.convertKanaToRomaji("じゅ");
		assertEquals("ju", romaji);

		romaji = converter.convertKanaToRomaji("ろう");
		assertEquals("rou", romaji);

		romaji = converter.convertKanaToRomaji("じゅんいちろう");
		assertEquals("jun'ichirou", romaji);
	}

	@Test
	public void testHepburnKatakana() {
		String romaji = converter.convertKanaToRomaji("ヘボンローマ");
		assertEquals("hebonro-ma", romaji);
	}

	@Test
	public void testMixed() {
		String romaji = converter.convertKanaToRomaji("CDプレーヤー");
		assertEquals("CDpure-ya-", romaji);
	}

	@Test
	public void testSimpleHebpurn() {
		String romaji = converter.convertKanaToRomajiSimple("プレーヤー");
		assertEquals("pureya", romaji);

		romaji = converter.convertKanaToRomajiSimple("じゅんいちろう");
		assertEquals("junichirou", romaji);
	}

	@Test
	public void testKatakanaToHiragana() {
		String hiragana = converter.convertKatakanaToHiragana("ホン");
		assertEquals("ほん", hiragana);

		hiragana = converter.convertKatakanaToHiragana("ヘボンローマ");
		assertEquals("へぼんろーま", hiragana);
	}
}
