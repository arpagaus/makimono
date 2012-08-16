package net.makimono.dictionary.model;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class KnajiEntryTest {

	private static final List<Language> ALL_LANGUAGES = Arrays.asList(Language.values());

	@Test
	public void testGetMeaningSummary() throws Exception {
		KanjiEntry entry = new KanjiEntry();
		assertEquals("", entry.getMeaningSummary(ALL_LANGUAGES));

		entry.getMeanings().add(new Meaning("car", Language.en));
		entry.getMeanings().add(new Meaning("automobile", Language.en));
		assertEquals("car, automobile", entry.getMeaningSummary(ALL_LANGUAGES));

		entry.getMeanings().add(new Meaning("Auto", Language.de));
		assertEquals("car, automobile / Auto", entry.getMeaningSummary(ALL_LANGUAGES));
		assertEquals("Auto / car, automobile", entry.getMeaningSummary(Arrays.asList(Language.de, Language.en)));

		entry.getMeanings().add(new Meaning("automobile", Language.fr));
		assertEquals("car, automobile / Auto / automobile", entry.getMeaningSummary(ALL_LANGUAGES));
	}
}
