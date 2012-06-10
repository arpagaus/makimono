package net.makimono.dictionary.activity;

import java.util.List;

import net.makimono.dictionary.R;
import net.makimono.dictionary.adapter.SearchResultAdapter;
import net.makimono.dictionary.content.AbstractSearchSuggestionProvider;
import net.makimono.dictionary.model.Entry;
import net.makimono.dictionary.searcher.Searcher;
import net.makimono.dictionary.service.SearcherService;
import net.makimono.dictionary.service.SearcherServiceConnection;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;

public abstract class AbstractSearchActivity extends AbstractDefaultActivity implements OnItemClickListener {
	private static final String CLASS_NAME = AbstractSearchActivity.class.getName();

	private String searchString = "";

	protected SearcherServiceConnection connection = new SearcherServiceConnection();

	private SearchResultAdapter resultAdapter;
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
		resultAdapter = new SearchResultAdapter(this);
		listView.setAdapter(resultAdapter);
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
		getSupportMenuInflater().inflate(R.menu.search, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		setIntent(intent);
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			searchString = query;

			progressView.setVisibility(View.VISIBLE);
			noEntriesTextView.setVisibility(View.GONE);
			listView.setVisibility(View.GONE);
			new SearchTask().execute(query);
		}
	}

	protected abstract Searcher<? extends Entry> getSearcher();

	protected abstract Class<? extends AbstractSearchSuggestionProvider> getSearchSuggestionProviderClass();

	private class SearchTask extends AsyncTask<String, Void, List<? extends Entry>> {

		protected List<? extends Entry> doInBackground(String... queries) {
			try {
				String query = queries[0];
				List<? extends Entry> entries = getSearcher().search(query);
				if (!entries.isEmpty()) {
					AbstractSearchSuggestionProvider.saveRecentQuery(getApplicationContext(), getSearchSuggestionProviderClass(), query);
				}
				return entries;
			} catch (Exception e) {
				Log.e(CLASS_NAME, "Failed to search", e);
				return null;
			}
		}

		protected void onPostExecute(List<? extends Entry> entries) {
			progressView.setVisibility(View.GONE);
			if (entries.isEmpty()) {
				noEntriesTextView.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
			} else {
				noEntriesTextView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
				resultAdapter.updateEntries(entries);
				listView.setSelectionAfterHeaderView();
			}
		}
	}

}
