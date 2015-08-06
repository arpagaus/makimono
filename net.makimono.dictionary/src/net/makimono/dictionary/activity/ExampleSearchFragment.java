package net.makimono.dictionary.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ListView;
import net.makimono.dictionary.R;
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

		Bundle arguments = new Bundle();
		arguments.putParcelable(net.makimono.dictionary.Intent.EXTRA_EXAMPLE_ENTRY, entry);

		DictionaryEntryFragment fragment = new DictionaryEntryFragment();
		fragment.setArguments(arguments);

		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
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
