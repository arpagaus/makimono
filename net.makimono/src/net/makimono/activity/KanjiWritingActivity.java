package net.makimono.activity;

import java.util.List;

import net.makimono.view.KanjiWritingLayout;
import net.makimono.view.KanjiWritingView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class KanjiWritingActivity extends Activity {

	private KanjiWritingLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initializeContentView();
		handleIntent(getIntent());
	}

	private void initializeContentView() {
		layout = new KanjiWritingLayout(this);
		setContentView(layout);
	}

	private void handleIntent(Intent intent) {
		if (intent.hasExtra(net.makimono.Intent.EXTRA_STROKE_PATHS)) {
			List<String> strokePaths = intent.getStringArrayListExtra(net.makimono.Intent.EXTRA_STROKE_PATHS);
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
