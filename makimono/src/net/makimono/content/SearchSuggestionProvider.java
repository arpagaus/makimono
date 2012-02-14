package net.makimono.content;

import java.io.IOException;
import java.util.TreeSet;

import net.makimono.service.SearcherService;
import net.makimono.service.SearcherServiceConnection;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.SearchRecentSuggestions;
import android.util.Log;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
	private static final String LOG_TAG = SearchSuggestionProvider.class.getSimpleName();

	public static final int MODE = DATABASE_MODE_QUERIES;
	public static final String AUTHORITY = SearchSuggestionProvider.class.getName();

	public static SearchRecentSuggestions getSearchRecentSuggestions(Context context) {
		return new SearchRecentSuggestions(context, SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
	}

	private SearcherServiceConnection connection = new SearcherServiceConnection();

	public SearchSuggestionProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}

	@Override
	public boolean onCreate() {
		boolean success = super.onCreate();
		bindSearcher();
		return success;
	}

	private void bindSearcher() {
		Intent intent = new Intent(getContext(), SearcherService.class);
		getContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Cursor recentSearchesCursor = super.query(uri, projection, selection, selectionArgs, sortOrder);
		Cursor dictionarySuggestionsCursor = queryDictionarySuggestions(selectionArgs[0]);
		return new MergeCursor(new Cursor[] { recentSearchesCursor, dictionarySuggestionsCursor });
	}

	private Cursor queryDictionarySuggestions(String string) {
		MatrixCursor cursor = new MatrixCursor(new String[] { SearchManager.SUGGEST_COLUMN_FORMAT, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_QUERY, BaseColumns._ID });
		try {
			TreeSet<String> suggestions = connection.getSearcher().suggest(string);
			for (String s : suggestions) {
				cursor.addRow(new Object[] { null, s, s, s.hashCode() });
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, "Failed to get suggestions", e);
		}
		return cursor;
	}
}
