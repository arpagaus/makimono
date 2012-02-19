package net.makimono.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.makimono.R;
import net.makimono.model.Entry;
import net.makimono.model.Language;
import net.makimono.model.Sense;
import net.makimono.service.SearcherService;
import net.makimono.service.SearcherServiceConnection;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class EntryActivity extends AbstractDefaultActivity {
	private static final String LOG_TAG = EntryActivity.class.getSimpleName();

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

	private Entry entry;

	private AtomicInteger currentExpressionIndex = new AtomicInteger();
	private AtomicInteger currentReadingIndex = new AtomicInteger();

	private TextSwitcher expressionTextSwitcher;
	private TextView expressionAlternativeIndTextView;

	private TextSwitcher readingTextSwitcher;
	private TextView readingAlternativeIndTextView;

	private LinearLayout translationsGroupView;

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
		setContentView(R.layout.dictionary_entry);
		expressionTextSwitcher = createExpressionTextSwitcher();
		expressionAlternativeIndTextView = createExpressionAlternativeIndTextView();
		readingTextSwitcher = createReadingTextSwitcher();
		readingAlternativeIndTextView = createReadingAlternativeIndTextView();
		translationsGroupView = (LinearLayout) findViewById(R.id.entry_translations);
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
				TextView textView = new TextView(EntryActivity.this);
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
				TextView textView = new TextView(EntryActivity.this);
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
		}
	}

	private void showNextReading() {
		if (entry != null) {
			showNextAlternative(entry.getReadings(), readingTextSwitcher, readingAlternativeIndTextView, currentReadingIndex);
		}
	}

	private void showNextAlternative(ArrayList<String> alternatives, TextSwitcher textSwitcher, TextView indTextView, AtomicInteger index) {
		if (alternatives.size() > 1) {
			index.set(index.incrementAndGet() % alternatives.size());
			textSwitcher.setText(alternatives.get(index.get()));
			indTextView.setVisibility(View.VISIBLE);
			indTextView.setText("(" + (index.get() + 1) + "/" + alternatives.size() + ")");
		}
	}

	private void handleIntent(Intent intent) {
		if (intent.hasExtra("DOC_ID")) {
			AsyncTask<Integer, Void, Entry> task = new AsyncTask<Integer, Void, Entry>() {
				protected Entry doInBackground(Integer... docIds) {
					try {
						return connection.getSearcher().getByDocId(docIds[0]);
					} catch (IOException e) {
						Log.e(LOG_TAG, "Failed to get dictionary entry", e);
						return null;
					}
				}

				protected void onPostExecute(Entry entry) {
					updateView(entry);
				}
			};
			task.execute(intent.getExtras().getInt("DOC_ID"));
		}
	}

	private void updateView(Entry entry) {
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

		if (entry.getExpressions().size() < 2) {
			expressionAlternativeIndTextView.setVisibility(View.GONE);
		} else {
			expressionAlternativeIndTextView.setVisibility(View.VISIBLE);
		}

		if (entry.getReadings().size() < 2) {
			readingAlternativeIndTextView.setVisibility(View.GONE);
		} else {
			readingAlternativeIndTextView.setVisibility(View.VISIBLE);
		}

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
