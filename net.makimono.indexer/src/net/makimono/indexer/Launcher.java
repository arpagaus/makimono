package net.makimono.indexer;

import java.io.File;
import java.util.Map;

import net.makimono.indexer.parser.EdictParser;
import net.makimono.model.DictionaryEntry;
import net.makimono.model.Language;

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

		File directory = new File(args[1]);
		for (File f : directory.listFiles()) {
			f.delete();
		}

		Directory luceneDirectory = new SimpleFSDirectory(directory);

		if (args.length < 3 || args[2].equalsIgnoreCase("JMDICT")) {
			Map<String, DictionaryEntry> mixinMeanings = new EdictParser(Language.es).parse(new File("res/hispadic.utf8"));
			DictionaryIndexer indexer = new DictionaryIndexer(mixinMeanings);
			indexer.createIndex(gzipFile, luceneDirectory);
		} else if (args[2].equalsIgnoreCase("KANJIDIC2")) {
			KanjiIndexer indexer;
			if (args.length >= 4) {
				Map<Integer, String> strokePaths = new KanjiVgIndexer().getStrokePaths(new File(args[3]));
				indexer = new KanjiIndexer(strokePaths);
			} else {
				indexer = new KanjiIndexer();
			}
			indexer.createIndex(gzipFile, luceneDirectory);
		} else {
			System.err.println("Unrecognized dictionary format " + args[2]);
		}
	}
}
