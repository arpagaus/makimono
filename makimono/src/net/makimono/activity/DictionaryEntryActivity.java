package net.makimono.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.makimono.R;
import net.makimono.model.DictionaryEntry;
import net.makimono.model.KanjiEntry;
import net.makimono.model.Language;
import net.makimono.model.Sense;
import net.makimono.service.SearcherService;
import net.makimono.service.SearcherServiceConnection;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

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
	private static final String LOG_TAG = DictionaryEntryActivity.class.getSimpleName();

	@SuppressWarnings("serial")
	private static final Map<Language, Integer> LANGUAGE_ICONS = new HashMap<Language, Integer>() {
		{
			put(Language.en, R.drawable.ic_english);
			put(Language.de, R.drawable.ic_german);
			put(Language.fr, R.drawable.ic_french);
			put(Language.ru, R.drawable.ic_russian);
		}
	};

	private SearcherServiceConnection connection = new SearcherServiceConnection();

	private DictionaryEntry entry;

	private AtomicInteger currentExpressionIndex = new AtomicInteger();
	private AtomicInteger currentReadingIndex = new AtomicInteger();

	private LayoutInflater layoutInflater;

	private TextSwitcher expressionTextSwitcher;
	private TextView expressionAlternativeIndTextView;
	private TextSwitcher readingTextSwitcher;
	private TextView readingAlternativeIndTextView;
	private LinearLayout translationsGroupView;
	private LinearLayout kanjisGroupView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bindSearcher();
		initializeView();
		handleIntent(getIntent());
	}

	private void bindSearcher() {
		Intent intent = new Intent(this, SearcherService.class);
		bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	private void initializeView() {
		layoutInflater = LayoutInflater.from(this);

		setContentView(R.layout.dictionary_entry);
		expressionTextSwitcher = createExpressionTextSwitcher();
		expressionAlternativeIndTextView = createExpressionAlternativeIndTextView();
		readingTextSwitcher = createReadingTextSwitcher();
		readingAlternativeIndTextView = createReadingAlternativeIndTextView();
		translationsGroupView = (LinearLayout) findViewById(R.id.entry_translations);
		kanjisGroupView = (LinearLayout) findViewById(R.id.entry_kanjis);
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
		if (intent.hasExtra("DOC_ID")) {
			AsyncTask<Integer, Void, Pair<DictionaryEntry, HashSet<KanjiEntry>>> task = new AsyncTask<Integer, Void, Pair<DictionaryEntry, HashSet<KanjiEntry>>>() {
				protected Pair<DictionaryEntry, HashSet<KanjiEntry>> doInBackground(Integer... docIds) {
					try {
						DictionaryEntry dictionaryEntry = connection.getDictionarySearcher().getByDocId(docIds[0]);

						HashSet<KanjiEntry> kanjiEntries = new HashSet<KanjiEntry>();
						for (String e : dictionaryEntry.getExpressions()) {
							kanjiEntries.addAll(connection.getKanjiSearcher().getKanjiEntries(e));
						}
						return Pair.of(dictionaryEntry, kanjiEntries);
					} catch (IOException e) {
						Log.e(LOG_TAG, "Failed to get dictionary entry", e);
						return null;
					}
				}

				protected void onPostExecute(Pair<DictionaryEntry, HashSet<KanjiEntry>> pair) {
					updateView(pair.getLeft(), pair.getRight());
				}
			};
			task.execute(intent.getExtras().getInt("DOC_ID"));
		}
	}

	private void updateView(DictionaryEntry entry, HashSet<KanjiEntry> kanjiEntries) {
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

		translationsGroupView.removeAllViews();
		for (Sense sense : entry.getSenses()) {
			if (translationsGroupView.getChildCount() > 0) {
				translationsGroupView.addView(createSeparator());
			}
			int glossesCount = addGlosses(sense);
			if (glossesCount > 0) {
				addAdditionalInfo(sense);
			}
		}

		kanjisGroupView.removeAllViews();
		if (kanjiEntries.isEmpty()) {
			findViewById(R.id.entry_separator_kanjis).setVisibility(View.GONE);
			findViewById(R.id.entry_separator_line_kanjis).setVisibility(View.GONE);
			kanjisGroupView.setVisibility(View.GONE);
		} else {
			for (KanjiEntry kanjiEntry : kanjiEntries) {
				if (kanjisGroupView.getChildCount() > 0) {
					kanjisGroupView.addView(createSeparator());
				}
				kanjisGroupView.addView(createKanjiView(kanjiEntry));
			}
		}
	}

	private View createKanjiView(KanjiEntry kanjiEntry) {
		View kanjiView = layoutInflater.inflate(R.layout.search_result_entry, kanjisGroupView, false);
		kanjiView.setPadding(0, getPixelForDip(5), 0, getPixelForDip(5));

		TextView resultExpression = (TextView) kanjiView.findViewById(R.id.result_expression);
		TextView resultReading = (TextView) kanjiView.findViewById(R.id.result_reading);
		TextView resultGloss = (TextView) kanjiView.findViewById(R.id.result_translation);

		resultExpression.setText(kanjiEntry.getLiteral());
		resultReading.setText(StringUtils.join(StringUtils.join(kanjiEntry.getKunYomi(), ", "), StringUtils.join(kanjiEntry.getOnYomi(), ", "), " / "));
		resultGloss.setText(StringUtils.join(kanjiEntry.getGlosses(), ", "));
		return kanjiView;
	}

	private View createSeparator() {
		View separator = new View(this);
		separator.setBackgroundResource(R.drawable.secondary_separator);
		separator.setMinimumHeight(getPixelForDip(1));
		separator.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		return separator;
	}

	private int addGlosses(Sense sense) {
		ArrayList<Language> languages = PreferenceActivity.getConfiguredLanguages(PreferenceManager.getDefaultSharedPreferences(this));

		int glossesCount = 0;
		for (Language lang : languages) {
			CharSequence gloss = sense.getGlossString(lang);
			if (gloss.length() > 0) {
				TextView textView = new TextView(this);
				textView.setText(gloss);
				textView.setCompoundDrawablesWithIntrinsicBounds(LANGUAGE_ICONS.get(lang), 0, 0, 0);
				textView.setCompoundDrawablePadding(getPixelForDip(15));
				textView.setPadding(0, getPixelForDip(5), 0, getPixelForDip(5));
				textView.setGravity(Gravity.CENTER_VERTICAL);
				translationsGroupView.addView(textView);

				glossesCount++;
			}
		}
		return glossesCount;
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
			translationsGroupView.addView(textView);
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
}
