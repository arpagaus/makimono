package net.makimono.dictionary.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.makimono.dictionary.activity.PreferenceFragment;
import net.makimono.dictionary.activity.PreferenceEnum;
import net.makimono.dictionary.model.Language;
import net.makimono.dictionary.searcher.DictionarySearcher;
import net.makimono.dictionary.searcher.ExampleSearcher;
import net.makimono.dictionary.searcher.KanjiSearcher;
import net.makimono.dictionary.util.ExternalStorageUtil;
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
	private ExampleSearcher exampleSearcher;

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

				directory = new File(ExternalStorageUtil.getExternalFilesDir(getApplicationContext()), "indexes/example/");
				exampleSearcher = new ExampleSearcher(directory);

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

		if (exampleSearcher != null) {
			try {
				exampleSearcher.close();
			} catch (IOException e) {
				Log.e(LOG_TAG, "Failed to close searcher", e);
			}
		}
		exampleSearcher = null;
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

		public ExampleSearcher getExampleSearcher() {
			return SearcherService.this.exampleSearcher;
		}
	}

	private void updatePreferences(SharedPreferences sharedPreferences) {
		List<Language> languages = PreferenceFragment.getConfiguredLanguages(sharedPreferences);
		dictionarySearcher.setLanguages(languages);
		dictionarySearcher.setRomajiSearchEnabled(sharedPreferences.getBoolean(PreferenceEnum.ROMAJI_SEARCH.key(), true));
	}

	private class PreferenceChangeListener implements OnSharedPreferenceChangeListener {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			updatePreferences(sharedPreferences);
		}

	}
}
