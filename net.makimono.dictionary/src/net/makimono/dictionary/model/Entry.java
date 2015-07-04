package net.makimono.dictionary.model;

import java.util.Collection;

public interface Entry {

	public String getExpression();

	public String getReadingSummary();

	public String getMeaningSummary(Collection<Language> languages);
}
