package net.makimono.activity;

import net.makimono.R;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends AbstractDefaultActivity {

	private TextView searchDictionaryTextView;
	private TextView searchKanjiTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);

		searchDictionaryTextView = (TextView) findViewById(R.id.search_dictionary);
		searchDictionaryTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startSearch(DictionarySearchActivity.class);
			}
		});

		searchKanjiTextView = (TextView) findViewById(R.id.search_kanji);
		searchKanjiTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startSearch(KanjiSearchActivity.class);
			}
		});
	}

	private void startSearch(Class<?> clazz) {
		SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		manager.startSearch(null, false, new ComponentName(HomeActivity.this, clazz), null, false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
