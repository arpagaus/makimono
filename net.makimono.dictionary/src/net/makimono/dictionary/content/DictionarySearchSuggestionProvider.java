package net.makimono.dictionary.content;

import net.makimono.dictionary.searcher.DictionarySearcher;
import net.makimono.dictionary.service.SearcherServiceConnection;

public class DictionarySearchSuggestionProvider extends AbstractSearchSuggestionProvider {

	@Override
	protected DictionarySearcher getSearcher(SearcherServiceConnection connection) {
		return connection.getDictionarySearcher();
	}

}
