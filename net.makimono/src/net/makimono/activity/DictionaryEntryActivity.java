package net.makimono.activity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.makimono.R;
import net.makimono.listener.KanjiViewListener;
import net.makimono.model.DictionaryEntry;
import net.makimono.model.KanjiEntry;
import net.makimono.model.Language;
import net.makimono.model.Meaning;
import net.makimono.model.Sense;
import net.makimono.service.SearcherService;
import net.makimono.service.SearcherServiceConnection;
import net.makimono.util.MeaningTextViewFactory;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class DictionaryEntryActivity extends AbstractDefaultActivity {
	public static final String EXTRA_DICTIONARY_ENTRY = DictionaryEntryActivity.class + ".EXTRA_DOC_ID";

	private SearcherServiceConnection connection = new SearcherServiceConnection();

	private DictionaryEntry entry;

	private AtomicInteger currentExpressionIndex = new AtomicInteger();
	private AtomicInteger currentReadingIndex = new AtomicInteger();

	private LayoutInflater layoutInflater;

	private TextSwitcher expressionTextSwitcher;
	private TextView expressionAlternativeIndTextView;
	private TextSwitcher readingTextSwitcher;
	private TextView readingAlternativeIndTextView;
	private LinearLayout meaningsGroupView;
	private LinearLayout kanjiGroupView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		expressionAlternativeIndTextView = createExpressionAlternativeIndTextView();
		readingTextSwitcher = createReadingTextSwitcher();
		readingAlternativeIndTextView = createReadingAlternativeIndTextView();
		meaningsGroupView = (LinearLayout) findViewById(R.id.entry_meanings);
		kanjiGroupView = (LinearLayout) findViewById(R.id.entry_kanji);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(connection);
	}

	private TextView createReadingAlternativeIndTextView() {
		readingAlternativeIndTextView = (TextView) findViewById(R.id.entry_reading_alternative_ind);
		readingAlternativeIndTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showNextReading();
			}
		});
		return readingAlternativeIndTextView;
	}

	private TextSwitcher createReadingTextSwitcher() {
		readingTextSwitcher = (TextSwitcher) findViewById(R.id.entry_reading);
		readingTextSwitcher.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				TextView textView = new TextView(DictionaryEntryActivity.this);
				textView.setTextColor(Color.GRAY);
				return textView;
			}
		});
		readingTextSwitcher.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showNextReading();
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
				textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
				textView.setGravity(Gravity.CENTER_HORIZONTAL);
				return textView;
			}
		});
		expressionTextSwitcher.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showNextExpression();
			}
		});
		return expressionTextSwitcher;
	}

	private TextView createExpressionAlternativeIndTextView() {
		expressionAlternativeIndTextView = (TextView) findViewById(R.id.entry_expression_alternative_ind);
		expressionAlternativeIndTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showNextExpression();
			}
		});
		return expressionAlternativeIndTextView;
	}

	private void showNextExpression() {
		if (entry != null) {
			showNextAlternative(entry.getExpressions(), expressionTextSwitcher, expressionAlternativeIndTextView, currentExpressionIndex);
			currentReadingIndex.set(-1);
			showNextReading();
		}
	}

	private void showNextReading() {
		if (entry != null) {
			ArrayList<String> readings;
			if (entry.getExpressions().isEmpty()) {
				readings = entry.getReadings();
			} else {
				readings = entry.getReadings(entry.getExpressions().get(currentExpressionIndex.get()));
			}
			showNextAlternative(readings, readingTextSwitcher, readingAlternativeIndTextView, currentReadingIndex);
		}
	}

	private void showNextAlternative(ArrayList<String> alternatives, TextSwitcher textSwitcher, TextView indTextView, AtomicInteger index) {
		if (alternatives.size() > 1) {
			index.set(index.incrementAndGet() % alternatives.size());
			textSwitcher.setText(alternatives.get(index.get()));
			indTextView.setText("(" + (index.get() + 1) + "/" + alternatives.size() + ")");
			indTextView.setVisibility(View.VISIBLE);
		} else if (alternatives.size() == 1) {
			index.set(0);
			textSwitcher.setText(alternatives.get(0));
			indTextView.setVisibility(View.GONE);
		}
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
		this.entry = entry;
		currentExpressionIndex.set(0);
		currentReadingIndex.set(0);

		String reading = entry.getReadings().get(0);
		if (entry.getExpressions().isEmpty()) {
			expressionTextSwitcher.setText(reading);
			readingTextSwitcher.setVisibility(View.GONE);
		} else {
			expressionTextSwitcher.setText(entry.getExpressions().get(0));
			readingTextSwitcher.setVisibility(View.VISIBLE);
			readingTextSwitcher.setText(reading);
		}

		currentExpressionIndex.set(-1);
		showNextExpression();

		meaningsGroupView.removeAllViews();
		for (Sense sense : entry.getSenses()) {
			if (meaningsGroupView.getChildCount() > 0) {
				meaningsGroupView.addView(createSeparator());
			}
			int meaningsCount = addMeanings(sense);
			if (meaningsCount > 0) {
				addAdditionalInfo(sense);
			}
		}
	}

	private void updateKanjiView(HashSet<KanjiEntry> kanjiEntries) {
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
		kanjiView.setPadding(0, getPixelForDip(5), 0, getPixelForDip(5));

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
		separator.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		return separator;
	}

	private int addMeanings(Sense sense) {
		MeaningTextViewFactory factory = new MeaningTextViewFactory(this);
		int meaningsCount = 0;
		for (Language language : getConfiguredLanguages()) {
			CharSequence meaning = Meaning.getMeaningString(language, sense.getMeanings());
			if (meaning.length() > 0) {
				meaningsGroupView.addView(factory.makeView(meaning, language));
				meaningsCount++;
			}
		}
		return meaningsCount;
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
			textView.setPadding(0, getPixelForDip(5), 0, getPixelForDip(5));
			textView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
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

	private class LoadKanjiEntriesTask extends AsyncTask<DictionaryEntry, Void, HashSet<KanjiEntry>> {

		protected HashSet<KanjiEntry> doInBackground(DictionaryEntry... dictionaryEntries) {
			try {
				DictionaryEntry dictionaryEntry = dictionaryEntries[0];

				HashSet<KanjiEntry> kanjiEntries = new HashSet<KanjiEntry>();
				for (String e : dictionaryEntry.getExpressions()) {
					kanjiEntries.addAll(connection.getKanjiSearcher().getKanjiEntries(e));
				}
				return kanjiEntries;
			} catch (IOException e) {
				Log.e(LoadKanjiEntriesTask.class.getSimpleName(), "Failed to get kanji entry", e);
				return null;
			}
		}

		protected void onPostExecute(HashSet<KanjiEntry> kanji) {
			updateKanjiView(kanji);
		}
	}
}
