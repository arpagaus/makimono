package net.makimono.activity;

import java.util.ArrayList;

import net.makimono.R;
import net.makimono.adapter.SearchResultAdapter;
import net.makimono.content.SearchSuggestionProvider;
import net.makimono.model.DictionaryEntry;
import net.makimono.service.SearcherService;
import net.makimono.service.SearcherServiceConnection;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SearchActivity extends AbstractDefaultActivity implements OnItemClickListener {
	private static final String CLASS_NAME = SearchActivity.class.getName();

	private SearcherServiceConnection connection = new SearcherServiceConnection();

	private SearchResultAdapter resultAdapter;
	private ListView listView;

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
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		setIntent(intent);
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);

			AsyncTask<String, Void, ArrayList<DictionaryEntry>> task = new AsyncTask<String, Void, ArrayList<DictionaryEntry>>() {
				protected ArrayList<DictionaryEntry> doInBackground(String... queries) {
					try {
						String query = queries[0];
						ArrayList<DictionaryEntry> entries = connection.getDictionarySearcher().search(query);
						if (!entries.isEmpty()) {
							SearchSuggestionProvider.getSearchRecentSuggestions(SearchActivity.this).saveRecentQuery(query, null);
						}
						return entries;
					} catch (Exception e) {
						Log.e(CLASS_NAME, "Failed to search", e);
						return null;
					}
				}

				protected void onPostExecute(ArrayList<DictionaryEntry> entries) {
					resultAdapter.updateEntries(entries);
				}
			};
			task.execute(query);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		Intent intent = new Intent(this, EntryActivity.class);
		intent.putExtra("DOC_ID", (int) id);
		startActivity(intent);
	}

}
