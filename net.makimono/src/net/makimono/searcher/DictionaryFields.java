package net.makimono.searcher;

import net.makimono.model.Language;

public enum DictionaryFields {
	EXPRESSION, READING, SENSE_EN, SENSE_DE, SENSE_FR, SENSE_RU, SENSE_ANALYZED_EN, SENSE_ANALYZED_DE, SENSE_ANALYZED_FR, SENSE_ANALYZED_RU;

	public Language getLanguage() {
		if (name().startsWith("SENSE")) {
			return Language.valueOf(name().substring(name().length() - 2, name().length()).toLowerCase());
		} else {
			return null;
		}
	}

	public boolean isSenseField() {
		return name().startsWith("SENSE");
	}

	public boolean isAnalyzedField() {
		return name().contains("ANALYZED");
	}
}
