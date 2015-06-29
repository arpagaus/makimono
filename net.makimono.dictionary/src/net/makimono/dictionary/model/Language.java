package net.makimono.dictionary.model;

public enum Language {
	en, de, fr, ru, es, pt, ja;

	public static Language getDefaultLanguage() {
		return en;
	}
}
