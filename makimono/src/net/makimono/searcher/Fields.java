package net.makimono.searcher;

import java.util.HashSet;
import java.util.Set;

public enum Fields {
	EXPRESSION, READING, SENSE_EN, SENSE_DE, SENSE_RU, SENSE_FR, SENSE_ANALYZED_EN, SENSE_ANALYZED_DE, SENSE_ANALYZED_RU, SENSE_ANALYZED_FR;

	public static final String[] ALL_ANALYZED_FIELDS;
	public static final String[] ALL_NOT_ANALYZED_FIELDS;

	static {
		Set<String> allFields = new HashSet<String>();
		Set<String> allNotAnalyzedFields = new HashSet<String>();
		for (Fields f : values()) {
			if (!f.name().contains("ANALYZED")) {
				allNotAnalyzedFields.add(f.name());
			} else {
				allFields.add(f.name());
			}
		}
		ALL_ANALYZED_FIELDS = allFields.toArray(new String[allFields.size()]);
		ALL_NOT_ANALYZED_FIELDS = allNotAnalyzedFields.toArray(new String[allNotAnalyzedFields.size()]);
	}
}
