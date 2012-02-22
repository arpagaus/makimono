package net.makimono.service;

import java.util.concurrent.CountDownLatch;

import net.makimono.searcher.DictionarySearcher;
import net.makimono.service.SearcherService.SearcherBinder;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class SearcherServiceConnection implements ServiceConnection {
	private static final String LOG_TAG = SearcherServiceConnection.class.getSimpleName();

	private SearcherBinder searcherBinder;

	private CountDownLatch lock = new CountDownLatch(1);

	public DictionarySearcher getSearcher() {
		try {
			lock.await();
		} catch (InterruptedException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
		return searcherBinder.getSearcher();
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