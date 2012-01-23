package android.jiten.activity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jiten.model.Entry;
import jiten.model.Language;
import jiten.model.Sense;
import jiten.searcher.Searcher;

import org.apache.lucene.store.SimpleFSDirectory;

import android.content.Intent;
import android.jiten.R;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EntryActivity extends FragmentActivity {

	@SuppressWarnings("serial")
	private static final Map<Language, Integer> LANGUAGE_ICONS = new HashMap<Language, Integer>() {
		{
			put(Language.en, R.drawable.gb);
			put(Language.de, R.drawable.de);
			put(Language.fr, R.drawable.fr);
			put(Language.ru, R.drawable.ru);
		}
	};

	private TextView expressionTextView;
	private TextView readingTextView;

	private LinearLayout translationsGroupView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dictionary_entry);
		expressionTextView = (TextView) findViewById(R.id.entry_expression);
		readingTextView = (TextView) findViewById(R.id.entry_reading);
		translationsGroupView = (LinearLayout) findViewById(R.id.entry_translations);

		handleIntent(getIntent());
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
		String reading = entry.getReadings().get(0);
		if (entry.getExpressions().isEmpty()) {
			expressionTextView.setText(reading);
		} else {
			expressionTextView.setText(entry.getExpressions().get(0));
		}
		readingTextView.setText(reading);

		translationsGroupView.removeAllViews();
		for (Sense sense : entry.getSenses()) {
			if (translationsGroupView.getChildCount() > 0) {
				translationsGroupView.addView(createSeparator());
			}

			addGlosses(sense);

			TextView textView = new TextView(this);
			textView.setText("Ideomatic" /* sense.getPartsOfSpeech().toString() */);
			textView.setTextColor(android.R.color.darker_gray);
			translationsGroupView.addView(textView);
		}
	}

	private View createSeparator() {
		View separator = new View(this);
		separator.setBackgroundColor(android.R.color.darker_gray);
		separator.setMinimumHeight(2);
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

	private int getPixelForDip(int dip) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) dip, getResources().getDisplayMetrics());

	}
}
