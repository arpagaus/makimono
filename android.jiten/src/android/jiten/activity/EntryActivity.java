package android.jiten.activity;

import java.io.File;
import java.io.IOException;

import jiten.model.Entry;
import jiten.model.Language;
import jiten.model.Sense;
import jiten.searcher.Searcher;

import org.apache.lucene.store.SimpleFSDirectory;

import android.content.Intent;
import android.jiten.R;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

public class EntryActivity extends FragmentActivity {

	private TextView expressionTextView;
	private TextView readingTextView;

	private TextView englishTextView;
	private TextView germanTextView;
	private TextView frenchTextView;
	private TextView russianTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dictionary_entry);
		expressionTextView = (TextView) findViewById(R.id.entry_expression);
		readingTextView = (TextView) findViewById(R.id.entry_reading);

		englishTextView = (TextView) findViewById(R.id.entry_translation_en);
		germanTextView = (TextView) findViewById(R.id.entry_translation_de);
		frenchTextView = (TextView) findViewById(R.id.entry_translation_fr);
		russianTextView = (TextView) findViewById(R.id.entry_translation_ru);

		handleIntent(getIntent());
	}

	private void handleIntent(Intent intent) {
		if (intent.hasExtra("DOC_ID")) {
			Searcher searcher = null;
			try {
				String storageState = Environment.getExternalStorageState();
				if (Environment.MEDIA_MOUNTED.equals(storageState) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState)) {
					File directory = new File(this.getExternalFilesDir(null), "/indexes/dictionary/");
					searcher = new Searcher(new SimpleFSDirectory(directory));
					Entry entry = searcher.getByDocId(intent.getExtras().getInt("DOC_ID"));
					updateView(entry);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				if (searcher != null) {
					try {
						searcher.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	private void updateView(Entry entry) {
		String reading = entry.getReadings().get(0);
		if (entry.getExpressions().isEmpty()) {
			expressionTextView.setText(reading);
		} else {
			expressionTextView.setText(entry.getExpressions().get(0));
		}
		readingTextView.setText(reading);

		Sense sense = entry.getSenses().get(0);

		englishTextView.setText(sense.getGlossString(Language.en));
		if (englishTextView.getText().length() == 0) {
			englishTextView.setVisibility(View.INVISIBLE);
		} else {
			englishTextView.setVisibility(View.VISIBLE);
		}

		germanTextView.setText(sense.getGlossString(Language.de));
		if (germanTextView.getText().length() == 0) {
			germanTextView.setVisibility(View.INVISIBLE);
		} else {
			germanTextView.setVisibility(View.VISIBLE);
		}

		frenchTextView.setText(sense.getGlossString(Language.fr));
		if (frenchTextView.getText().length() == 0) {
			frenchTextView.setVisibility(View.INVISIBLE);
		} else {
			frenchTextView.setVisibility(View.VISIBLE);
		}

		russianTextView.setText(sense.getGlossString(Language.ru));
		if (russianTextView.getText().length() == 0) {
			russianTextView.setVisibility(View.INVISIBLE);
		} else {
			russianTextView.setVisibility(View.VISIBLE);
		}
	}

}
