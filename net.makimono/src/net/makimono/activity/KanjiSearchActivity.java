package net.makimono.activity;

import net.makimono.content.AbstractSearchSuggestionProvider;
import net.makimono.content.KanjiSearchSuggestionProvider;
import net.makimono.model.KanjiEntry;
import net.makimono.searcher.Searcher;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

public class KanjiSearchActivity extends AbstractSearchActivity {
	@Override
	public void onItemClick(AdapterView<?> view, View v, int position, long id) {
		KanjiEntry entry = (KanjiEntry) view.getAdapter().getItem(position);
		Intent intent = new Intent(this, KanjiEntryActivity.class);
		intent.putExtra(KanjiEntryActivity.EXTRA_KANJI_ENTRY, entry);
		startActivity(intent);
	}

	@Override
	protected Searcher getSearcher() {
		return connection.getKanjiSearcher();
	}

	@Override
	protected Class<? extends AbstractSearchSuggestionProvider> getSearchSuggestionProviderClass() {
		return KanjiSearchSuggestionProvider.class;
	}
}
