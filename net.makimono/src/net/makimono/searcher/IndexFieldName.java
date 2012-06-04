package net.makimono.searcher;

import net.makimono.model.Language;

public interface IndexFieldName {
	public String name();

	public Language getLanguage();

	public boolean isAnalyzed();

	public boolean isMeaning();

	public boolean isRomaji();
}
