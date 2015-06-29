package net.makimono.dictionary.activity;

import java.util.EnumSet;
import java.util.Locale;

public enum PreferenceEnum {

	USER_LEARNED_NAVIGATION_DRAWER, INDEX_FILES_VERSION, INDEX_FILES_VERSION_RESET, CLEAR_SEARCH_HISTORY, ROMAJI_SEARCH, LANGUAGE_EN, LANGUAGE_DE, LANGUAGE_FR, LANGUAGE_RU, LANGUAGE_ES, LANGUAGE_PT;

	public String key() {
		return name().toLowerCase(Locale.US);
	}

	public static EnumSet<PreferenceEnum> getLanguageEnums() {
		return EnumSet.range(LANGUAGE_EN, LANGUAGE_PT);
	}
}
