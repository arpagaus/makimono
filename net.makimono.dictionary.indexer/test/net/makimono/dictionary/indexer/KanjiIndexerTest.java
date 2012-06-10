package net.makimono.dictionary.indexer;

import static org.junit.Assert.assertEquals;

import net.makimono.dictionary.indexer.KanjiIndexer;

import org.junit.Test;

public class KanjiIndexerTest {

	@Test
	public void cleanReadingString() {
		KanjiIndexer indexer = new KanjiIndexer();

		assertEquals("つく", indexer.cleanReadingString("つく.る"));
		assertEquals("つか", indexer.cleanReadingString("-つか.い"));
	}
}
