package net.makimono.activity;

import java.io.IOException;

import net.makimono.model.KanjiEntry;
import android.os.AsyncTask;
import android.util.Log;

public class KanjiEntryTask extends AsyncTask<Integer, Void, KanjiEntry> {

	private KanjiEntryActivity activity;

	public KanjiEntryTask(KanjiEntryActivity activity) {
		this.activity = activity;
	}

	@Override
	protected KanjiEntry doInBackground(Integer... params) {
		String literal = String.valueOf(Character.toChars(params[0]));
		try {
			return activity.connection.getKanjiSearcher().getKanjiEntry(literal);
		} catch (IOException e) {
			Log.e(KanjiEntryTask.class.getSimpleName(), "Failed to get kanji entry", e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(KanjiEntry result) {
		activity.updateView(result);
	}
}
