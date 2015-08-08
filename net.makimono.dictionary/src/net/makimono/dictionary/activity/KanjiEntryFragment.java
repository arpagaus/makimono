package net.makimono.dictionary.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.makimono.dictionary.R;
import net.makimono.dictionary.adapter.SearchResultAdapter;
import net.makimono.dictionary.model.KanjiEntry;
import net.makimono.dictionary.model.Language;
import net.makimono.dictionary.model.Meaning;
import net.makimono.dictionary.service.SearcherService;
import net.makimono.dictionary.service.SearcherServiceConnection;
import net.makimono.dictionary.util.MeaningTextViewFactory;
import net.makimono.dictionary.view.KanjiWritingView;
import net.makimono.dictionary.view.NonScrollingListView;

public class KanjiEntryFragment extends Fragment {
	public static final String EXTRA_KANJI_ENTRY = KanjiEntryFragment.class.getName() + ".EXTRA_KANJI_ENTRY";
	private final static String LOG_TAG = KanjiEntryFragment.class.getSimpleName();

	private SearcherServiceConnection connection = new SearcherServiceConnection();

	private View contentView;

	private TextView literalTextView;
	private KanjiWritingView kanjiAnimationView;
	private TextView onYomiTextView;
	private TextView kunYomiTextView;
	private TextView nanoriTextView;
	private TextView hangulTextView;
	private TextView pinyinTextView;
	private TextView radicalTextView;
	private TextView strokeCountTextView;
	private TextView jlptTextView;
	private TextView gradeTextView;
	private TextView frequencyTextView;
	private TextView unicodeTextView;
	private LinearLayout meaningsGroupView;
	private NonScrollingListView alternativeRadicalsListView;
	private SearchResultAdapter alternativeRadicalsResultAdapter;

	private void bindSearcher() {
		Intent intent = new Intent(getActivity(), SearcherService.class);
		getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.kanji_entry, container, false);

		literalTextView = (TextView) contentView.findViewById(R.id.kanji_literal);
		kanjiAnimationView = (KanjiWritingView) contentView.findViewById(R.id.kanji_animation);
		kanjiAnimationView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle arguments = new Bundle();
				arguments.putStringArrayList(net.makimono.dictionary.Intent.EXTRA_STROKE_PATHS, new ArrayList<String>(getEntry().getStrokePaths()));

