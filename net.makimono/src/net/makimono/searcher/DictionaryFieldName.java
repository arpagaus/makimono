package net.makimono.searcher;

import net.makimono.model.Language;

public enum DictionaryFieldName implements IndexFieldName {
	EXPRESSION, READING, MEANING_EN, MEANING_DE, MEANING_FR, MEANING_RU, MEANING_ANALYZED_EN, MEANING_ANALYZED_DE, MEANING_ANALYZED_FR, MEANING_ANALYZED_RU;

	@Override
	public boolean isMeaning() {
		return name().startsWith("MEANING");
	}

	@Override
	public Language getLanguage() {
		if (isMeaning()) {
			return Language.valueOf(name().substring(name().length() - 2, name().length()).toLowerCase());
		} else {
			return null;
		}
	}

	@Override
	public boolean isAnalyzed() {
		return name().contains("ANALYZED");
	}
}
