package net.makimono.dictionary.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExampleEntry implements Entry {

	private Map<Language, String> sentences = new HashMap<Language, String>(Language.values().length);

	public String getSentence(Language language) {
		return sentences.get(language);
	}

	public void putSentence(Language language, String sentence) {
		sentences.put(language, sentence);
	}

	@Override
	public String getExpression() {
		return null;
	}

	@Override
	public String getReadingSummary() {
		return null;
	}

	@Override
	public String getMeaningSummary(List<Language> languages) {
		return null;
	}

	@Override
	public String toString() {
		return "ExampleEntry [sentences=" + sentences + "]";
	}
}
