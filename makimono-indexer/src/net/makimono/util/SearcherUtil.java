package net.makimono.util;

import java.io.File;
import java.util.List;

import net.makimono.model.Entry;
import net.makimono.searcher.Searcher;

/**
 * <p>
 * Searches in the given JMdict index
 * </p>
 * <p>
 * When using Windows UTF-8 won't be properly printed on the console. <a href=
 * "http://paranoid-engineering.blogspot.com/2008/05/getting-unicode-output-in-eclipse.html"
 * >Check this workaround</a>.
 * </p>
 */
public class SearcherUtil {

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Usage: Searcher [path to index] [saercher string]");
			System.exit(1);
		}

		String query = args[1];
		System.out.println("Searching for '" + query + "'");

		long time = System.currentTimeMillis();
		List<Entry> entries = new Searcher(new File(args[0])).search(query);

		for (Entry entry : entries) {
			System.out.println(entry);
		}

		System.out.println("Found " + entries.size() + " hits in " + (System.currentTimeMillis() - time) + "ms");
	}
}
