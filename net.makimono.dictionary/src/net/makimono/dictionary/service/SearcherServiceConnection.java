package net.makimono.dictionary.service;

import java.util.concurrent.CountDownLatch;

import net.makimono.dictionary.searcher.DictionarySearcher;
import net.makimono.dictionary.searcher.ExampleSearcher;
import net.makimono.dictionary.searcher.KanjiSearcher;
import net.makimono.dictionary.service.SearcherService.SearcherBinder;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class SearcherServiceConnection implements ServiceConnection {
	private static final String LOG_TAG = SearcherServiceConnection.class.getSimpleName();

	private SearcherBinder searcherBinder;

	private CountDownLatch lock = new CountDownLatch(1);

	public DictionarySearcher getDictionarySearcher() {
		try {
			lock.await();
		} catch (InterruptedException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
		return searcherBinder.getDictionarySearcher();
	}

	public KanjiSearcher getKanjiSearcher() {
		try {
			lock.await();
		} catch (InterruptedException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
		return searcherBinder.getKanjiSearcher();
	}

	public ExampleSearcher getExampleSearcher() {
		try {
			lock.await();
		} catch (InterruptedException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
		return searcherBinder.getExampleSearcher();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		searcherBinder = null;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
		searcherBinder = ((SearcherBinder) binder);
		lock.countDown();
	}
}