package net.makimono.content;

import net.makimono.searcher.KanjiSearcher;
import net.makimono.service.SearcherServiceConnection;

public class KanjiSearchSuggestionProvider extends AbstractSearchSuggestionProvider {

	@Override
	protected KanjiSearcher getSearcher(SearcherServiceConnection connection) {
		return connection.getKanjiSearcher();
	}

}
