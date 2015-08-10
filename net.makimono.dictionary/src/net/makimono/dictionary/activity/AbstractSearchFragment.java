package net.makimono.dictionary.activity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import net.makimono.dictionary.R;
import net.makimono.dictionary.adapter.SearchResultAdapter;
import net.makimono.dictionary.content.AbstractSearchSuggestionProvider;
import net.makimono.dictionary.model.Entry;
import net.makimono.dictionary.searcher.Searcher;
import net.makimono.dictionary.service.SearcherService;
import net.makimono.dictionary.service.SearcherServiceConnection;

public abstract class AbstractSearchFragment extends ListFragment {
	private static final String LOG_TAG = AbstractSearchFragment.class.getName();

	public static final String EXTRA_QUERY_STRING = DictionaryEntryFragment.class + ".EXTRA_SEARCH_STRING";

	protected SearcherServiceConnection connection = new SearcherServiceConnection();

	protected String queryString;

	public AppCompatActivity getAppCompatActivity() {
		return (AppCompatActivity) super.getActivity();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		setListAdapter(new SearchResultAdapter(getActivity()));
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.search, menu);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				executeQuery(query);
				return false;
			}
		});
	}

	protected void executeQuery(String query) {
		this.queryString = query;
		new SearchTask().execute(query);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().bindService(new Intent(getActivity(), SearcherService.class), connection, Context.BIND_AUTO_CREATE);

		String argumentsSearchString = getArguments().getString(EXTRA_QUERY_STRING);
		String instanceSearchString = savedInstanceState.getString(EXTRA_QUERY_STRING);
		if (StringUtils.isNotEmpty(argumentsSearchString)) {
			executeQuery(argumentsSearchString);
		} else if (StringUtils.isNotEmpty(instanceSearchString)) {
			executeQuery(instanceSearchString);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(EXTRA_QUERY_STRING, queryString);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unbindService(connection);
	}

	protected void updateEntries(List<? extends Entry> entries) {
		((SearchResultAdapter) getListAdapter()).updateEntries(entries);
	}

	protected abstract Searcher<? extends Entry> getSearcher();

	protected abstract Class<? extends AbstractSearchSuggestionProvider> getSearchSuggestionProviderClass();

	protected class SearchTask extends AsyncTask<Object, Void, List<? extends Entry>> {

		@Override
		protected void onPreExecute() {
			setListShown(false);
		}

		@Override
		protected List<? extends Entry> doInBackground(Object... queries) {
			try {
				return executeQuery(queries);
			} catch (Exception e) {
				Log.e(LOG_TAG, "Failed to search", e);
				return null;
			}
		}

		protected List<? extends Entry> executeQuery(Object... queries) throws IOException {
			String query = queries[0].toString();
			List<? extends Entry> entries = getSearcher().search(query);
			if (!entries.isEmpty()) {
				AbstractSearchSuggestionProvider.saveRecentQuery(getActivity().getApplicationContext(), getSearchSuggestionProviderClass(), query);
			}
			return entries;
		}

		@Override
		protected void onPostExecute(List<? extends Entry> entries) {
			if (entries == null) {
				Log.e(LOG_TAG, "No entries provided");
				entries = Collections.emptyList();
			}

			updateEntries(entries);
			setListShown(true);
		}
	}
}
