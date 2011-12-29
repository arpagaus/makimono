package android.jiten;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;

import jmdict.Entry;

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

import android.app.Activity;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SearchActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_layout);

		findViewById(R.id.searchButton).setOnClickListener(this);
		findViewById(R.id.resultListView).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Log.i("test", v.toString());

		switch (v.getId()) {
		case R.id.searchButton:
			doSearch();
			break;
		case R.id.resultListView:
			openEntry((ListView) v);
			break;
		}
	}

	private void openEntry(ListView v) {
		// TODO Auto-generated method stub
		Log.i("test", v.getSelectedItem().toString());
	}

	private void doSearch() {
		TextView searchText = (TextView) findViewById(R.id.searchText);
		try {
			Directory directory = new SimpleFSDirectory(new File(
					"/sdcard/dictionary/index/"));
			Query q = new QueryParser(Version.LUCENE_30, "glossen",
					new SimpleAnalyzer(Version.LUCENE_30)).parse(searchText
					.getText().toString());

			int hitsPerPage = 10;
			IndexSearcher searcher = new IndexSearcher(directory, true);
			TopScoreDocCollector collector = TopScoreDocCollector.create(
					hitsPerPage, true);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			final String[] matrix = { "_id", "name", "value" };
			final String[] columns = { "name", "value" };
			final int[] layouts = { android.R.id.text1, android.R.id.text2 };

			MatrixCursor cursor = new MatrixCursor(matrix);

			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				byte[] binaryValue = CompressionTools.decompress(d
						.getBinaryValue("entry"));
				ObjectInputStream inputStream = new ObjectInputStream(
						new ByteArrayInputStream(binaryValue));
				Entry entry = (Entry) inputStream.readObject();

				String kanjiElement = "";
				if (!entry.getKEle().isEmpty()) {
					kanjiElement = entry.getKEle().get(0).getKeb();
				}
				String readingElement = "";
				if (!entry.getREle().isEmpty()) {
					readingElement = entry.getREle().get(0).getReb();
				}
				String gloss = "";
				if (!entry.getSense().isEmpty()
						&& !entry.getSense().get(0).getGloss().isEmpty()) {
					gloss = entry.getSense().get(0).getGloss().get(0)
							.getvalue();
				}
				cursor.addRow(new Object[] { entry.getEntSeq(),
						kanjiElement + " [" + readingElement + "]", gloss });
			}

			SimpleCursorAdapter data = new SimpleCursorAdapter(this,
					R.layout.search_result_entry, cursor, columns, layouts);

			ListView listView = (ListView) findViewById(R.id.resultListView);
			listView.setAdapter(data);

		} catch (Exception e) {
			Log.e("DictionaryActivity", "Error", e);
		}
	}
}