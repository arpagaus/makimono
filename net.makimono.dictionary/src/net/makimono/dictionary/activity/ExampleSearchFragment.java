package net.makimono.dictionary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import net.makimono.dictionary.adapter.SearchResultAdapter;
import net.makimono.dictionary.content.AbstractSearchSuggestionProvider;
import net.makimono.dictionary.content.ExampleSearchSuggestionProvider;
import net.makimono.dictionary.model.Entry;
import net.makimono.dictionary.model.ExampleEntry;
import net.makimono.dictionary.searcher.Searcher;

public class ExampleSearchFragment extends AbstractSearchFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new SearchResultAdapter(getActivity()));
	}

	@Override
	public void onListItemClick(ListView l, View view, int position, long id) {
		ExampleEntry entry = (ExampleEntry) getListAdapter().getItem(position);
		Intent intent = new Intent(getActivity(), ExampleEntryActivity.class);
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
}
