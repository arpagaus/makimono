package net.makimono.dictionary.activity;

import net.makimono.dictionary.content.AbstractSearchSuggestionProvider;
import net.makimono.dictionary.content.ExampleSearchSuggestionProvider;
import net.makimono.dictionary.model.Entry;
import net.makimono.dictionary.searcher.Searcher;
import android.view.View;
import android.widget.AdapterView;

public class ExampleSearchActivity extends AbstractSearchActivity {

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
}
