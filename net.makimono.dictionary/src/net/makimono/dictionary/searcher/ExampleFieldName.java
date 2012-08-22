package net.makimono.dictionary.searcher;

import net.makimono.dictionary.model.Language;

public enum ExampleFieldName implements IndexFieldName {
	SENTENCE_EN, SENTENCE_DE, SENTENCE_FR, SENTENCE_ES, SENTENCE_JA;

	@Override
	public Language getLanguage() {
		return Language.valueOf(name().substring(name().length() - 2, name().length()).toLowerCase());
	}

	@Override
	public boolean isAnalyzed() {
		return true;
	}

	@Override
	public boolean isMeaning() {
		return true;
	}

	@Override
	public boolean isRomaji() {
		return false;
	}
}
