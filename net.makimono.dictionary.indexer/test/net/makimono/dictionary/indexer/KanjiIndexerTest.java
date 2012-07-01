package net.makimono.dictionary.indexer;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

public class KanjiIndexerTest {

	@Test
	public void cleanReadingString() {
		KanjiIndexer indexer = new KanjiIndexer(new Properties());

		assertEquals("つく", indexer.cleanReadingString("つく.る"));
		assertEquals("つか", indexer.cleanReadingString("-つか.い"));
	}
}
