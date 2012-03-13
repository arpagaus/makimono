package net.makimono.model;

import java.util.List;

public interface Entry {

	public String getExpression();

	public String getReadingSummary();

	public String getMeaningSummary(List<Language> languages);
}
