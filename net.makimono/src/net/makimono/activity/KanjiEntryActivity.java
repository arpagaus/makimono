package net.makimono.activity;

import java.io.IOException;
import java.util.ArrayList;

import net.makimono.R;
import net.makimono.model.KanjiEntry;
import net.makimono.model.Language;
import net.makimono.model.Meaning;
import net.makimono.service.SearcherService;
import net.makimono.service.SearcherServiceConnection;
import net.makimono.util.MeaningTextViewFactory;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KanjiEntryActivity extends AbstractDefaultActivity {
	public static final String EXTRA_KANJI_ENTRY = KanjiEntryActivity.class.getName() + ".EXTRA_KANJI_ENTRY";
	private final static String LOG_TAG = KanjiEntryActivity.class.getSimpleName();

	private SearcherServiceConnection connection = new SearcherServiceConnection();

	private KanjiEntry entry;

	private TextView literalTextView;
	private TextView onYomiTextView;
	private TextView kunYomiTextView;
	private TextView radicalTextView;
	private TextView strokeCountTextView;
	private TextView jlptTextView;
	private TextView gradeTextView;
	private TextView frequencyTextView;
	private TextView unicodeTextView;
	private LinearLayout meaningsGroupView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bindSearcher();
		initializeContentView();
		handleIntent(getIntent());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(connection);
	}

	private void bindSearcher() {
		Intent intent = new Intent(this, SearcherService.class);
		bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	private void initializeContentView() {
		setContentView(R.layout.kanji_entry);
		literalTextView = (TextView) findViewById(R.id.kanji_literal);
		onYomiTextView = (TextView) findViewById(R.id.kanji_on_yomi);
		kunYomiTextView = (TextView) findViewById(R.id.kanji_kun_yomi);
		radicalTextView = (TextView) findViewById(R.id.kanji_radical);
		strokeCountTextView = (TextView) findViewById(R.id.kanji_stroke_count);
		jlptTextView = (TextView) findViewById(R.id.kanji_jlpt);
		gradeTextView = (TextView) findViewById(R.id.kanji_grade);
		frequencyTextView = (TextView) findViewById(R.id.kanji_frequency);
		unicodeTextView = (TextView) findViewById(R.id.kanji_unicode);

		meaningsGroupView = (LinearLayout) findViewById(R.id.kanji_meanings);
	}

	private void handleIntent(Intent intent) {
		if (intent.hasExtra(EXTRA_KANJI_ENTRY)) {
			KanjiEntry entry = intent.getParcelableExtra(EXTRA_KANJI_ENTRY);
			updateView(entry);

			new LoadRadicalTask().execute(entry.getRadicalKanji());
		}
	}

	private void updateView(KanjiEntry entry) {
		this.entry = entry;

		literalTextView.setText(entry.getLiteral());
		onYomiTextView.setText(StringUtils.join(entry.getOnYomi(), ", "));
		kunYomiTextView.setText(StringUtils.join(entry.getKunYomi(), ", "));

		updateRadicalTextView(entry, null);
		strokeCountTextView.setText(entry.getStrokeCount() == 0 ? "-" : String.valueOf(entry.getStrokeCount()));
		jlptTextView.setText(entry.getJlpt() == 0 ? "-" : String.valueOf(entry.getJlpt()));
		gradeTextView.setText(entry.getGrade() == 0 ? "-" : String.valueOf(entry.getGrade()));
		frequencyTextView.setText(entry.getFrequency() == 0 ? "-" : String.valueOf(entry.getFrequency()));
		unicodeTextView.setText("U+" + Integer.toHexString(entry.getCodePoint()).toUpperCase());

		MeaningTextViewFactory factory = new MeaningTextViewFactory(this);
		meaningsGroupView.removeAllViews();
		ArrayList<Language> languages = PreferenceActivity.getConfiguredLanguages(PreferenceManager.getDefaultSharedPreferences(this));
		for (Language language : languages) {
			CharSequence meaning = Meaning.getMeaningString(language, entry.getMeanings());
			if (meaning.length() > 0) {
				meaningsGroupView.addView(factory.makeView(meaning, language));
			}
		}
	}

	private void updateRadicalTextView(KanjiEntry kanji, KanjiEntry radical) {
		StringBuilder text = new StringBuilder();
		if (kanji.getRadical() == 0) {
			text.append('-');
		} else {
			text.append(kanji.getRadicalKanji());
			text.append(" [");
			text.append(kanji.getRadicalKana());
			text.append("]");
		}
		if (radical != null) {
			for (Meaning m : radical.getMeanings()) {
				if (m.getLanguage() == Language.en) {
					text.append('\n');
					text.append(m.getValue());
					break;
				}
			}
		}
		radicalTextView.setText(text);
	}

	private class LoadRadicalTask extends AsyncTask<Character, Void, KanjiEntry> {

		@Override
		protected KanjiEntry doInBackground(Character... params) {
			try {
				return connection.getKanjiSearcher().getKanjiEntry(String.valueOf(params[0]));
			} catch (IOException e) {
				Log.e(LOG_TAG, "Failed to load radical", e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(KanjiEntry result) {
			if (result == null) {
				return;
			} else {
				updateRadicalTextView(entry, result);
			}
		}
	}
}
