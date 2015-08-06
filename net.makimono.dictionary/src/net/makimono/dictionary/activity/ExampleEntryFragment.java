package net.makimono.dictionary.activity;

import java.util.EnumSet;

import org.apache.commons.lang3.StringUtils;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.makimono.dictionary.R;
import net.makimono.dictionary.model.ExampleEntry;
import net.makimono.dictionary.model.Language;
import net.makimono.dictionary.model.Meaning;
import net.makimono.dictionary.util.MeaningTextViewFactory;

public class ExampleEntryFragment extends Fragment {

	private View contentView;

	private TextView japaneseTextView;
	private LinearLayout meaningsGroupView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.example_entry, container, false);

		japaneseTextView = (TextView) contentView.findViewById(R.id.japanese_sentence);
		meaningsGroupView = (LinearLayout) contentView.findViewById(R.id.meanings);

		return contentView;
	}

	@Override
	public void onStart() {
		super.onStart();
		handleArguments();
	}

	private void handleArguments() {
		ExampleEntry entry = getArguments().getParcelable(net.makimono.dictionary.Intent.EXTRA_EXAMPLE_ENTRY);
		if (entry != null) {
			updateView(entry);
		}
	}

	private void updateView(ExampleEntry entry) {
		japaneseTextView.setText(StringUtils.defaultString(entry.getJapaneseMeaning().getValue()));

		MeaningTextViewFactory factory = new MeaningTextViewFactory(getActivity());
		meaningsGroupView.removeAllViews();
		EnumSet<Language> languages = PreferenceFragment.getConfiguredLanguages(PreferenceManager.getDefaultSharedPreferences(getActivity()));
		for (Language language : languages) {
			Meaning meaning = entry.getMeaning(language);
			if (meaning != null) {
				TextView textView = factory.makeView(meaning.getValue(), language);
				meaningsGroupView.addView(textView);
			}
		}
	}
}
