package net.makimono.dictionary.searcher;

import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import net.makimono.dictionary.model.Entry;

public interface Searcher<T extends Entry> {
	public List<T> search(String queryString) throws IOException;

	public TreeSet<String> suggest(String prefix) throws IOException;
}
