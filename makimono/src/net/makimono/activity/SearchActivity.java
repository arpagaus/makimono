package net.makimono.activity;

import net.makimono.R;
import net.makimono.adapter.SearchResultAdapter;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SearchActivity extends AbstractDefaultActivity implements OnItemClickListener {
	private static final String CLASS_NAME = SearchActivity.class.getName();

	private SearchResultAdapter resultAdapter;
	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result);

		listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemClickListener(this);

		resultAdapter = new SearchResultAdapter(this);
		listView.setAdapter(resultAdapter);

		handleIntent(getIntent());
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
			try {
				resultAdapter.search(query);
			} catch (Exception e) {
				Log.e(CLASS_NAME, "Failed to search", e);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		Intent intent = new Intent(this, EntryActivity.class);
		intent.putExtra("DOC_ID", (int) id);
		startActivity(intent);
	}

}
