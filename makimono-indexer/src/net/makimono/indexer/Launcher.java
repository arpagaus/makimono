package net.makimono.indexer;

import java.io.File;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

public class Launcher {

	/**
	 * Creates a JMDICT or KANJIDIC2 Lucene index
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Usage: Indexer [path to original GZIP file] [path to index destination] [JMDICT | KANJIDIC2]");
			System.exit(1);
		}

		File gzipFile = new File(args[0]);
		if (!gzipFile.exists()) {
			System.err.println("Failed to find file: " + args[0]);
			System.exit(1);
		}

		Directory luceneDirectory = new SimpleFSDirectory(new File(args[1]));

		if (args.length < 3 || args[2].equalsIgnoreCase("JMDICT")) {
			new DictionaryIndexer().createIndex(gzipFile, luceneDirectory);
		} else if (args[2].equalsIgnoreCase("KANJIDIC2")) {
			new KanjiIndexer().createIndex(gzipFile, luceneDirectory);
		} else {
			System.err.println("Unrecognized dictionary format " + args[2]);
		}
	}

}
