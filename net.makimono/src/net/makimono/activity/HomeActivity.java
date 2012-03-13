package net.makimono.activity;

import net.makimono.R;
import net.makimono.activity.SearchActivity.SearchContent;
import android.os.Bundle;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AbstractDefaultActivity {

	private Button searchKanjiButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);

		searchKanjiButton = (Button) findViewById(R.id.search_kanji);
		searchKanjiButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString(SearchActivity.EXTRA_SEARCH_CONTENT, SearchContent.Kanji.name());
				startSearch(null, false, bundle, false);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
