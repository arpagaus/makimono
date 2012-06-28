package net.makimono.dictionary.activity;

import java.util.List;

import net.makimono.dictionary.R;
import net.makimono.dictionary.view.KanjiWritingLayout;
import net.makimono.dictionary.view.KanjiWritingView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class KanjiWritingActivity extends AbstractDefaultActivity {

	private KanjiWritingLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initializeContentView();
		handleIntent(getIntent());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.share, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (super.onOptionsItemSelected(item)) {
			return true;
		} else {
			if (item.getItemId() == R.id.menu_share) {
				View view = this.layout;
				view.setDrawingCacheEnabled(true);
				Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
				view.setDrawingCacheEnabled(false);

				String url = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, getString(R.string.app_name), "Kanji writing");

				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
				shareIntent.setType("image/png");
				startActivity(Intent.createChooser(shareIntent, "Share with"));
				return true;
			}
			return false;
		}

	}

	private void initializeContentView() {
		setContentView(R.layout.kanji_writing);
		this.layout = (KanjiWritingLayout) findViewById(R.id.kanji_writing);
	}

	private void handleIntent(Intent intent) {
		if (intent.hasExtra(net.makimono.dictionary.Intent.EXTRA_STROKE_PATHS)) {
			List<String> strokePaths = intent.getStringArrayListExtra(net.makimono.dictionary.Intent.EXTRA_STROKE_PATHS);
			updateView(strokePaths);
		}
	}

	private void updateView(List<String> strokePaths) {
		layout.removeAllViews();

		for (int i = 0; i < strokePaths.size(); i++) {
			KanjiWritingView kanjiWritingView = new KanjiWritingView(this);
			kanjiWritingView.setStrokePaths(strokePaths);
			kanjiWritingView.setStrokeIndex(i);
			layout.addView(kanjiWritingView);
		}
	}
}
