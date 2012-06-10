package net.makimono.dictionary.content;

import java.io.IOException;
import java.util.TreeSet;

import net.makimono.dictionary.model.Entry;
import net.makimono.dictionary.searcher.Searcher;
import net.makimono.dictionary.service.SearcherService;
import net.makimono.dictionary.service.SearcherServiceConnection;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public abstract class AbstractSearchSuggestionProvider extends ContentProvider {
	private static final String LOG_TAG = AbstractSearchSuggestionProvider.class.getSimpleName();

	private SearcherServiceConnection connection = new SearcherServiceConnection();
	private RecentSearchesOpenHelper recentSearchesOpenHelper;

	private RecentSearchesOpenHelper getRecentSearchesOpenHelper() {
		if (recentSearchesOpenHelper == null) {
			recentSearchesOpenHelper = new RecentSearchesOpenHelper(getContext());
		}
		return recentSearchesOpenHelper;
	}

	@Override
	public boolean onCreate() {
		bindSearcher();
		return true;
	}

	private void bindSearcher() {
		Intent intent = new Intent(getContext(), SearcherService.class);
		getContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		String queryString = selectionArgs != null && selectionArgs.length > 0 ? selectionArgs[0] : "";
		Cursor recentSearchesCursor = queryRecentSearches(queryString);
		Cursor dictionarySuggestionsCursor = queryDictionarySuggestions(queryString);
		return new MergeCursor(new Cursor[] { recentSearchesCursor, dictionarySuggestionsCursor });
	}

	private Cursor queryRecentSearches(String string) {
		return getRecentSearchesOpenHelper().getRecentSearches(getClass().getSimpleName(), string);
	}

	private Cursor queryDictionarySuggestions(String string) {
		MatrixCursor cursor = new MatrixCursor(new String[] { BaseColumns._ID, SearchManager.SUGGEST_COLUMN_ICON_1, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_QUERY });
		try {
			TreeSet<String> suggestions = getSearcher(connection).suggest(string);
			for (String suggestion : suggestions) {
				cursor.addRow(new Object[] { suggestion.hashCode(), android.R.drawable.ic_menu_search, suggestion, suggestion });
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, "Failed to get suggestions", e);
		}
		return cursor;
	}

	protected abstract Searcher<? extends Entry> getSearcher(SearcherServiceConnection connection);

	public static void clearHistory(Context context) {
		RecentSearchesOpenHelper helper = new RecentSearchesOpenHelper(context);
		helper.clearHistory();
		helper.close();
	}

	public static void saveRecentQuery(Context context, Class<? extends AbstractSearchSuggestionProvider> providerClass, String string) {
		RecentSearchesOpenHelper helper = new RecentSearchesOpenHelper(context);
		helper.saveRecentQuery(providerClass.getSimpleName(), string);
		helper.close();
	}
}
