package net.makimono.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import net.makimono.R;
import net.makimono.adapter.SearchResultAdapter;
import net.makimono.content.SearchSuggestionProvider;
import net.makimono.model.DictionaryEntry;
import net.makimono.model.Entry;
import net.makimono.model.KanjiEntry;
import net.makimono.searcher.Searcher;
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
import android.widget.TextView;

public class SearchActivity extends AbstractDefaultActivity implements OnItemClickListener {
	private static final String CLASS_NAME = SearchActivity.class.getName();
	public static final String EXTRA_SEARCH_CONTENT = SearchActivity.class.getName() + ".EXTRA_SEARCH_CONTENT";

	public enum SearchContent {
		Dictionary {
			@Override
			public Searcher getSearcher(SearcherServiceConnection connection) {
				return connection.getDictionarySearcher();
			}
		},
		Kanji {
			@Override
			public Searcher getSearcher(SearcherServiceConnection connection) {
				return connection.getKanjiSearcher();
			}
		};
		public abstract Searcher getSearcher(SearcherServiceConnection connection);
	}

	private SearcherServiceConnection connection = new SearcherServiceConnection();

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
	public boolean onSearchRequested() {
		Bundle bundle = new Bundle();
		bundle.putString(SearchActivity.EXTRA_SEARCH_CONTENT, getSearchContent().name());
		startSearch(null, false, bundle, false);
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
			new DictionarySearchTask(getSearchContent()).execute(query);
		}
	}

	private SearchContent getSearchContent() {
		Bundle bundle = getIntent().getBundleExtra(SearchManager.APP_DATA);
		SearchContent searchContent = SearchContent.Dictionary;
		if (bundle != null && bundle.containsKey(EXTRA_SEARCH_CONTENT)) {
			searchContent = SearchContent.valueOf(bundle.getString(EXTRA_SEARCH_CONTENT));
		}
		return searchContent;
	}

	@Override
	public void onItemClick(AdapterView<?> view, View v, int position, long id) {
		Entry entry = (Entry) view.getAdapter().getItem(position);
		if (entry instanceof DictionaryEntry) {
			try {
				Intent intent = new Intent(this, DictionaryEntryActivity.class);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DictionaryEntry.writeEntry(new ObjectOutputStream(out), (DictionaryEntry) entry);
				intent.putExtra(DictionaryEntryActivity.EXTRA_DICTIONARY_ENTRY, out.toByteArray());
				startActivity(intent);
			} catch (IOException e) {
				Log.e(CLASS_NAME, "Failed to serialize entry", e);
			}
		} else if (entry instanceof KanjiEntry) {
			Intent intent = new Intent(this, KanjiEntryActivity.class);
			intent.putExtra(KanjiEntryActivity.EXTRA_KANJI_ENTRY, (KanjiEntry) entry);
			startActivity(intent);
		}
	}

	private class DictionarySearchTask extends AsyncTask<String, Void, List<? extends Entry>> {
		private SearchContent searchContent;

		public DictionarySearchTask(SearchContent searchContent) {
			this.searchContent = searchContent;
		}

		protected List<? extends Entry> doInBackground(String... queries) {
			try {
				String query = queries[0];
				List<? extends Entry> entries = searchContent.getSearcher(connection).search(query);
				if (!entries.isEmpty()) {
					SearchSuggestionProvider.getSearchRecentSuggestions(SearchActivity.this).saveRecentQuery(query, null);
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
