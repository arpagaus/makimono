package net.makimono.content;

import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.util.Log;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
	public static final int MODE = DATABASE_MODE_QUERIES;
	public static final String AUTHORITY = SearchSuggestionProvider.class.getName();

	public SearchSuggestionProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Log.i("suggestion", uri.getPath());
		Cursor recentSearchesCursor = super.query(uri, projection, selection, selectionArgs, sortOrder);
		Cursor dictionarySuggestionsCursor = queryDictionarySuggestions(uri.getLastPathSegment().toLowerCase());
		return new MergeCursor(new Cursor[] { recentSearchesCursor, dictionarySuggestionsCursor });
	}

	private Cursor queryDictionarySuggestions(String lowerCase) {
		// TODO Auto-generated method stub
		return null;
	}
}
