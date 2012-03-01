package net.makimono.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class SenseTest {

	private Sense createSense() {
		Sense sense = new Sense();

		Gloss glossGrapefruit = new Gloss();
		glossGrapefruit.setLanguage(Language.en);
		glossGrapefruit.setValue("grapefruit");

		Gloss glossOrange = new Gloss();
		glossOrange.setLanguage(Language.en);
		glossOrange.setValue("orange");

		Gloss glossPampelmuse = new Gloss();
		glossPampelmuse.setLanguage(Language.de);
		glossPampelmuse.setValue("orange");

		sense.getGlosses().add(glossGrapefruit);
		sense.getGlosses().add(glossOrange);
		sense.getGlosses().add(glossPampelmuse);

		sense.getPartsOfSpeech().add(PartOfSpeech.JMdict_n);
		sense.getPartsOfSpeech().add(PartOfSpeech.JMdict_n_suf);

		sense.getMiscellaneous().add(Miscellaneous.JMdict_pol);

		sense.getFieldsOfApplication().add(FieldOfApplication.JMdict_geom);

		sense.getDialects().add(Dialect.JMdict_kyu);

		return sense;
	}

	@Test
	public void getGlossString() throws Exception {
		Sense sense = createSense();
		CharSequence glossString = Gloss.getGlossString(Language.en, sense.getGlosses());
		assertEquals("grapefruit, orange", glossString.toString());
	}

	@Test
	public void getAdditionalInfo() throws Exception {
		Sense sense = createSense();
		ArrayList<String> additionalInfo = sense.getAdditionalInfo();
		assertEquals(Arrays.asList(PartOfSpeech.JMdict_n.name(), PartOfSpeech.JMdict_n_suf.name(), Miscellaneous.JMdict_pol.name(), FieldOfApplication.JMdict_geom.name(), Dialect.JMdict_kyu.name()),
				additionalInfo);
	}
}
