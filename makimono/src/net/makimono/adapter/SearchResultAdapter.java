package net.makimono.adapter;

import java.util.ArrayList;
import java.util.List;

import net.makimono.R;
import net.makimono.activity.PreferenceActivity;
import net.makimono.model.DictionaryEntry;
import net.makimono.model.Language;
import net.makimono.model.Meaning;
import net.makimono.model.Sense;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchResultAdapter extends BaseAdapter {

	private List<Language> languages;
	private LayoutInflater inflater;
	private SharedPreferences sharedPreferences;

	private ArrayList<DictionaryEntry> entries = new ArrayList<DictionaryEntry>();

	public SearchResultAdapter(Context context) {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		this.inflater = LayoutInflater.from(context);
		updateLanguages();
	}

	private void updateLanguages() {
		languages = PreferenceActivity.getConfiguredLanguages(sharedPreferences);
	}

	@Override
	public int getCount() {
		return entries.size();
	}

	@Override
	public Object getItem(int position) {
		return entries.get(position);
	}

	@Override
	public long getItemId(int position) {
		return entries.get(position).getDocId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.search_result_entry, parent, false);
		}
		TextView resultExpression = (TextView) convertView.findViewById(R.id.result_expression);
		TextView resultReading = (TextView) convertView.findViewById(R.id.result_reading);
		TextView resultMeaning = (TextView) convertView.findViewById(R.id.entry_meanings);

		resultExpression.setText(getExpression(entries.get(position)));
		resultReading.setText(getReading(entries.get(position)));
		resultMeaning.setText(getMeaning(entries.get(position)));
		return convertView;
	}

	private CharSequence getExpression(DictionaryEntry entry) {
		if (entry.getExpressions().isEmpty()) {
			return entry.getReadings().get(0);
		} else {
			return entry.getExpressions().get(0);
		}
	}

	private CharSequence getReading(DictionaryEntry entry) {
		if (entry.getExpressions().isEmpty()) {
			return null;
		} else {
			return entry.getReadings().get(0);
		}
	}

	private CharSequence getMeaning(DictionaryEntry entry) {
		StringBuilder meaning = new StringBuilder();
		for (Sense s : entry.getSenses()) {
			for (Meaning g : s.getMeanings()) {
				if (languages.contains(g.getLanguage())) {
					if (meaning.length() > 0) {
						meaning.append(", ");
					}
					meaning.append(g.getValue());
				}
			}
		}
		return meaning.toString();
	}

	public void updateEntries(ArrayList<DictionaryEntry> entries) {
		updateLanguages();
		this.entries = entries;
		notifyDataSetChanged();
	}
}
