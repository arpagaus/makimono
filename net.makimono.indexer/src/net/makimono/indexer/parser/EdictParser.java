package net.makimono.indexer.parser;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import net.makimono.model.DictionaryEntry;
import net.makimono.model.Language;
import net.makimono.model.Meaning;
import net.makimono.model.Sense;

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
			String meaning = parts[i].replaceAll("^\\(.*?\\)", "").trim();
			if (!meaning.isEmpty()) {
				sense.getMeanings().add(new Meaning(meaning, language));
			}
		}

		return entry;
	}

	public List<DictionaryEntry> parse(Reader reader) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(reader);

		List<DictionaryEntry> entries = new ArrayList<DictionaryEntry>();
		String line = bufferedReader.readLine();
		while (line != null) {
			DictionaryEntry entry = parseLine(line);
			if (entry != null) {
				entries.add(entry);
			}
			line = bufferedReader.readLine();
		}
		return entries;
	}

}
