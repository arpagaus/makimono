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
