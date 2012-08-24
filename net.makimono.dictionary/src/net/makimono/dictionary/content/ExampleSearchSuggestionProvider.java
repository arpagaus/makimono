package net.makimono.dictionary.content;

import net.makimono.dictionary.model.Entry;
import net.makimono.dictionary.searcher.Searcher;
import net.makimono.dictionary.service.SearcherServiceConnection;

public class ExampleSearchSuggestionProvider extends AbstractSearchSuggestionProvider {

	@Override
	protected Searcher<? extends Entry> getSearcher(SearcherServiceConnection connection) {
		return connection.getExampleSearcher();
	}

}
