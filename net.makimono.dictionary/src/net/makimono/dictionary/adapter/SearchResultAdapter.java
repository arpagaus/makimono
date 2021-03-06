package net.makimono.dictionary.adapter;

import java.util.ArrayList;
import java.util.List;

import net.makimono.dictionary.R;
import net.makimono.dictionary.activity.PreferenceActivity;
import net.makimono.dictionary.model.Entry;
import net.makimono.dictionary.model.Language;
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

	private List<? extends Entry> entries = new ArrayList<Entry>();

	public SearchResultAdapter(Context context) {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		this.inflater = LayoutInflater.from(context);
		updateLanguages();
	}

	private void updateLanguages() {
		languages = PreferenceActivity.getConfiguredLanguages(sharedPreferences);
	}

	protected List<Language> getLanguages() {
		return languages;
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
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.search_result_entry, parent, false);
		}
		TextView resultExpression = (TextView) convertView.findViewById(R.id.result_expression);
		TextView resultReading = (TextView) convertView.findViewById(R.id.result_reading);
		TextView resultMeaning = (TextView) convertView.findViewById(R.id.result_meaning);

		Entry entry = entries.get(position);
		resultExpression.setText(entry.getExpression());
		resultReading.setText(entry.getReadingSummary());
		resultMeaning.setText(entry.getMeaningSummary(languages));
		return convertView;
	}

	public void updateEntries(List<? extends Entry> entries) {
		updateLanguages();
		this.entries = entries;
		notifyDataSetChanged();
	}
}
