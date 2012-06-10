package net.makimono.dictionary.content;

import net.makimono.dictionary.searcher.KanjiSearcher;
import net.makimono.dictionary.service.SearcherServiceConnection;

public class KanjiSearchSuggestionProvider extends AbstractSearchSuggestionProvider {

	@Override
	protected KanjiSearcher getSearcher(SearcherServiceConnection connection) {
		return connection.getKanjiSearcher();
	}

}
