package net.makimono.activity;

import java.util.List;

import net.makimono.R;
import net.makimono.adapter.SearchResultAdapter;
import net.makimono.content.SearchSuggestionProvider;
import net.makimono.model.Entry;
import net.makimono.searcher.Searcher;
import net.makimono.service.SearcherService;
import net.makimono.service.SearcherServiceConnection;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public abstract class AbstractSearchActivity extends AbstractDefaultActivity implements OnItemClickListener {
	private static final String CLASS_NAME = AbstractSearchActivity.class.getName();

	protected SearcherServiceConnection connection = new SearcherServiceConnection();

	private SearchResultAdapter resultAdapter;
	private ListView listView;
	private TextView noEntriesTextView;

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
		listView.setOnItemClickListener(this);
		resultAdapter = new SearchResultAdapter(this);
		listView.setAdapter(resultAdapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(connection);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.findItem(R.id.menu_search).setVisible(true);
		return true;
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
			new SearchTask().execute(query);
		}
	}

	protected abstract Searcher getSearcher();

	private class SearchTask extends AsyncTask<String, Void, List<? extends Entry>> {

		protected List<? extends Entry> doInBackground(String... queries) {
			try {
				String query = queries[0];
				List<? extends Entry> entries = getSearcher().search(query);
				if (!entries.isEmpty()) {
					SearchSuggestionProvider.getSearchRecentSuggestions(AbstractSearchActivity.this).saveRecentQuery(query, null);
				}
				return entries;
			} catch (Exception e) {
				Log.e(CLASS_NAME, "Failed to search", e);
				return null;
			}
		}

		protected void onPostExecute(List<? extends Entry> entries) {
			if (entries.isEmpty()) {
				noEntriesTextView.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
			} else {
				noEntriesTextView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
				resultAdapter.updateEntries(entries);
				listView.scrollTo(0, 0);
			}
		}
	}
}
