package net.makimono.dictionary.model;

import java.util.Collections;
import java.util.List;

public enum Language {
	en, de, fr, ru, es, pt, ja;

	public static List<Language> getDefaultLanguage() {
		return Collections.singletonList(en);
	}
}
