package net.makimono.dictionary.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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
		return sentences.get(Language.ja);
	}

	@Override
	public String getReadingSummary() {
		return null;
	}

	@Override
	public String getMeaningSummary(List<Language> languages) {
		String[] meanings = new String[languages.size()];
		for (int i = 0; i < meanings.length; i++) {
			meanings[i] = sentences.get(languages.get(i));
		}
		return StringUtils.join(meanings, "\n");
	}

	@Override
	public String toString() {
		return "ExampleEntry [sentences=" + sentences + "]";
	}
}
