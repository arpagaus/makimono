package net.makimono.dictionary.activity;

import java.util.List;

import net.makimono.dictionary.adapter.ExampleSearchResultAdapter;
import net.makimono.dictionary.content.AbstractSearchSuggestionProvider;
import net.makimono.dictionary.content.ExampleSearchSuggestionProvider;
import net.makimono.dictionary.model.Entry;
import net.makimono.dictionary.searcher.Searcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;

public class ExampleSearchActivity extends AbstractSearchActivity {

	private ExampleSearchResultAdapter listAdapter;

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		System.out.println("onItemClick");
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
