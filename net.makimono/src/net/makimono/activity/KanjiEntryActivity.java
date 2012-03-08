package net.makimono.activity;

import java.util.ArrayList;

import net.makimono.R;
import net.makimono.model.KanjiEntry;
import net.makimono.model.Language;
import net.makimono.model.Meaning;
import net.makimono.util.MeaningTextViewFactory;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KanjiEntryActivity extends AbstractDefaultActivity {
	public static final String EXTRA_KANJI_ENTRY = KanjiEntryActivity.class.getName() + ".EXTRA_KANJI_ENTRY";

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
		initializeContentView();
		handleIntent(getIntent());
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
		}
	}

	private void updateView(KanjiEntry entry) {
		literalTextView.setText(entry.getLiteral());
		onYomiTextView.setText(StringUtils.join(entry.getOnYomi(), ", "));
		kunYomiTextView.setText(StringUtils.join(entry.getKunYomi(), ", "));

		radicalTextView.setText(entry.getRadical() == 0 ? "-" : String.valueOf(entry.getRadical()));
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
}
