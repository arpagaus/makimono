package net.makimono.content;

import net.makimono.searcher.DictionarySearcher;
import net.makimono.service.SearcherServiceConnection;

public class DictionarySearchSuggestionProvider extends AbstractSearchSuggestionProvider {

	@Override
	protected DictionarySearcher getSearcher(SearcherServiceConnection connection) {
		return connection.getDictionarySearcher();
	}

}
