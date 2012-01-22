package android.jiten.adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jiten.model.Entry;
import jiten.model.Gloss;
import jiten.model.Sense;
import jiten.searcher.Searcher;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.SimpleFSDirectory;

import android.content.Context;
import android.jiten.R;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchResultAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private ArrayList<Entry> entries = new ArrayList<Entry>();

	public SearchResultAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
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
		TextView resultGloss = (TextView) convertView.findViewById(R.id.result_translation);

		resultExpression.setText(getExpression(entries.get(position)));
		resultReading.setText(getReading(entries.get(position)));
		resultGloss.setText(getGloss(entries.get(position)));
		return convertView;
	}

	private CharSequence getExpression(Entry entry) {
		if (entry.getExpressions().isEmpty()) {
			return entry.getReadings().get(0);
		} else {
			return entry.getExpressions().get(0);
		}
	}

	private CharSequence getReading(Entry entry) {
		if (entry.getExpressions().isEmpty()) {
			return null;
		} else {
			return entry.getReadings().get(0);
		}
	}

	private CharSequence getGloss(Entry entry) {
		StringBuilder gloss = new StringBuilder();
		for (Sense s : entry.getSenses()) {
			for (Gloss g : s.getGlosses()) {
				if (gloss.length() > 0) {
					gloss.append(", ");
				}
				gloss.append(g.getValue());
			}
		}
		return gloss.toString();
	}

	public void search(String query) throws IOException, ParseException {
		String storageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(storageState) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState)) {
			File directory = new File(context.getExternalFilesDir(null), "/indexes/dictionary/");
			Searcher searcher = new Searcher(new SimpleFSDirectory(directory));
			entries = searcher.search(query);
			searcher.close();
			notifyDataSetChanged();
		}

	}
}
