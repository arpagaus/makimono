package net.makimono.dictionary.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

public class IndexerLauncher {

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("Usage: IndexerLauncher [path to properties file]");
			System.exit(1);
		}

		Properties properties = new Properties();
		properties.load(new FileInputStream(new File(args[0])));

		File directory = new File(properties.getProperty("index"));
		for (File f : directory.listFiles()) {
			f.delete();
		}

		Directory luceneDirectory = new SimpleFSDirectory(directory);

		Class<?> clazz = Class.forName(properties.getProperty("class"));
		Indexer indexer = (Indexer) clazz.getDeclaredConstructor(Properties.class).newInstance(properties);
		indexer.createIndex(new File(properties.getProperty("sourceFile")), luceneDirectory);
	}
}
