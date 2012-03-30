package net.makimono.model;

import java.util.Collections;
import java.util.List;

public enum Language {
	en, de, fr, ru, es, pt;

	public static List<Language> getDefaultLanguage() {
		return Collections.singletonList(en);
	}
}
