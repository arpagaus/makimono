package net.makimono.dictionary.activity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;
import net.makimono.dictionary.R;
import net.makimono.dictionary.adapter.SearchResultAdapter;
import net.makimono.dictionary.model.DictionaryEntry;
import net.makimono.dictionary.model.KanjiEntry;
import net.makimono.dictionary.model.Language;
import net.makimono.dictionary.model.Sense;
import net.makimono.dictionary.service.SearcherService;
import net.makimono.dictionary.service.SearcherServiceConnection;
import net.makimono.dictionary.util.DictionaryAlternativesSwitcher;
import net.makimono.dictionary.util.MeaningTextViewFactory;
import net.makimono.dictionary.util.TypedValueUtil;
import net.makimono.dictionary.view.NonScrollingListView;

public class DictionaryEntryActivity extends AbstractDefaultActivity {
	private static final String LOG_TAG = DictionaryEntryActivity.class.getSimpleName();

	public static final String EXTRA_DICTIONARY_ENTRY = DictionaryEntryActivity.class + ".EXTRA_DOC_ID";

	private SearcherServiceConnection connection = new SearcherServiceConnection();

	private DictionaryAlternativesSwitcher alternativesSwitcher;

	private TextSwitcher expressionTextSwitcher;
	private TextSwitcher readingTextSwitcher;
	private LinearLayout meaningsGroupView;
	private NonScrollingListView kanjiListView;

	private SearchResultAdapter kanjiResultAdapter;

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
		setContentView(R.layout.dictionary_entry);
		expressionTextSwitcher = createExpressionTextSwitcher();
		readingTextSwitcher = createReadingTextSwitcher();
		meaningsGroupView = (LinearLayout) findViewById(R.id.entry_meanings);
		kanjiListView = (NonScrollingListView) findViewById(R.id.entry_kanji);

		kanjiResultAdapter = new SearchResultAdapter(this);
		kanjiListView.setAdapter(kanjiResultAdapter);
		kanjiListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long id) {
				Intent intent = new Intent(DictionaryEntryActivity.this, KanjiEntryActivity.class);
				intent.putExtra(KanjiEntryActivity.EXTRA_KANJI_ENTRY, (KanjiEntry) kanjiResultAdapter.getItem(index));
				startActivity(intent);
			}
		});

		alternativesSwitcher = new DictionaryAlternativesSwitcher(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(connection);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search_example, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_search_example:
			if (expressionTextSwitcher.getCurrentView() instanceof TextView) {
				CharSequence expression = ((TextView) expressionTextSwitcher.getCurrentView()).getText();
				if (StringUtils.isNotBlank(expression)) {
					Intent intent = new Intent(this, ExampleSearchFragment.class);
					intent.setAction(Intent.ACTION_SEARCH);
					intent.putExtra(SearchManager.QUERY, expression);
					startActivity(intent);
					return true;
				}
			}
			Log.e(LOG_TAG, "Failed to search examples, expressionTextSwitcher=" + expressionTextSwitcher.getCurrentView());
			return false;
		}
		return super.onOptionsItemSelected(item);
	}

	private TextSwitcher createReadingTextSwitcher() {
		readingTextSwitcher = (TextSwitcher) findViewById(R.id.entry_reading);
		readingTextSwitcher.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				TextView textView = new net.makimono.dictionary.view.TextView(DictionaryEntryActivity.this);
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
				TextView textView = new net.makimono.dictionary.view.TextView(DictionaryEntryActivity.this);
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

	private View createSeparator() {
		View separator = new View(this);
		separator.setBackgroundResource(R.drawable.secondary_separator);
		separator.setMinimumHeight(1);
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

	private EnumSet<Language> getConfiguredLanguages() {
		return PreferenceFragment.getConfiguredLanguages(PreferenceManager.getDefaultSharedPreferences(this));
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
		return TypedValueUtil.getPixelForDip(dip, getResources().getDisplayMetrics());
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
			kanjiResultAdapter.updateEntries(kanji);

			int visibility = kanji.isEmpty() ? View.GONE : View.VISIBLE;
			findViewById(R.id.entry_separator_kanji).setVisibility(visibility);
			findViewById(R.id.entry_separator_line_kanji).setVisibility(visibility);
			kanjiListView.setVisibility(visibility);
		}
	}
}
