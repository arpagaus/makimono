package net.makimono.dictionary.content;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

public class RecentSearchesOpenHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "recent_search";
	private static final String RECENT_COLUMN_AUTHORITY = "authority";
	private static final String RECENT_COLUMN_SEARCH_STRING = "search_string";
	private static final String RECENT_COLUMN_LAST_SEARCH = "last_search";

	private static final String TABLE_CREATE_SQL = "CREATE TABLE " + TABLE_NAME + " (" + BaseColumns._ID + " INTEGER, " + RECENT_COLUMN_AUTHORITY + " TEXT, " + RECENT_COLUMN_SEARCH_STRING + " TEXT, "
			+ RECENT_COLUMN_LAST_SEARCH + " INTEGER, UNIQUE (" + RECENT_COLUMN_AUTHORITY + ", " + RECENT_COLUMN_SEARCH_STRING + "));";

	RecentSearchesOpenHelper(Context context) {
		super(context.getApplicationContext(), TABLE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("SQL", TABLE_CREATE_SQL);
		db.execSQL(TABLE_CREATE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	@SuppressWarnings("deprecation")
	public Cursor getRecentSearches(String authority, String searchString) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);
		queryBuilder.appendWhere(RECENT_COLUMN_AUTHORITY + " = '" + authority + "' ");
		if (StringUtils.isNotBlank(searchString)) {
			queryBuilder.appendWhere(" AND " + RECENT_COLUMN_SEARCH_STRING + " LIKE '" + searchString + "%'");
		}

		Map<String, String> columnMap = new HashMap<String, String>();
		columnMap.put(BaseColumns._ID, BaseColumns._ID);
		columnMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1, RECENT_COLUMN_SEARCH_STRING + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
		columnMap.put(SearchManager.SUGGEST_COLUMN_QUERY, RECENT_COLUMN_SEARCH_STRING + " AS " + SearchManager.SUGGEST_COLUMN_QUERY);
		columnMap.put(SearchManager.SUGGEST_COLUMN_ICON_1, "'" + android.R.drawable.ic_menu_recent_history + "' AS " + SearchManager.SUGGEST_COLUMN_ICON_1);
		queryBuilder.setProjectionMap(columnMap);

		String[] projection = new String[] { BaseColumns._ID, SearchManager.SUGGEST_COLUMN_ICON_1, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_QUERY };
		Log.d("SQL", queryBuilder.buildQuery(projection, null, null, null, null, RECENT_COLUMN_LAST_SEARCH + " DESC", null));
		return queryBuilder.query(getReadableDatabase(), projection, null, null, null, null, RECENT_COLUMN_LAST_SEARCH + " DESC", "50");
	}

	public void clearHistory() {
		getWritableDatabase().delete(TABLE_NAME, null, null);
	}

	public void saveRecentQuery(String authority, String searchString) {
		ContentValues values = new ContentValues();
		values.put(BaseColumns._ID, searchString.hashCode());
		values.put(RECENT_COLUMN_AUTHORITY, authority);
		values.put(RECENT_COLUMN_SEARCH_STRING, searchString);
		values.put(RECENT_COLUMN_LAST_SEARCH, System.currentTimeMillis() / 1000);

		SQLiteDatabase database = getWritableDatabase();
		int updateCount = database.update(TABLE_NAME, values, RECENT_COLUMN_AUTHORITY + " = ? AND " + RECENT_COLUMN_SEARCH_STRING + " = ?", new String[] { authority, searchString });
		if (updateCount == 0) {
			database.insert(TABLE_NAME, null, values);
		}
	}
}
