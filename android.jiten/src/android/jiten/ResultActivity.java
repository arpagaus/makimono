package android.jiten;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.CompressionTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import au.edu.monash.csse.jmdict.model.Entry;

public class ResultActivity extends ListActivity {

	private static final String CLASS_NAME = ResultActivity.class.getName();

	private ResultAdapter resultAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(getClass().getSimpleName(), "onCreate");
		super.onCreate(savedInstanceState);

		resultAdapter = new ResultAdapter(LayoutInflater.from(this));
		setListAdapter(resultAdapter);

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			// handles a click on a search suggestion; launches activity to show
			// word
			// Intent wordIntent = new Intent(this, WordActivity.class);
			// wordIntent.setData(intent.getData());
			// startActivity(wordIntent);
			// finish();
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// handles a search query
			String query = intent.getStringExtra(SearchManager.QUERY);
			doSearch(query);
		}
	}

	private void doSearch(final String query) {
		long time = System.currentTimeMillis();
		try {
			Log.i(CLASS_NAME, "Opening index");
			Directory directory = new SimpleFSDirectory(new File("/sdcard/dictionary/index/"));

			Log.i(CLASS_NAME, "Parse Query");
			Query q = new QueryParser(Version.LUCENE_30, "keyword", new SimpleAnalyzer(Version.LUCENE_30)).parse(query);

			Log.i(CLASS_NAME, "Execute query");
			int hitsPerPage = 100;
			IndexSearcher searcher = new IndexSearcher(directory, true);
			TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			ArrayList<Entry> entries = new ArrayList<Entry>();

			Log.i(CLASS_NAME, "Unwrap result");
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				byte[] binaryValue = CompressionTools.decompress(d.getBinaryValue("entry"));
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(binaryValue));
				Entry entry = (Entry) inputStream.readObject();

				entries.add(entry);
			}

			Log.i(CLASS_NAME, "Updating view");
			resultAdapter.replaceEntries(entries);
			Log.i(CLASS_NAME, "Finished search");
		} catch (Exception e) {
			Log.e("DictionaryActivity", "Error", e);
		}
	}

}
