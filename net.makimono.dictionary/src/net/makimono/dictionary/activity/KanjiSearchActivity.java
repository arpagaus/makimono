package net.makimono.dictionary.activity;

import net.makimono.dictionary.R;
import net.makimono.dictionary.content.AbstractSearchSuggestionProvider;
import net.makimono.dictionary.content.KanjiSearchSuggestionProvider;
import net.makimono.dictionary.model.KanjiEntry;
import net.makimono.dictionary.searcher.KanjiSearcher;
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
