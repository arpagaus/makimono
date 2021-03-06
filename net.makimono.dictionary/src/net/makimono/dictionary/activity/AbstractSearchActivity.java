package net.makimono.dictionary.activity;

import java.io.IOException;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import net.makimono.dictionary.R;
import net.makimono.dictionary.adapter.SearchResultAdapter;
import net.makimono.dictionary.content.AbstractSearchSuggestionProvider;
import net.makimono.dictionary.model.Entry;
import net.makimono.dictionary.searcher.Searcher;
import net.makimono.dictionary.service.SearcherService;
import net.makimono.dictionary.service.SearcherServiceConnection;

public abstract class AbstractSearchActivity extends AbstractDefaultActivity implements OnItemClickListener {
	private static final String LOG_TAG = AbstractSearchActivity.class.getName();

	/**
	 * Keep the string of the last search, so, it can be show to the user when
	 * she wants to search again
	 */
	private String searchString = "";

	protected SearcherServiceConnection connection = new SearcherServiceConnection();

	private SearchResultAdapter listAdapter;
	private ListView listView;
	private TextView noEntriesTextView;
	private LinearLayout progressView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bindSearcher();
		initiazlizeView();
		handleIntent(getIntent());
	}

	private void bindSearcher() {
		Intent intent = new Intent(this, SearcherService.class);
		bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	private void initiazlizeView() {
		setContentView(R.layout.search_result);
		listView = (ListView) findViewById(android.R.id.list);
		noEntriesTextView = (TextView) findViewById(R.id.no_entries);
		progressView = (LinearLayout) findViewById(R.id.progress_bar);
		listView.setOnItemClickListener(this);
		listView.setAdapter(getListAdapter());
	}

	@Override
	public boolean onSearchRequested() {
		startSearch(searchString, true, null, false);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(connection);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	protected void handleIntent(Intent intent) {
		setIntent(intent);
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			searchString = query;
			new SearchTask().execute(query);
		}
	}

	protected ListAdapter getListAdapter() {
		if (listAdapter == null) {
			listAdapter = new SearchResultAdapter(this);
		}
		return listAdapter;
	}

	protected void updateEntries(List<? extends Entry> entries) {
		listAdapter.updateEntries(entries);
	}

	protected abstract Searcher<? extends Entry> getSearcher();

	protected abstract Class<? extends AbstractSearchSuggestionProvider> getSearchSuggestionProviderClass();

	protected class SearchTask extends AsyncTask<Object, Void, List<? extends Entry>> {

		@Override
		protected void onPreExecute() {
			progressView.setVisibility(View.VISIBLE);
			noEntriesTextView.setVisibility(View.GONE);
			listView.setVisibility(View.GONE);
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
				AbstractSearchSuggestionProvider.saveRecentQuery(getApplicationContext(), getSearchSuggestionProviderClass(), query);
			}
			return entries;
		}

		protected void onPostExecute(List<? extends Entry> entries) {
			progressView.setVisibility(View.GONE);
			if (entries == null) {
				Toast.makeText(AbstractSearchActivity.this, getText(R.string.search_error), Toast.LENGTH_LONG).show();
			} else if (entries.isEmpty()) {
				noEntriesTextView.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
			} else {
				noEntriesTextView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
				updateEntries(entries);
			}
		}
	}

}
