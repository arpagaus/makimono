package net.makimono.content;

import net.makimono.searcher.Searcher;
import net.makimono.service.SearcherServiceConnection;

public class KanjiSearchSuggestionProvider extends AbstractSearchSuggestionProvider {

	@Override
	protected Searcher getSearcher(SearcherServiceConnection connection) {
		return connection.getKanjiSearcher();
	}

}
