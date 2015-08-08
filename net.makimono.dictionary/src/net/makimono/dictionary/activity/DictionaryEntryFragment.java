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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class DictionaryEntryFragment extends Fragment {
	private static final String LOG_TAG = DictionaryEntryFragment.class.getSimpleName();

	public static final String EXTRA_DICTIONARY_ENTRY = DictionaryEntryFragment.class + ".EXTRA_DOC_ID";

	private SearcherServiceConnection connection = new SearcherServiceConnection();

	private View contentView;

	private TextSwitcher expressionTextSwitcher;
	private TextSwitcher readingTextSwitcher;
	private LinearLayout meaningsGroupView;
	private NonScrollingListView kanjiListView;

	private SearchResultAdapter kanjiResultAdapter;

	private DictionaryAlternativesSwitcher alternativesSwitcher;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.dictionary_entry, container, false);

		expressionTextSwitcher = createExpressionTextSwitcher();
		readingTextSwitcher = createReadingTextSwitcher();
		meaningsGroupView = (LinearLayout) contentView.findViewById(R.id.entry_meanings);
		kanjiListView = (NonScrollingListView) contentView.findViewById(R.id.entry_kanji);

		kanjiListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long id) {
				Intent intent = new Intent(getActivity(), KanjiEntryFragment.class);
				intent.putExtra(KanjiEntryFragment.EXTRA_KANJI_ENTRY, (KanjiEntry) kanjiResultAdapter.getItem(index));
				startActivity(intent);
			}
		});

		alternativesSwitcher = new DictionaryAlternativesSwitcher(contentView);

		return contentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		bindSearcher();

		kanjiResultAdapter = new SearchResultAdapter(getActivity());
		kanjiListView.setAdapter(kanjiResultAdapter);
	}

	@Override
	public void onStart() {
		super.onStart();
		handleArguments();
	}

	private void handleArguments() {
		byte[] entryData = getArguments().getByteArray(EXTRA_DICTIONARY_ENTRY);

		if (entryData != null && entryData.length > 0) {
			ByteArrayInputStream in = new ByteArrayInputStream(entryData);
			try {
				DictionaryEntry entry = DictionaryEntry.readEntry(new ObjectInputStream(in));
				updateView(entry);

				new LoadKanjiEntriesTask().execute(entry);
			} catch (Exception e) {
				Log.e(DictionaryEntryFragment.class.getSimpleName(), "Failed to deserialize entry", e);
			}
		}
	}

	private void bindSearcher() {
		Intent intent = new Intent(getActivity(), SearcherService.class);
		getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unbindService(connection);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_search_example:
			if (expressionTextSwitcher.getCurrentView() instanceof TextView) {
				CharSequence expression = ((TextView) expressionTextSwitcher.getCurrentView()).getText();
				if (StringUtils.isNotBlank(expression)) {
					Intent intent = new Intent(getActivity(), ExampleSearchFragment.class);
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
		readingTextSwitcher = (TextSwitcher) contentView.findViewById(R.id.entry_reading);
		readingTextSwitcher.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				TextView textView = new net.makimono.dictionary.view.TextView(getActivity());
				textView.setTextColor(Color.GRAY);
				textView.setTextSize(24);
				return textView;
			}
		});
		return readingTextSwitcher;
	}

	private TextSwitcher createExpressionTextSwitcher() {
		expressionTextSwitcher = (TextSwitcher) contentView.findViewById(R.id.entry_expression);
		expressionTextSwitcher.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				TextView textView = new net.makimono.dictionary.view.TextView(getActivity());
				textView.setTextSize(32);
				textView.setGravity(Gravity.CENTER_HORIZONTAL);
				return textView;
			}
		});
		return expressionTextSwitcher;
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
		View separator = new View(getActivity());
		separator.setBackgroundResource(R.drawable.secondary_separator);
		separator.setMinimumHeight(1);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		separator.setLayoutParams(layoutParams);
		layoutParams.leftMargin = getPixelForDip(10);
		layoutParams.rightMargin = getPixelForDip(10);
		return separator;
	}

	private void addMeanings(Sense sense) {
		MeaningTextViewFactory factory = new MeaningTextViewFactory(getActivity());
		for (Language language : getConfiguredLanguages()) {
			CharSequence meaning = StringUtils.join(sense.getMeanings(language), ", ");
			if (meaning.length() > 0) {
				meaningsGroupView.addView(factory.makeView(meaning, language));
			}
		}
	}

	private EnumSet<Language> getConfiguredLanguages() {
		return PreferenceFragment.getConfiguredLanguages(PreferenceManager.getDefaultSharedPreferences(getActivity()));
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
			TextView textView = new TextView(getActivity());
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
			return getResources().getString(getResources().getIdentifier(name, "string", getActivity().getPackageName()));
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
			contentView.findViewById(R.id.entry_separator_kanji).setVisibility(visibility);
			contentView.findViewById(R.id.entry_separator_line_kanji).setVisibility(visibility);
			kanjiListView.setVisibility(visibility);
		}
	}
}
