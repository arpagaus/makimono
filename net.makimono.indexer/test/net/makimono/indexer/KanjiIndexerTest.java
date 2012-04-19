package net.makimono.indexer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class KanjiIndexerTest {

	@Test
	public void cleanReadingString() {
		KanjiIndexer indexer = new KanjiIndexer();

		assertEquals("つく", indexer.cleanReadingString("つく.る"));
		assertEquals("つか", indexer.cleanReadingString("-つか.い"));
	}
}
