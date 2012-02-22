package net.makimono.model;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Before;
import org.junit.Test;

public class SerializationTest {

	private DictionaryEntry entry;

	@Before
	public void setUp() throws Exception {
		entry = new DictionaryEntry();
		entry.setId(1153520);
		entry.getExpressions().add("日本語");
		entry.getReadings().add("にほんご");
		entry.addReadingRestriction(0, 0);

		Sense sense = new Sense();
		sense.getPartsOfSpeech().add(PartOfSpeech.JMdict_adj_i);
		sense.getPartsOfSpeech().add(PartOfSpeech.JMdict_adj_no);

		sense.getDialects().add(Dialect.JMdict_kyb);
		sense.getDialects().add(Dialect.JMdict_osb);

		sense.getMiscellaneous().add(Miscellaneous.JMdict_fam);
		sense.getMiscellaneous().add(Miscellaneous.JMdict_derog);

		sense.getFieldsOfApplication().add(FieldOfApplication.JMdict_Buddh);

		entry.getSenses().add(sense);

		sense.getGlosses().add(createGloss("(hard) candy", Language.en));
		sense.getGlosses().add(createGloss("toffee", Language.en));
		sense.getGlosses().add(createGloss("bonbon", Language.fr));
		sense.getGlosses().add(createGloss("sucrerie", Language.fr));
		sense.getGlosses().add(createGloss("Bonbon", Language.de));
		sense.getGlosses().add(createGloss("Verlockung", Language.de));
	}

	private Gloss createGloss(String value, Language language) {
		Gloss gloss = new Gloss();
		gloss.setLanguage(language);
		gloss.setValue(value);
		return gloss;
	}

	private static byte[] serializeToByteArray(DictionaryEntry entry) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		DictionaryEntry.writeEntry(oos, entry);
		oos.close();
		return baos.toByteArray();
	}

	private DictionaryEntry deserializeFromByteArray(byte[] bytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		return DictionaryEntry.readEntry(ois);
	}

	@Test
	public void testSerialization() throws Exception {
		byte[] bytes = serializeToByteArray(entry);
		DictionaryEntry newEntry = deserializeFromByteArray(bytes);

		assertNotNull(newEntry);
		assertEquals(entry.getId(), newEntry.getId());
		assertEquals(entry.getExpressions().size(), newEntry.getExpressions().size());
		assertEquals(entry.getExpressions(), newEntry.getExpressions());
		assertEquals(entry.getReadings(), newEntry.getReadings());
		assertEquals(entry.getSenses().size(), newEntry.getSenses().size());

		Sense sense = entry.getSenses().get(0);
		Sense newSense = newEntry.getSenses().get(0);
		assertEquals(sense.getPartsOfSpeech(), newSense.getPartsOfSpeech());
		assertEquals(sense.getDialects(), newSense.getDialects());
		assertEquals(sense.getMiscellaneous(), newSense.getMiscellaneous());
		assertEquals(sense.getFieldsOfApplication(), newSense.getFieldsOfApplication());

		assertEquals(entry, newEntry);
	}

}
