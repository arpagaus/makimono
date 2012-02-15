package net.makimono.searcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.makimono.model.Language;

public enum Fields {
	EXPRESSION, READING, SENSE_EN, SENSE_DE, SENSE_FR, SENSE_RU, SENSE_ANALYZED_EN, SENSE_ANALYZED_DE, SENSE_ANALYZED_FR, SENSE_ANALYZED_RU;

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

	public static String[] getAllAnalzedFields(List<Language> languages) {
		ArrayList<String> fields = new ArrayList<String>();
		fields.add(EXPRESSION.name());
		fields.add(READING.name());
		for (Language l : languages) {
			switch (l) {
			case en:
				fields.add(SENSE_ANALYZED_EN.name());
				break;
			case de:
				fields.add(SENSE_ANALYZED_DE.name());
				break;
			case fr:
				fields.add(SENSE_ANALYZED_FR.name());
				break;
			case ru:
				fields.add(SENSE_ANALYZED_RU.name());
				break;
			}
		}
		return fields.toArray(new String[fields.size()]);
	}

	public static String[] getAllNotAnalzedFields(List<Language> languages) {
		ArrayList<String> fields = new ArrayList<String>();
		fields.add(EXPRESSION.name());
		fields.add(READING.name());
		for (Language l : languages) {
			switch (l) {
			case en:
				fields.add(SENSE_EN.name());
				break;
			case de:
				fields.add(SENSE_DE.name());
				break;
			case fr:
				fields.add(SENSE_FR.name());
				break;
			case ru:
				fields.add(SENSE_RU.name());
				break;
			}
		}
		return fields.toArray(new String[fields.size()]);
	}
}
