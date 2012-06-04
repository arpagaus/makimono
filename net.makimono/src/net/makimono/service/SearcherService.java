package net.makimono.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.makimono.activity.PreferenceActivity;
import net.makimono.model.Language;
import net.makimono.searcher.DictionarySearcher;
import net.makimono.searcher.KanjiSearcher;
import net.makimono.util.ExternalStorageUtil;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class SearcherService extends Service {
	private final static String LOG_TAG = SearcherService.class.getSimpleName();

	private DictionarySearcher dictionarySearcher;
	private KanjiSearcher kanjiSearcher;

	private OnSharedPreferenceChangeListener preferenceChangeListener;

	@Override
	public void onCreate() {
		Log.i(LOG_TAG, "onCreate");
		try {
			String storageState = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(storageState) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState)) {
				File directory = new File(ExternalStorageUtil.getExternalFilesDir(getApplicationContext()), "indexes/dictionary/");
				dictionarySearcher = new DictionarySearcher(directory);

				directory = new File(ExternalStorageUtil.getExternalFilesDir(getApplicationContext()), "indexes/kanji/");
				kanjiSearcher = new KanjiSearcher(directory);

				updatePreferences(PreferenceManager.getDefaultSharedPreferences(this));
				preferenceChangeListener = new PreferenceChangeListener();
				PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(preferenceChangeListener);
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, "Failed to create searcher", e);
			closeSearcher();
		}
	}

	@Override
	public void onDestroy() {
		Log.i(LOG_TAG, "onDestroy");
		closeSearcher();
		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
	}

	private void closeSearcher() {
		if (dictionarySearcher != null) {
			try {
				dictionarySearcher.close();
			} catch (IOException e) {
				Log.e(LOG_TAG, "Failed to close searcher", e);
			}
		}
		dictionarySearcher = null;

		if (kanjiSearcher != null) {
			try {
				kanjiSearcher.close();
			} catch (IOException e) {
				Log.e(LOG_TAG, "Failed to close searcher", e);
			}
		}
		kanjiSearcher = null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new SearcherBinder();
	}

	public class SearcherBinder extends Binder {
		public DictionarySearcher getDictionarySearcher() {
			return SearcherService.this.dictionarySearcher;
		}

		public KanjiSearcher getKanjiSearcher() {
			return SearcherService.this.kanjiSearcher;
		}
	}

	private void updatePreferences(SharedPreferences sharedPreferences) {
		List<Language> languages = PreferenceActivity.getConfiguredLanguages(sharedPreferences);
		dictionarySearcher.setLanguages(languages);
		dictionarySearcher.setRomajiSearchEnabled(sharedPreferences.getBoolean(PreferenceActivity.ROMAJI_SEARCH, true));
	}

	private class PreferenceChangeListener implements OnSharedPreferenceChangeListener {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			updatePreferences(sharedPreferences);
		}

	}
}
