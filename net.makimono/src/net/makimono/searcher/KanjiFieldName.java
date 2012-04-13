package net.makimono.searcher;

import net.makimono.model.Language;

public enum KanjiFieldName implements IndexFieldName {
	LITERAL, CODE_POINT, JLPT, GRADE, FREQUENCY, RADICAL, STROKE_COUNT, ONYOMI, KUNYOMI, NANORI, PINYIN, HANGUL, RADICAL_NAME, MEANING_EN, MEANING_ES, MEANING_FR, MEANING_PT, MEANING_ANALYZED_EN, MEANING_ANALYZED_ES, MEANING_ANALYZED_FR, MEANING_ANALYZED_PT, STROKE_PATHS;

	@Override
	public boolean isAnalyzed() {
		return name().contains("ANALYZED");
	}

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
}
