package net.makimono.activity;

import net.makimono.R;
import net.makimono.model.KanjiEntry;
import net.makimono.service.SearcherService;
import net.makimono.service.SearcherServiceConnection;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class KanjiEntryActivity extends AbstractDefaultActivity {
	public static final String EXTRA_CODE_POINT = KanjiEntryActivity.class.getName() + ".EXTRA_CODE_POINT";

	SearcherServiceConnection connection = new SearcherServiceConnection();

	private TextView literalTextView;
	private TextView onYomiTextView;
	private TextView kunYomiTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeContentView();
		bindSearcher();
		handleIntent(getIntent());
	}

	private void initializeContentView() {
		setContentView(R.layout.kanji_entry);
		literalTextView = (TextView) findViewById(R.id.kanji_literal);
		onYomiTextView = (TextView) findViewById(R.id.kanji_on_yomi);
		kunYomiTextView = (TextView) findViewById(R.id.kanji_kun_yomi);
	}

	private void bindSearcher() {
		Intent intent = new Intent(this, SearcherService.class);
		bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	private void handleIntent(Intent intent) {
		if (intent.hasExtra(EXTRA_CODE_POINT)) {
			int codePoint = intent.getIntExtra(EXTRA_CODE_POINT, 0);
			KanjiEntryTask task = new KanjiEntryTask(this);
			task.execute(codePoint);
		}
	}

	void updateView(KanjiEntry entry) {
		literalTextView.setText(entry.getLiteral());
		onYomiTextView.setText(StringUtils.join(entry.getOnYomi(), ", "));
		kunYomiTextView.setText(StringUtils.join(entry.getKunYomi(), ", "));
	}
}
