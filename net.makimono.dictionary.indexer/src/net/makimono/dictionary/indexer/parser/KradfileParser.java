package net.makimono.dictionary.indexer.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;

public class KradfileParser {
	private BufferedReader reader;

	public KradfileParser(File file) throws IOException {
		GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(file));
		reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
	}

	public Map<String, Set<String>> getKanjiRadicals() throws IOException {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();

		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")) {
				continue;
			}

			HashSet<String> set = new HashSet<String>();
			String[] split = line.split(":");
			map.put(split[0].trim(), set);
			for (String radical : split[1].split(" ")) {
				if (StringUtils.isNotBlank(radical)) {
					set.add(radical);
				}
			}
		}

		return map;
	}
}
