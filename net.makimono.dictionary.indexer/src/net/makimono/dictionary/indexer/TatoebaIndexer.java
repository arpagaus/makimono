package net.makimono.dictionary.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.store.Directory;

public class TatoebaIndexer implements Indexer {

	private Map<Integer, Integer> links = new HashMap<Integer, Integer>();

	public TatoebaIndexer(Properties properties) {
		String linksFile = properties.getProperty("linksFile");
		if (StringUtils.isNotBlank(linksFile)) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(linksFile));
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] ids = line.split("\t");
					links.put(Integer.parseInt(ids[0]), Integer.parseInt(ids[0]));
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void createIndex(File gzipXmlFile, Directory luceneDirectory) throws Exception {
	}
}
