package net.makimono.dictionary.indexer.parser;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import net.makimono.dictionary.model.DictionaryEntry;
import net.makimono.dictionary.model.FieldOfApplication;
import net.makimono.dictionary.model.Language;
import net.makimono.dictionary.model.Meaning;
import net.makimono.dictionary.model.Miscellaneous;
import net.makimono.dictionary.model.PartOfSpeech;
import net.makimono.dictionary.model.Sense;

public class EdictParser {

	private Language language;

	public EdictParser() {
		this(Language.en);
	}

	public EdictParser(Language language) {
		this.language = language;
	}

	DictionaryEntry parseLine(String line) {
		if (isBlank(line)) {
			return null;
		}

		DictionaryEntry entry = new DictionaryEntry();
		String[] parts = split(line, '/');

		if (parts.length < 2) {
			return null;
		}

		String japanese = parts[0].trim();
		if (japanese.endsWith("]")) {
			String[] japaneseParts = split(parts[0]);
			entry.getExpressions().add(japaneseParts[0]);
			entry.getReadings().add(strip(japaneseParts[1], "[]"));
		} else {
			entry.getReadings().add(japanese);
		}

		Sense sense = new Sense();
		entry.getSenses().add(sense);
		for (int i = 1; i < parts.length; i++) {
			String meaning = parts[i].trim();
			if (!meaning.isEmpty() && !"(P)".equals(meaning)) {
				String[] substrings = substringsBetween(parts[i], "(", ")");
				if (substrings != null) {
					for (String substring : substrings) {
						if (addAdditionalInfos(sense, substring)) {
							meaning = remove(meaning, "(" + substring + ")");
						}
					}
				}
				sense.getMeanings().add(new Meaning(meaning.trim(), language));
			}
		}
		return entry;
	}

	private boolean addAdditionalInfos(Sense sense, String substring) {
		boolean foundGeneralInfo = false;
		String[] additionalInfos = split(substring, ',');
		for (String info : additionalInfos) {
			final String enumName = "JMdict_" + info.replaceAll("-", "_");
			if (enumContainsValue(PartOfSpeech.values(), enumName)) {
				sense.getPartsOfSpeech().add(PartOfSpeech.valueOf(enumName));
				foundGeneralInfo = true;
			} else if (enumContainsValue(Miscellaneous.values(), enumName)) {
				sense.getMiscellaneous().add(Miscellaneous.valueOf(enumName));
				foundGeneralInfo = true;
			} else if (enumContainsValue(FieldOfApplication.values(), enumName)) {
				sense.getFieldsOfApplication().add(FieldOfApplication.valueOf(enumName));
				foundGeneralInfo = true;
			}
		}
		return foundGeneralInfo;
	}

	@SuppressWarnings("rawtypes")
	private boolean enumContainsValue(Enum[] enums, String enumName) {
		for (Enum e : enums) {
			if (e.name().equals(enumName)) {
				return true;
			}
		}
		return false;
	}

	public Map<String, DictionaryEntry> parse(File file) throws IOException {
		System.out.println("Parsing " + file.getName());
		return parse(new InputStreamReader(new FileInputStream(file), "UTF-8"));
	}

	public Map<String, DictionaryEntry> parse(Reader reader) throws IOException {
		final long time = System.currentTimeMillis();

		BufferedReader bufferedReader = new BufferedReader(reader);
		Map<String, DictionaryEntry> entries = new HashMap<String, DictionaryEntry>();
		String line = bufferedReader.readLine();
		while (line != null) {
			DictionaryEntry entry = parseLine(line);
			if (entry != null) {
				entries.put(entry.getExpression(), entry);
			}
			line = bufferedReader.readLine();
		}

		System.out.println("Finished parsing EDICT file with " + entries.size() + " entries in " + (System.currentTimeMillis() - time) + "ms");
		return entries;
	}
}
