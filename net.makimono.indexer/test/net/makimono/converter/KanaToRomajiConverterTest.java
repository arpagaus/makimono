package net.makimono.converter;

import static org.junit.Assert.*;

import org.junit.Test;

public class KanaToRomajiConverterTest {

	private KanaToRomajiConverter converter = new KanaToRomajiConverter();

	@Test
	public void testEmptyHiragana() {
		String romaji = converter.convertKana("");
		assertEquals("", romaji);

		romaji = converter.convertKana(null);
		assertNull(romaji);
	}

	@Test
	public void testHepburnHiragana() {
		String romaji = converter.convertKana("じゅ");
		assertEquals("ju", romaji);

		romaji = converter.convertKana("ろう");
		assertEquals("rou", romaji);

		romaji = converter.convertKana("じゅんいちろう");
		assertEquals("jun'ichirou", romaji);
	}

	@Test
	public void testHepburnKatakana() {
		String romaji = converter.convertKana("ヘボンローマ");
		assertEquals("hebonro^ma", romaji);
	}

	@Test
	public void testMixed() {
		String romaji = converter.convertKana("CDプレーヤー");
		assertEquals("CDpure^ya^", romaji);
	}

	@Test
	public void testSimpleHebpurn() {
		String romaji = converter.convertKanaSimple("プレーヤー");
		assertEquals("pureya", romaji);

		romaji = converter.convertKanaSimple("じゅんいちろう");
		assertEquals("junichirou", romaji);
	}
}
