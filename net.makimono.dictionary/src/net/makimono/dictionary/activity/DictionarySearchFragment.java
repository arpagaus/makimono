package net.makimono.dictionary.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import net.makimono.dictionary.R;
import net.makimono.dictionary.content.AbstractSearchSuggestionProvider;
import net.makimono.dictionary.content.DictionarySearchSuggestionProvider;
import net.makimono.dictionary.model.DictionaryEntry;
import net.makimono.dictionary.searcher.DictionarySearcher;

public class DictionarySearchFragment extends AbstractSearchFragment {
	private static final String CLASS_NAME = DictionarySearchFragment.class.getName();

	@Override
	public void onListItemClick(ListView l, View view, int position, long id) {
		DictionaryEntry entry = (DictionaryEntry) getListAdapter().getItem(position);
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DictionaryEntry.writeEntry(new ObjectOutputStream(out), entry);

			Bundle arguments = new Bundle();
			arguments.putByteArray(DictionaryEntryFragment.EXTRA_DICTIONARY_ENTRY, out.toByteArray());

			DictionaryEntryFragment fragment = new DictionaryEntryFragment();
			fragment.setArguments(arguments);

			FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
		} catch (IOException e) {
			Log.e(CLASS_NAME, "Failed to serialize entry", e);
		}
	}

	@Override
	protected DictionarySearcher getSearcher() {
		return connection.getDictionarySearcher();
	}

	@Override
	protected Class<? extends AbstractSearchSuggestionProvider> getSearchSuggestionProviderClass() {
		return DictionarySearchSuggestionProvider.class;
	}
}
