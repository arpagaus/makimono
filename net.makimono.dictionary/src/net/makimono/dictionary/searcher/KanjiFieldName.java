package net.makimono.dictionary.searcher;

import net.makimono.dictionary.model.Language;

public enum KanjiFieldName implements IndexFieldName {
	LITERAL, CODE_POINT, JLPT, GRADE, FREQUENCY, MAIN_RADICAL, RADICAL, STROKE_COUNT, ONYOMI, KUNYOMI, NANORI, PINYIN, HANGUL, MEANING_EN, MEANING_ES, MEANING_FR, MEANING_PT, MEANING_ANALYZED_EN, MEANING_ANALYZED_ES, MEANING_ANALYZED_FR, MEANING_ANALYZED_PT, STROKE_PATHS, ROMAJI;

	@Override
	public boolean isMeaning() {
		return name().startsWith("MEANING");
	}

	@Override
	public boolean isAnalyzed() {
		return name().contains("ANALYZED");
	}

	@Override
	public boolean isRomaji() {
		return this.equals(ROMAJI);
	}

	@Override
	public Language getLanguage() {
		if (isMeaning()) {
			return Language.valueOf(name().substring(name().length() - 2, name().length()).toLowerCase());
		} else {
			return null;
		}
	}
}
