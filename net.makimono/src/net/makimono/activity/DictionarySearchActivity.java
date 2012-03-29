package net.makimono.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import net.makimono.content.AbstractSearchSuggestionProvider;
import net.makimono.content.DictionarySearchSuggestionProvider;
import net.makimono.model.DictionaryEntry;
import net.makimono.searcher.DictionarySearcher;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class DictionarySearchActivity extends AbstractSearchActivity implements OnItemClickListener {
	private static final String CLASS_NAME = DictionarySearchActivity.class.getName();

	@Override
	public void onItemClick(AdapterView<?> view, View v, int position, long id) {
		DictionaryEntry entry = (DictionaryEntry) view.getAdapter().getItem(position);
		try {
			Intent intent = new Intent(this, DictionaryEntryActivity.class);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DictionaryEntry.writeEntry(new ObjectOutputStream(out), entry);
			intent.putExtra(DictionaryEntryActivity.EXTRA_DICTIONARY_ENTRY, out.toByteArray());
			startActivity(intent);
		} catch (IOException e) {
			Log.e(CLASS_NAME, "Failed to serialize entry", e);
		}
	}

	@Override
	protected DictionarySearcher getSearcher() {
		return connection.getDictionarySearcher();
	}

	@Override
	protected Class<? extends AbstractSearchSuggestionProvider> getSearchSuggestionProviderClass() {
		return DictionarySearchSuggestionProvider.class;
	}
}
