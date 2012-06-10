package net.makimono.dictionary.indexer;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import net.makimono.dictionary.indexer.DictionaryIndexer;
import net.makimono.dictionary.model.DictionaryEntry;

import org.junit.Test;

import au.edu.monash.csse.jmdict.model.KEle;
import au.edu.monash.csse.jmdict.model.REle;
import au.edu.monash.csse.jmdict.model.ReRestr;

public class DictionaryIndexerTest {

	@Test
	public void testBoost() {
		DictionaryIndexer indexer = new DictionaryIndexer();

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

	@Test
	public void transformReadingRestrictedEntry() throws Exception {
		au.edu.monash.csse.jmdict.model.Entry jmdictEntry = new au.edu.monash.csse.jmdict.model.Entry();
		jmdictEntry.setEntSeq("42");

		jmdictEntry.getKEle().add(createKEle("罅"));
		jmdictEntry.getKEle().add(createKEle("皹"));
		jmdictEntry.getKEle().add(createKEle("皸"));

		jmdictEntry.getREle().add(createREel("ひび"));
		jmdictEntry.getREle().add(createREel("あかぎれ", "皹", "皸"));

		DictionaryIndexer indexer = new DictionaryIndexer();
		DictionaryEntry entry = indexer.transformEntry(jmdictEntry);

		assertEquals(Arrays.asList("ひび"), entry.getReadings("罅"));
		assertEquals(Arrays.asList("ひび", "あかぎれ"), entry.getReadings("皹"));
		assertEquals(Arrays.asList("ひび", "あかぎれ"), entry.getReadings("皸"));
	}

	private REle createREel(String s, String... restrictions) {
		REle rEle = new REle();
		rEle.setReb(s);
		for (String r : restrictions) {
			ReRestr reRestr = new ReRestr();
			reRestr.setvalue(r);
			rEle.getReRestr().add(reRestr);
		}
		return rEle;
	}

	private KEle createKEle(String s) {
		KEle kEle = new KEle();
		kEle.setKeb(s);
		return kEle;
	}

}
