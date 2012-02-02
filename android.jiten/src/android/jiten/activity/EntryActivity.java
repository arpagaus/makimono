package android.jiten.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import jiten.model.Dialect;
import jiten.model.Entry;
import jiten.model.Language;
import jiten.model.PartOfSpeech;
import jiten.model.Sense;
import jiten.searcher.Searcher;

import org.apache.lucene.store.SimpleFSDirectory;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.jiten.R;
import android.os.Bundle;
import android.os.Environment;
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

	@SuppressWarnings("serial")
	private static final Map<Language, Integer> LANGUAGE_ICONS = new HashMap<Language, Integer>() {
		{
			put(Language.en, R.drawable.gb);
			put(Language.de, R.drawable.de);
			put(Language.fr, R.drawable.fr);
			put(Language.ru, R.drawable.ru);
		}
	};

	
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

		setContentView(R.layout.dictionary_entry);
		expressionTextSwitcher = createExpressionTextSwitcher();
		expressionAlternativeIndTextView = createExpressionAlternativeIndTextView();
		readingTextSwitcher = createReadingTextSwitcher();
		readingAlternativeIndTextView = createReadingAlternativeIndTextView();
		translationsGroupView = (LinearLayout) findViewById(R.id.entry_translations);

		handleIntent(getIntent());
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
			Searcher searcher = null;
			try {
				String storageState = Environment.getExternalStorageState();
				if (Environment.MEDIA_MOUNTED.equals(storageState) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState)) {
					File directory = new File(this.getExternalFilesDir(null), "/indexes/dictionary/");
					searcher = new Searcher(new SimpleFSDirectory(directory));
					Entry entry = searcher.getByDocId(intent.getExtras().getInt("DOC_ID"));
					updateView(entry);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				if (searcher != null) {
					try {
						searcher.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
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
			addGlosses(sense);
			addAdditionalInfo(sense);
		}
	}

	private View createSeparator() {
		View separator = new View(this);
		separator.setBackgroundResource(R.drawable.secondary_separator);
		separator.setMinimumHeight(getPixelForDip(1));
		separator.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		return separator;
	}

	private void addGlosses(Sense sense) {
		for (Language lang : Language.values()) {
			CharSequence gloss = sense.getGlossString(lang);
			if (gloss.length() > 0) {
				TextView textView = new TextView(this);
				textView.setText(gloss);
				textView.setCompoundDrawablesWithIntrinsicBounds(LANGUAGE_ICONS.get(lang), 0, 0, 0);
				textView.setCompoundDrawablePadding(getPixelForDip(15));
				textView.setPadding(0, getPixelForDip(5), 0, getPixelForDip(5));
				textView.setGravity(Gravity.CENTER_VERTICAL);
				translationsGroupView.addView(textView);
			}
		}
	}

	private void addAdditionalInfo(Sense sense) {
		StringBuilder additionalInfo = new StringBuilder();
		for (PartOfSpeech pos : sense.getPartsOfSpeech()) {
			if (additionalInfo.length() > 0) {
				additionalInfo.append(", ");
			}
			additionalInfo.append(getStringForName(pos.name()));
		}

		for (Dialect d : sense.getDialects()) {
			if (additionalInfo.length() > 0) {
				additionalInfo.append(", ");
			}
			additionalInfo.append(getStringForName(d.name()));
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
		return getResources().getString(getResources().getIdentifier(name, "string", getPackageName()));
	}

	private int getPixelForDip(int dip) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) dip, getResources().getDisplayMetrics());

	}
}
