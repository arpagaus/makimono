package net.makimono.dictionary.activity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import net.makimono.dictionary.R;
import net.makimono.dictionary.content.AbstractSearchSuggestionProvider;
import net.makimono.dictionary.content.KanjiSearchSuggestionProvider;
import net.makimono.dictionary.model.Entry;
import net.makimono.dictionary.model.KanjiEntry;
import net.makimono.dictionary.searcher.KanjiSearcher;

public class KanjiSearchFragment extends AbstractSearchFragment {

	private static final String LOG_TAG = KanjiSearchFragment.class.getSimpleName();

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		KanjiEntry entry = (KanjiEntry) getListAdapter().getItem(position);

		Bundle arguments = new Bundle();
		arguments.putParcelable(KanjiEntryFragment.EXTRA_KANJI_ENTRY, entry);

		KanjiEntryFragment fragment = new KanjiEntryFragment();
		fragment.setArguments(arguments);

		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
	}

	@Override
	protected KanjiSearcher getSearcher() {
		return connection.getKanjiSearcher();
	}

	@Override
	protected Class<? extends AbstractSearchSuggestionProvider> getSearchSuggestionProviderClass() {
		return KanjiSearchSuggestionProvider.class;
	}

	@Override
	protected void executeQuery(String query) {
		super.executeQuery(query);

		if (getArguments().containsKey(net.makimono.dictionary.Intent.EXTRA_RADICALS)) {
			final List<String> radicals = Arrays.asList(getArguments().getStringArray(net.makimono.dictionary.Intent.EXTRA_RADICALS));
			final int minStrokes = getArguments().getInt(net.makimono.dictionary.Intent.EXTRA_MIN_STROKES, 0);
			final int maxStrokes = getArguments().getInt(net.makimono.dictionary.Intent.EXTRA_MAX_STROKES, 0);
			Log.v(LOG_TAG, "minStrokes=" + minStrokes + ", maxStrokes=" + maxStrokes + ", radicals=" + radicals);
			new SearchTask() {
				protected List<? extends Entry> executeQuery(Object... queries) throws IOException {
					return getSearcher().searchByRadicals(radicals, minStrokes, maxStrokes);
				}
			}.execute();
		}
	}
}
