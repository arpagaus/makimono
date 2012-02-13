package net.makimono.service;

import java.io.File;
import java.io.IOException;

import net.makimono.searcher.Searcher;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class SearcherService extends Service {
	private final static String LOG_TAG = SearcherService.class.getSimpleName();

	private Searcher searcher;

	@Override
	public void onCreate() {
		Log.i(LOG_TAG, "onCreate");
		try {
			String storageState = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(storageState) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState)) {
				File directory = new File(Environment.getExternalStorageDirectory(), "makimono/indexes/dictionary/");
				searcher = new Searcher(directory);
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
	}

	private void closeSearcher() {
		if (searcher != null) {
			try {
				searcher.close();
			} catch (IOException e) {
				Log.e(LOG_TAG, "Failed to close searcher", e);
			}
		}
		searcher = null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new SearcherBinder();
	}

	public class SearcherBinder extends Binder {
		public Searcher getSearcher() {
			return SearcherService.this.searcher;
		}
	}
}
