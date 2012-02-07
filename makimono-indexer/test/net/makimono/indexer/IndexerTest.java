package net.makimono.indexer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IndexerTest {

	@Test
	public void testBoost() {
		Indexer indexer = new Indexer();

		assertEquals(100, indexer.getBoost("ichi1"), 0);
		assertEquals(100, indexer.getBoost("spec1"), 0);

		assertEquals(112.5, indexer.getBoost("nf01"), 0);
		assertEquals(100.0, indexer.getBoost("nf12"), 1.5);
		assertEquals(100.0, indexer.getBoost("nf13"), 1.5);
		assertEquals(75, indexer.getBoost("nf37"), 1.5);
		assertEquals(75, indexer.getBoost("nf38"), 1.5);
		assertEquals(88.5, indexer.getBoost("nf25"), 0);
		assertEquals(63.5, indexer.getBoost("nf50"), 0);

		assertEquals(75, indexer.getBoost("ichi2"), 0);
		assertEquals(75, indexer.getBoost("spec2"), 0);
	}

}
