package net.makimono.activity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import net.makimono.R;
import net.makimono.listener.KanjiViewListener;
import net.makimono.model.DictionaryEntry;
import net.makimono.model.KanjiEntry;
import net.makimono.model.Language;
import net.makimono.model.Sense;
import net.makimono.service.SearcherService;
import net.makimono.service.SearcherServiceConnection;
import net.makimono.util.DictionaryAlternativesSwitcher;
import net.makimono.util.MeaningTextViewFactory;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class DictionaryEntryActivity extends AbstractDefaultActivity {
	public static final String EXTRA_DICTIONARY_ENTRY = DictionaryEntryActivity.class + ".EXTRA_DOC_ID";

	private SearcherServiceConnection connection = new SearcherServiceConnection();

	private LayoutInflater layoutInflater;

	private DictionaryAlternativesSwitcher alternativesSwitcher;

	private TextSwitcher expressionTextSwitcher;
	private TextSwitcher readingTextSwitcher;
	private LinearLayout meaningsGroupView;
	private LinearLayout kanjiGroupView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.dictionary);

		bindSearcher();
		initializeContentView();
		handleIntent(getIntent());
	}

	private void bindSearcher() {
		Intent intent = new Intent(this, SearcherService.class);
		bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	private void initializeContentView() {
		layoutInflater = LayoutInflater.from(this);

		setContentView(R.layout.dictionary_entry);
		expressionTextSwitcher = createExpressionTextSwitcher();
		readingTextSwitcher = createReadingTextSwitcher();
		meaningsGroupView = (LinearLayout) findViewById(R.id.entry_meanings);
		kanjiGroupView = (LinearLayout) findViewById(R.id.entry_kanji);

		alternativesSwitcher = new DictionaryAlternativesSwitcher(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(connection);
	}

	private TextSwitcher createReadingTextSwitcher() {
		readingTextSwitcher = (TextSwitcher) findViewById(R.id.entry_reading);
		readingTextSwitcher.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				TextView textView = new TextView(DictionaryEntryActivity.this);
				textView.setTextColor(Color.GRAY);
				textView.setTextSize(24);
				return textView;
			}
		});
		return readingTextSwitcher;
	}

	private TextSwitcher createExpressionTextSwitcher() {
		expressionTextSwitcher = (TextSwitcher) findViewById(R.id.entry_expression);
		expressionTextSwitcher.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				TextView textView = new TextView(DictionaryEntryActivity.this);
				textView.setTextSize(32);
				textView.setGravity(Gravity.CENTER_HORIZONTAL);
				return textView;
			}
		});
		return expressionTextSwitcher;
	}

	private void handleIntent(Intent intent) {
		if (intent.hasExtra(EXTRA_DICTIONARY_ENTRY)) {
			ByteArrayInputStream in = new ByteArrayInputStream(intent.getExtras().getByteArray(EXTRA_DICTIONARY_ENTRY));
			try {
				DictionaryEntry entry = DictionaryEntry.readEntry(new ObjectInputStream(in));
				updateView(entry);

				new LoadKanjiEntriesTask().execute(entry);
			} catch (Exception e) {
				Log.e(DictionaryEntryActivity.class.getSimpleName(), "Failed to deserialize entry", e);
			}
		}
	}

	private void updateView(DictionaryEntry entry) {
		alternativesSwitcher.updateEntry(entry);

		meaningsGroupView.removeAllViews();
		for (Sense sense : entry.getSenses()) {
			if (sense.hasMeaningsForLanguage(getConfiguredLanguages())) {
				if (meaningsGroupView.getChildCount() > 0) {
					View separator = createSeparator();
					((LinearLayout.LayoutParams) separator.getLayoutParams()).bottomMargin = getPixelForDip(8);
					((LinearLayout.LayoutParams) separator.getLayoutParams()).topMargin = getPixelForDip(8);
					meaningsGroupView.addView(separator);
				}
				addAdditionalInfo(sense);
				addMeanings(sense);
			}
		}
	}

	private void updateKanjiView(List<KanjiEntry> kanjiEntries) {
		kanjiGroupView.removeAllViews();
		if (kanjiEntries.isEmpty()) {
			findViewById(R.id.entry_separator_kanji).setVisibility(View.GONE);
			findViewById(R.id.entry_separator_line_kanji).setVisibility(View.GONE);
			kanjiGroupView.setVisibility(View.GONE);
		} else {
			for (KanjiEntry kanjiEntry : kanjiEntries) {
				if (kanjiGroupView.getChildCount() > 0) {
					kanjiGroupView.addView(createSeparator());
				}
				kanjiGroupView.addView(createKanjiView(kanjiEntry));
			}
		}
	}

	private View createKanjiView(KanjiEntry kanjiEntry) {
		View kanjiView = layoutInflater.inflate(R.layout.search_result_entry, kanjiGroupView, false);
		kanjiView.setBackgroundResource(R.drawable.clickable_background);
		kanjiView.setPadding(getPixelForDip(10), getPixelForDip(5), getPixelForDip(10), getPixelForDip(5));

		TextView resultExpression = (TextView) kanjiView.findViewById(R.id.result_expression);
		TextView resultReading = (TextView) kanjiView.findViewById(R.id.result_reading);
		TextView resultMeaning = (TextView) kanjiView.findViewById(R.id.result_meaning);

		resultExpression.setText(kanjiEntry.getLiteral());
		resultReading.setText(kanjiEntry.getReadingSummary());
		resultMeaning.setText(kanjiEntry.getMeaningSummary(getConfiguredLanguages()));

		kanjiView.setOnClickListener(new KanjiViewListener(this, kanjiEntry));
		return kanjiView;
	}

	private View createSeparator() {
		View separator = new View(this);
		separator.setBackgroundResource(R.drawable.secondary_separator);
		separator.setMinimumHeight(getPixelForDip(1));
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		separator.setLayoutParams(layoutParams);
		layoutParams.leftMargin = getPixelForDip(10);
		layoutParams.rightMargin = getPixelForDip(10);
		return separator;
	}

	private void addMeanings(Sense sense) {
		MeaningTextViewFactory factory = new MeaningTextViewFactory(this);
		for (Language language : getConfiguredLanguages()) {
			CharSequence meaning = StringUtils.join(sense.getMeanings(language), ", ");
			if (meaning.length() > 0) {
				meaningsGroupView.addView(factory.makeView(meaning, language));
			}
		}
	}

	private List<Language> getConfiguredLanguages() {
		return PreferenceActivity.getConfiguredLanguages(PreferenceManager.getDefaultSharedPreferences(this));
	}

	private void addAdditionalInfo(Sense sense) {
		StringBuilder additionalInfo = new StringBuilder();
		for (String s : sense.getAdditionalInfo()) {
			if (additionalInfo.length() > 0) {
				additionalInfo.append(", ");
			}
			additionalInfo.append(getStringForName(s));
		}

		if (additionalInfo.length() > 0) {
			TextView textView = new TextView(this);
			textView.setText(additionalInfo);
			textView.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC), Typeface.ITALIC);
			textView.setTextColor(Color.GRAY);
			textView.setPadding(getPixelForDip(10), getPixelForDip(5), getPixelForDip(10), 0);
			textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			meaningsGroupView.addView(textView);
		}
	}

	private String getStringForName(String name) {
		try {
			return getResources().getString(getResources().getIdentifier(name, "string", getPackageName()));
		} catch (RuntimeException e) {
			return name;
		}
	}

	private int getPixelForDip(int dip) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) dip, getResources().getDisplayMetrics());
	}

	private class LoadKanjiEntriesTask extends AsyncTask<DictionaryEntry, Void, List<KanjiEntry>> {

		protected List<KanjiEntry> doInBackground(DictionaryEntry... dictionaryEntries) {
			try {
				DictionaryEntry dictionaryEntry = dictionaryEntries[0];

				String expressions = StringUtils.join(dictionaryEntry.getExpressions(), "");
				return connection.getKanjiSearcher().getKanjiEntries(expressions);
			} catch (IOException e) {
				Log.e(LoadKanjiEntriesTask.class.getSimpleName(), "Failed to get kanji entry", e);
				return null;
			}
		}

		protected void onPostExecute(List<KanjiEntry> kanji) {
			updateKanjiView(kanji);
		}
	}
}
