package net.makimono.activity;

import java.io.IOException;
import java.util.HashSet;

import net.makimono.model.DictionaryEntry;
import net.makimono.model.KanjiEntry;

import org.apache.commons.lang3.tuple.Pair;

import android.os.AsyncTask;
import android.util.Log;

public class DictionaryEntryTask extends AsyncTask<Integer, Void, Pair<DictionaryEntry, HashSet<KanjiEntry>>> {
	private DictionaryEntryActivity activity;

	DictionaryEntryTask(DictionaryEntryActivity dictionaryEntryActivity) {
		this.activity = dictionaryEntryActivity;
	}

	protected Pair<DictionaryEntry, HashSet<KanjiEntry>> doInBackground(Integer... docIds) {
		try {
			DictionaryEntry dictionaryEntry = activity.connection.getDictionarySearcher().getByDocId(docIds[0]);

			HashSet<KanjiEntry> kanjiEntries = new HashSet<KanjiEntry>();
			for (String e : dictionaryEntry.getExpressions()) {
				kanjiEntries.addAll(activity.connection.getKanjiSearcher().getKanjiEntries(e));
			}
			return Pair.of(dictionaryEntry, kanjiEntries);
		} catch (IOException e) {
			Log.e(DictionaryEntryTask.class.getSimpleName(), "Failed to get dictionary entry", e);
			return null;
		}
	}

	protected void onPostExecute(Pair<DictionaryEntry, HashSet<KanjiEntry>> pair) {
		activity.updateView(pair.getLeft(), pair.getRight());
	}
}