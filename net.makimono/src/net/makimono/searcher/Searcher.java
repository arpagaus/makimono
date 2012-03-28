package net.makimono.searcher;

import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import net.makimono.model.Entry;

public interface Searcher<T extends Entry> {
	public List<T> search(String queryString) throws IOException;

	public TreeSet<String> suggest(String prefix) throws IOException;
}
