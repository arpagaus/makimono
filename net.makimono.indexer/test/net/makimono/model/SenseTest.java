package net.makimono.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class SenseTest {

	private Sense createSense() {
		Sense sense = new Sense();

		Meaning meaningGrapefruit = new Meaning();
		meaningGrapefruit.setLanguage(Language.en);
		meaningGrapefruit.setValue("grapefruit");

		Meaning meaningOrange = new Meaning();
		meaningOrange.setLanguage(Language.en);
		meaningOrange.setValue("orange");

		Meaning meaningPampelmuse = new Meaning();
		meaningPampelmuse.setLanguage(Language.de);
		meaningPampelmuse.setValue("orange");

		sense.getMeanings().add(meaningGrapefruit);
		sense.getMeanings().add(meaningOrange);
		sense.getMeanings().add(meaningPampelmuse);

		sense.getPartsOfSpeech().add(PartOfSpeech.JMdict_n);
		sense.getPartsOfSpeech().add(PartOfSpeech.JMdict_n_suf);

		sense.getMiscellaneous().add(Miscellaneous.JMdict_pol);

		sense.getFieldsOfApplication().add(FieldOfApplication.JMdict_geom);

		sense.getDialects().add(Dialect.JMdict_kyu);

		return sense;
	}

	@Test
	public void getMeaningString() throws Exception {
		Sense sense = createSense();
		CharSequence meaningString = Meaning.getMeaningString(Language.en, sense.getMeanings());
		assertEquals("grapefruit, orange", meaningString.toString());
	}

	@Test
	public void getAdditionalInfo() throws Exception {
		Sense sense = createSense();
		ArrayList<String> additionalInfo = sense.getAdditionalInfo();
		assertEquals(Arrays.asList(PartOfSpeech.JMdict_n.name(), PartOfSpeech.JMdict_n_suf.name(), Miscellaneous.JMdict_pol.name(), FieldOfApplication.JMdict_geom.name(), Dialect.JMdict_kyu.name()),
				additionalInfo);
	}
}
