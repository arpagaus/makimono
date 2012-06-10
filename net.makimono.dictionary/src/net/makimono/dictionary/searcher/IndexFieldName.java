package net.makimono.dictionary.searcher;

import net.makimono.dictionary.model.Language;

public interface IndexFieldName {
	public String name();

	public Language getLanguage();

	public boolean isAnalyzed();

	public boolean isMeaning();

	public boolean isRomaji();
}