				KanjiWritingFragment fragment = new KanjiWritingFragment();
				fragment.setArguments(arguments);

				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
			}
		});

		onYomiTextView = (TextView) contentView.findViewById(R.id.kanji_on_yomi);
		kunYomiTextView = (TextView) contentView.findViewById(R.id.kanji_kun_yomi);
		nanoriTextView = (TextView) contentView.findViewById(R.id.kanji_nanori);
		hangulTextView = (TextView) contentView.findViewById(R.id.kanji_hangul);
		pinyinTextView = (TextView) contentView.findViewById(R.id.kanji_pinyin);
		radicalTextView = (TextView) contentView.findViewById(R.id.kanji_radical);
		strokeCountTextView = (TextView) contentView.findViewById(R.id.kanji_stroke_count);
		jlptTextView = (TextView) contentView.findViewById(R.id.kanji_jlpt);
		gradeTextView = (TextView) contentView.findViewById(R.id.kanji_grade);
		frequencyTextView = (TextView) contentView.findViewById(R.id.kanji_frequency);
		unicodeTextView = (TextView) contentView.findViewById(R.id.kanji_unicode);

		meaningsGroupView = (LinearLayout) contentView.findViewById(R.id.kanji_meanings);

		alternativeRadicalsListView = (NonScrollingListView) contentView.findViewById(R.id.alternative_radicals);
		alternativeRadicalsResultAdapter = new SearchResultAdapter(getActivity());
		alternativeRadicalsListView.setAdapter(alternativeRadicalsResultAdapter);

		return contentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		bindSearcher();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unbindService(connection);
	}

	@Override
	public void onStart() {
		super.onStart();
		handleArguments();
	}

	private KanjiEntry getEntry() {
		KanjiEntry entry = getArguments().getParcelable(EXTRA_KANJI_ENTRY);
		return entry;
	}

	private void handleArguments() {
		if (getEntry() != null) {
			updateView(getEntry());

			new LoadRadicalTask().execute(getEntry().getRadicalKanji());
			new LoadAlternativeRadicalsTask().execute(getEntry());
		}
	}

	private void updateView(KanjiEntry entry) {
		kanjiAnimationView.setStrokePaths(entry.getStrokePaths());
		if (entry.getStrokePaths().isEmpty()) {
			kanjiAnimationView.setVisibility(View.GONE);
		} else {
			kanjiAnimationView.setVisibility(View.VISIBLE);
		}

		literalTextView.setText(entry.getLiteral());
		onYomiTextView.setText(StringUtils.join(entry.getOnYomi(), ", "));
		kunYomiTextView.setText(StringUtils.join(entry.getKunYomi(), ", "));
		nanoriTextView.setText(StringUtils.join(entry.getNanori(), ", "));
		hangulTextView.setText(StringUtils.join(entry.getHangul(), ", "));
		pinyinTextView.setText(StringUtils.join(entry.getPinyin(), ", "));

		updateRadicalTextView(entry, null);
		strokeCountTextView.setText(getDisplayString(entry.getStrokeCount()));
		jlptTextView.setText(getJlptString(entry.getJlpt()));
		gradeTextView.setText(getGradeString(entry.getGrade()));
		frequencyTextView.setText(getDisplayString(entry.getFrequency()));
		unicodeTextView.setText("U+" + Integer.toHexString(entry.getCodePoint()).toUpperCase(Locale.US));

		MeaningTextViewFactory factory = new MeaningTextViewFactory(getActivity());
		meaningsGroupView.removeAllViews();
		EnumSet<Language> languages = PreferenceFragment.getConfiguredLanguages(PreferenceManager.getDefaultSharedPreferences(getActivity()));
		for (Language language : languages) {
			CharSequence meaning = entry.getMeaningSummary(Collections.singletonList(language));
			if (meaning.length() > 0) {
				meaningsGroupView.addView(factory.makeView(meaning, language));
			}
		}
	}

	private String getGradeString(byte grade) {
		if (grade >= 1 && grade <= 6) {
			return "Kyōiku " + grade;
		} else if (grade >= 7 && grade <= 8) {
			return "Jōyō";
		} else if (grade >= 9) {
			return "Jinmeiyō";
		}
		return "-";
	}

	private String getJlptString(byte jlpt) {
		switch (jlpt) {
		case 1:
			return "N1";
		case 2:
			return "N2 / N3";
		case 3:
			return "N3 / N4";
		case 4:
			return "N5";
		}
		return "-";
	}

	private String getDisplayString(int value) {
		return value == 0 ? "-" : String.valueOf(value);
	}

	private void updateRadicalTextView(KanjiEntry kanji, KanjiEntry radical) {
		StringBuilder text = new StringBuilder();
		if (kanji.getRadical() == 0) {
			text.append('-');
		} else {
			text.append(kanji.getRadicalKanji());
			text.append(" [");
			text.append(kanji.getRadicalKana());
			text.append("]");
		}
		if (radical != null) {
			for (Meaning m : radical.getMeanings()) {
				if (m.getLanguage() == Language.en) {
					text.append('\n');
					text.append(m.getValue());
					break;
				}
			}
		}
		radicalTextView.setText(text);
	}

	private class LoadRadicalTask extends AsyncTask<Character, Void, KanjiEntry> {

		@Override
		protected KanjiEntry doInBackground(Character... params) {
			try {
				return connection.getKanjiSearcher().getKanjiEntry(String.valueOf(params[0]));
			} catch (IOException e) {
				Log.e(LOG_TAG, "Failed to load radical", e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(KanjiEntry result) {
			if (result == null) {
				return;
			} else {
				updateRadicalTextView(getEntry(), result);
			}
		}
	}

	private class LoadAlternativeRadicalsTask extends AsyncTask<KanjiEntry, Void, List<KanjiEntry>> {

		@Override
		protected List<KanjiEntry> doInBackground(KanjiEntry... params) {
			try {
				List<KanjiEntry> radicalKanjiEntries = new ArrayList<KanjiEntry>(params[0].getRadicals().size());
				for (String radical : params[0].getRadicals()) {
					KanjiEntry radicalKanjiEntry = connection.getKanjiSearcher().getKanjiEntry(radical);
					if (radicalKanjiEntry == null) {
						Log.e(LOG_TAG, "There is no entry for U+" + Integer.toHexString(radical.codePointAt(0)));
					} else {
						if (RadicalSearchActivity.CHARACTER_SUBSTITUTES.containsKey(radicalKanjiEntry.getLiteral())) {
							radicalKanjiEntry.setLiteral(RadicalSearchActivity.CHARACTER_SUBSTITUTES.get(radicalKanjiEntry.getLiteral()));
						}
						radicalKanjiEntries.add(radicalKanjiEntry);
					}
				}
				return radicalKanjiEntries;
			} catch (IOException e) {
				Log.e(LOG_TAG, "Failed to load radical", e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<KanjiEntry> result) {
			if (result != null) {
				alternativeRadicalsResultAdapter.updateEntries(result);
			}
		}
	}
}
