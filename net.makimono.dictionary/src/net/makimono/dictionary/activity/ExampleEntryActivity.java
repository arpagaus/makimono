package net.makimono.dictionary.activity;

import java.util.List;

import net.makimono.dictionary.R;
import net.makimono.dictionary.model.ExampleEntry;
import net.makimono.dictionary.model.Language;
import net.makimono.dictionary.model.Meaning;
import net.makimono.dictionary.util.MeaningTextViewFactory;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExampleEntryActivity extends AbstractDefaultActivity {

	private TextView japaneseTextView;
	private LinearLayout meaningsGroupView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.example);

		initializeContentView();
		handleIntent(getIntent());
	}

	private void initializeContentView() {
		setContentView(R.layout.example_entry);

		japaneseTextView = (TextView) findViewById(R.id.japanese_sentence);
		meaningsGroupView = (LinearLayout) findViewById(R.id.meanings);
	}

	private void handleIntent(Intent intent) {
		if (intent.hasExtra(net.makimono.dictionary.Intent.EXTRA_EXAMPLE_ENTRY)) {
			ExampleEntry entry = intent.getParcelableExtra(net.makimono.dictionary.Intent.EXTRA_EXAMPLE_ENTRY);
			updateView(entry);
		}
	}

	private void updateView(ExampleEntry entry) {
		japaneseTextView.setText(StringUtils.defaultString(entry.getJapaneseMeaning().getValue()));

		MeaningTextViewFactory factory = new MeaningTextViewFactory(this);
		meaningsGroupView.removeAllViews();
		List<Language> languages = PreferenceFragment.getConfiguredLanguages(PreferenceManager.getDefaultSharedPreferences(this));
		for (Language language : languages) {
			Meaning meaning = entry.getMeaning(language);
			if (meaning != null) {
				TextView textView = factory.makeView(meaning.getValue(), language);
				meaningsGroupView.addView(textView);
			}
		}
	}
}
