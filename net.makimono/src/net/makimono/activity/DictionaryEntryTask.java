package net.makimono.activity;

import java.io.IOException;
import java.util.HashSet;

import net.makimono.model.DictionaryEntry;
import net.makimono.model.KanjiEntry;
import android.os.AsyncTask;
import android.util.Log;

public class DictionaryEntryTask extends AsyncTask<DictionaryEntry, Void, HashSet<KanjiEntry>> {
	private DictionaryEntryActivity activity;

	DictionaryEntryTask(DictionaryEntryActivity dictionaryEntryActivity) {
		this.activity = dictionaryEntryActivity;
	}

	protected HashSet<KanjiEntry> doInBackground(DictionaryEntry... dictionaryEntries) {
		try {
			DictionaryEntry dictionaryEntry = dictionaryEntries[0];

			HashSet<KanjiEntry> kanjiEntries = new HashSet<KanjiEntry>();
			for (String e : dictionaryEntry.getExpressions()) {
				kanjiEntries.addAll(activity.connection.getKanjiSearcher().getKanjiEntries(e));
			}
			return kanjiEntries;
		} catch (IOException e) {
			Log.e(DictionaryEntryTask.class.getSimpleName(), "Failed to get dictionary entry", e);
			return null;
		}
	}

	protected void onPostExecute(HashSet<KanjiEntry> kanjis) {
		activity.updateKanjisView(kanjis);
	}
}