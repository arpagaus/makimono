package net.makimono.dictionary.activity;

import java.util.List;

import net.makimono.dictionary.R;
import net.makimono.dictionary.adapter.ExampleSearchResultAdapter;
import net.makimono.dictionary.content.AbstractSearchSuggestionProvider;
import net.makimono.dictionary.content.ExampleSearchSuggestionProvider;
import net.makimono.dictionary.model.Entry;
import net.makimono.dictionary.model.ExampleEntry;
import net.makimono.dictionary.searcher.Searcher;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;

public class ExampleSearchActivity extends AbstractSearchActivity {

	private ExampleSearchResultAdapter listAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.example);
	}

	@Override
	public void onItemClick(AdapterView<?> view, View v, int position, long id) {
		ExampleEntry entry = (ExampleEntry) view.getAdapter().getItem(position);
		Intent intent = new Intent(this, ExampleEntryActivity.class);
		intent.putExtra(net.makimono.dictionary.Intent.EXTRA_EXAMPLE_ENTRY, entry);
		startActivity(intent);
	}

	@Override
	protected Searcher<? extends Entry> getSearcher() {
		return connection.getExampleSearcher();
	}

	@Override
	protected Class<? extends AbstractSearchSuggestionProvider> getSearchSuggestionProviderClass() {
		return ExampleSearchSuggestionProvider.class;
	}

	@Override
	protected ListAdapter getListAdapter() {
		if (listAdapter == null) {
			listAdapter = new ExampleSearchResultAdapter(getBaseContext());
		}
		return listAdapter;
	}

	@Override
	protected void updateEntries(List<? extends Entry> entries) {
		listAdapter.updateEntries(entries);
	}
}
