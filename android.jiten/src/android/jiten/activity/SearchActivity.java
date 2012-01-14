package android.jiten.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jiten.model.Entry;
import jiten.searcher.Searcher;

import org.apache.lucene.store.SimpleFSDirectory;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.jiten.adapter.ResultAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class SearchActivity extends ListActivity {

	private static final String CLASS_NAME = SearchActivity.class.getName();

	private ResultAdapter resultAdapter;
	private Searcher searcher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			searcher = new Searcher(new SimpleFSDirectory(new File("/mnt/sdcard/dictionary/index/")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		resultAdapter = new ResultAdapter(LayoutInflater.from(this));
		setListAdapter(resultAdapter);

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			doSearch(query);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Entry entry = (Entry) getListAdapter().getItem(position);
		Intent intent = new Intent(this, EntryActivity.class);
		intent.putExtra("DOC_ID", entry.getDocId());
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		searcher.close();
	}

	private void doSearch(String query) {
		try {
			Log.i(CLASS_NAME, "Execute query");
			ArrayList<Entry> entries = searcher.search(query);
			Log.i(CLASS_NAME, "Updating view with " + entries.size() + " entries");
			resultAdapter.replaceEntries(entries);
			Log.i(CLASS_NAME, "Finished search");
		} catch (Exception e) {
			Log.e("DictionaryActivity", "Error", e);
		}
	}

}
