package jiten.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class SenseTest {

	@Test
	public void getGlossString() throws Exception {
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

		CharSequence glossString = sense.getGlossString(Language.en);
		assertNotNull(glossString);
		assertEquals("grapefruit, orange", glossString.toString());
	}
}
