package net.makimono.dictionary.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import net.makimono.dictionary.content.AbstractSearchSuggestionProvider;
import net.makimono.dictionary.content.KanjiSearchSuggestionProvider;
import net.makimono.dictionary.model.KanjiEntry;
import net.makimono.dictionary.searcher.KanjiSearcher;

public class KanjiSearchFragment extends AbstractSearchFragment {

	@Override
	public void onItemClick(AdapterView<?> view, View v, int position, long id) {
		KanjiEntry entry = (KanjiEntry) view.getAdapter().getItem(position);
		Intent intent = new Intent(getActivity(), KanjiEntryActivity.class);
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

	@Override
	protected void executeQuery(String query) {
		super.executeQuery(query);
		// TODO inter-fragment communication?
		// if
		// (net.makimono.dictionary.Intent.ACTION_RADICAL_SEARCH.equals(intent.getAction()))
		// {
		// final List<String> radicals =
		// Arrays.asList(intent.getStringArrayExtra(net.makimono.dictionary.Intent.EXTRA_RADICALS));
		// final int minStrokes =
		// intent.getIntExtra(net.makimono.dictionary.Intent.EXTRA_MIN_STROKES,
		// 0);
		// final int maxStrokes =
		// intent.getIntExtra(net.makimono.dictionary.Intent.EXTRA_MAX_STROKES,
		// 0);
		// Log.v(LOG_TAG, "minStrokes=" + minStrokes + ", maxStrokes=" +
		// maxStrokes + ", radicals=" + radicals);
		// new SearchTask() {
		// protected List<? extends Entry> executeQuery(Object... queries)
		// throws IOException {
		// return getSearcher().searchByRadicals(radicals, minStrokes,
		// maxStrokes);
		// }
		// }.execute();
		// }
	}
}
