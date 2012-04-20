package net.makimono.activity;

import net.makimono.R;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class HomeActivity extends AbstractDefaultActivity {

	private View searchDictionaryTextView;
	private View searchKanjiTextView;
	private View settingsTextView;
	private View aboutTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		searchDictionaryTextView = (View) findViewById(R.id.search_dictionary);
		searchDictionaryTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startSearch(DictionarySearchActivity.class);
			}
		});

		searchKanjiTextView = (View) findViewById(R.id.search_kanji);
		searchKanjiTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startSearch(KanjiSearchActivity.class);
			}
		});

		settingsTextView = (View) findViewById(R.id.settings);
		settingsTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, PreferenceActivity.class);
				startActivity(intent);
			}
		});

		aboutTextView = (View) findViewById(R.id.about);
		aboutTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
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
