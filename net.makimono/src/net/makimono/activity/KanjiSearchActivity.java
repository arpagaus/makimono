package net.makimono.activity;

import net.makimono.R;
import net.makimono.content.AbstractSearchSuggestionProvider;
import net.makimono.content.KanjiSearchSuggestionProvider;
import net.makimono.model.KanjiEntry;
import net.makimono.searcher.KanjiSearcher;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

public class KanjiSearchActivity extends AbstractSearchActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setTitle(R.string.kanji);
	}

	@Override
	public void onItemClick(AdapterView<?> view, View v, int position, long id) {
		KanjiEntry entry = (KanjiEntry) view.getAdapter().getItem(position);
		Intent intent = new Intent(this, KanjiEntryActivity.class);
		intent.putExtra(KanjiEntryActivity.EXTRA_KANJI_ENTRY, entry);
		startActivity(intent);
	}

	@Override
	protected KanjiSearcher getSearcher() {
		return connection.getKanjiSearcher();
	}

	@Override
	protected Class<? extends AbstractSearchSuggestionProvider> getSearchSuggestionProviderClass() {
		return KanjiSearchSuggestionProvider.class;
	}
}
