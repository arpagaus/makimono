package android.jiten;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import au.edu.monash.csse.jmdict.model.Entry;
import au.edu.monash.csse.jmdict.model.Gloss;
import au.edu.monash.csse.jmdict.model.Sense;

public class ResultAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<Entry> entries = new ArrayList<Entry>();

	public ResultAdapter(LayoutInflater inflater) {
		this.inflater = inflater;
	}

	public void replaceEntries(ArrayList<Entry> newEntries) {
		entries = newEntries;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return entries.size();
	}

	@Override
	public Object getItem(int position) {
		return entries.get(0);
	}

	@Override
	public long getItemId(int position) {
		return Long.valueOf(entries.get(position).getEntSeq());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.search_result_entry, parent, false);
		}
		TextView resultExpressionAndReading = (TextView) convertView.findViewById(R.id.resultExpressionAndReading);
		TextView resultGloss = (TextView) convertView.findViewById(R.id.resultGloss);

		resultExpressionAndReading.setText(getExpressionAndReading(entries.get(position)));
		resultGloss.setText(getGloss(entries.get(position)));
		return convertView;
	}

	private CharSequence getExpressionAndReading(Entry entry) {
		StringBuilder builder = new StringBuilder();
		if (!entry.getKEle().isEmpty()) {
			builder.append(entry.getKEle().get(0).getKeb());
		}
		if (!entry.getREle().isEmpty()) {
			String reading = entry.getREle().get(0).getReb();
			if (builder.length() > 0) {
				builder.append(" [");
				builder.append(reading);
				builder.append(']');
			} else {
				builder.append(reading);
			}
		}
		return builder.toString();
	}

	private CharSequence getGloss(Entry entry) {
		StringBuilder gloss = new StringBuilder();
		for (Sense s : entry.getSense()) {
			for (Gloss g : s.getGloss()) {
				if (gloss.length() > 0) {
					gloss.append(", ");
				}
				gloss.append(g.getvalue());
			}
		}
		return gloss.toString();
	}

}
