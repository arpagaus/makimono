package net.makimono.searcher;

import java.io.IOException;
import java.util.List;

import net.makimono.model.Entry;

public interface Searcher {
	public List<? extends Entry> search(String queryString) throws IOException;
}
