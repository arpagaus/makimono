package net.makimono.activity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import net.makimono.R;
import net.makimono.model.KanjiEntry;
import net.makimono.model.Language;
import net.makimono.model.Meaning;
import net.makimono.service.SearcherService;
import net.makimono.service.SearcherServiceConnection;
import net.makimono.util.MeaningTextViewFactory;
import net.makimono.view.KanjiWritingView;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KanjiEntryActivity extends AbstractDefaultActivity {
	public static final String EXTRA_KANJI_ENTRY = KanjiEntryActivity.class.getName() + ".EXTRA_KANJI_ENTRY";
	private final static String LOG_TAG = KanjiEntryActivity.class.getSimpleName();

	private SearcherServiceConnection connection = new SearcherServiceConnection();

	private KanjiEntry entry;

	private TextView literalTextView;
	private KanjiWritingView kanjiAnimationView;
	private TextView kanjiAnimationCopyrightView;
	private TextView onYomiTextView;
	private TextView kunYomiTextView;
	private TextView nanoriTextView;
	private TextView hangulTextView;
	private TextView pinyinTextView;
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
		getSupportActionBar().setTitle(R.string.kanji);

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
		kanjiAnimationView = (KanjiWritingView) findViewById(R.id.kanji_animation);
		kanjiAnimationCopyrightView = (TextView) findViewById(R.id.kanji_animation_copyright);
		onYomiTextView = (TextView) findViewById(R.id.kanji_on_yomi);
		kunYomiTextView = (TextView) findViewById(R.id.kanji_kun_yomi);
		nanoriTextView = (TextView) findViewById(R.id.kanji_nanori);
		hangulTextView = (TextView) findViewById(R.id.kanji_hangul);
		pinyinTextView = (TextView) findViewById(R.id.kanji_pinyin);
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

		kanjiAnimationView.setStrokePaths(entry.getStrokePaths());
		if (entry.getStrokePaths().isEmpty()) {
			kanjiAnimationView.setVisibility(View.GONE);
			kanjiAnimationCopyrightView.setVisibility(View.GONE);
		} else {
			kanjiAnimationView.setVisibility(View.VISIBLE);
			kanjiAnimationCopyrightView.setVisibility(View.VISIBLE);
		}

		literalTextView.setText(entry.getLiteral());
		onYomiTextView.setText(StringUtils.join(entry.getOnYomi(), ", "));
		kunYomiTextView.setText(StringUtils.join(entry.getKunYomi(), ", "));
		nanoriTextView.setText(StringUtils.join(entry.getNanori(), ", "));
		hangulTextView.setText(StringUtils.join(entry.getHangul(), ", "));
		pinyinTextView.setText(StringUtils.join(entry.getPinyin(), ", "));

		updateRadicalTextView(entry, null);
		strokeCountTextView.setText(entry.getStrokeCount() == 0 ? "-" : String.valueOf(entry.getStrokeCount()));
		jlptTextView.setText(entry.getJlpt() == 0 ? "-" : String.valueOf(entry.getJlpt()));
		gradeTextView.setText(entry.getGrade() == 0 ? "-" : String.valueOf(entry.getGrade()));
		frequencyTextView.setText(entry.getFrequency() == 0 ? "-" : String.valueOf(entry.getFrequency()));
		unicodeTextView.setText("U+" + Integer.toHexString(entry.getCodePoint()).toUpperCase());

		MeaningTextViewFactory factory = new MeaningTextViewFactory(this);
		meaningsGroupView.removeAllViews();
		List<Language> languages = PreferenceActivity.getConfiguredLanguages(PreferenceManager.getDefaultSharedPreferences(this));
		for (Language language : languages) {
			CharSequence meaning = entry.getMeaningSummary(Collections.singletonList(language));
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
