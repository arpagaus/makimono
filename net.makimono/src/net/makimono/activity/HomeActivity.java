package net.makimono.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.makimono.R;
import net.makimono.util.ExternalStorageUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.vending.expansion.downloader.Helpers;

public class HomeActivity extends AbstractDefaultActivity {
	private static final String LOG_TAG = HomeActivity.class.getSimpleName();

	private View searchDictionaryTextView;
	private View searchKanjiTextView;
	private View settingsTextView;
	private View aboutTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		searchDictionaryTextView = (View) findViewById(R.id.search_dictionary);
		searchDictionaryTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startSearch(DictionarySearchActivity.class);
			}
		});
		searchKanjiTextView = (View) findViewById(R.id.search_kanji);
		searchKanjiTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startSearch(KanjiSearchActivity.class);
			}
		});

		settingsTextView = (View) findViewById(R.id.settings);
		settingsTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, PreferenceActivity.class);
				startActivity(intent);
			}
		});

		aboutTextView = (View) findViewById(R.id.about);
		aboutTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
				startActivity(intent);
			}
		});

		initializeIndexFiles();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	private void startSearch(Class<?> clazz) {
		SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		manager.startSearch(null, false, new ComponentName(HomeActivity.this, clazz), null, false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initializeIndexFiles() {
		if (!expansionFileExists()) {
			Intent intent = new Intent(this, ExpansionFileDownloaderActivity.class);
			startActivity(intent);
		} else {
			try {
				File destination = ExternalStorageUtil.getExternalFilesDir(this);
				if (!checkIndexFiles(destination)) {
					ProgressDialog progressDialog = new ProgressDialog(this);
					progressDialog.setTitle("Initialization");
					progressDialog.setMessage("Extracting dictionary data files");
					progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					progressDialog.show();

					new Thread(new FileExtractorTask(destination, progressDialog)).start();
				}
			} catch (Exception e) {
				Log.e(LOG_TAG, "Error when checking index files", e);
			}
		}
	}

	private File getExpansionFile() {
		String fileName = Helpers.getExpansionAPKFileName(this, true, ExpansionFileDownloaderActivity.MAIN_FILE.fileVersion);
		fileName = Helpers.generateSaveFileName(this, fileName);
		return new File(fileName);
	}

	private boolean expansionFileExists() {
		File expansionFile = getExpansionFile();
		return expansionFile.exists() && expansionFile.length() == ExpansionFileDownloaderActivity.MAIN_FILE.fileSize;
	}

	private boolean checkIndexFiles(File destination) throws ZipException, IOException {
		ZipFile zipFile = new ZipFile(getExpansionFile());
		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = entries.nextElement();
			if (zipEntry.isDirectory()) {
				continue;
			}

			File file = new File(destination, zipEntry.getName());
			if (!file.exists() || file.length() != zipEntry.getSize()) {
				Log.i(LOG_TAG, "Missing or wrong size: " + zipEntry.getName() + " (file.exists()=" + file.exists() + ", (file.length() - zipEntry.getSize())=" + (file.length() - zipEntry.getSize())
						+ ")");
				zipFile.close();
				return false;
			}
		}
		Log.d(LOG_TAG, "All index files present");
		zipFile.close();
		return true;
	}

	private class FileExtractorTask implements Runnable {
		private File destination;
		private ProgressDialog progressDialog;

		public FileExtractorTask(File destination, ProgressDialog progressDialog) {
			this.destination = destination;
			this.progressDialog = progressDialog;
		}

		public void run() {
			Thread.currentThread().setName(FileExtractorTask.class.getSimpleName());

			try {
				FileUtils.deleteDirectory(destination);

				ZipFile zipFile = new ZipFile(getExpansionFile());
				progressDialog.setMax(zipFile.size());
				int count = 0;

				Enumeration<? extends ZipEntry> entries = zipFile.entries();

				while (entries.hasMoreElements()) {
					ZipEntry zipEntry = entries.nextElement();
					if (zipEntry.isDirectory()) {
						continue;
					}

					long time = System.currentTimeMillis();

					File file = new File(destination, zipEntry.getName());
					file.getParentFile().mkdirs();
					InputStream inputStream = zipFile.getInputStream(zipEntry);
					OutputStream outputStream = new FileOutputStream(file);

					IOUtils.copy(inputStream, outputStream);
					IOUtils.closeQuietly(inputStream);
					IOUtils.closeQuietly(outputStream);

					Log.d(LOG_TAG, "Extracting " + zipEntry.getName() + " took " + (System.currentTimeMillis() - time) + "ms");

					progressDialog.setProgress(++count);
				}

				zipFile.close();
			} catch (IOException e) {
				Log.e("", "Error when extracting index files", e);
				// TODO notify the user
			} finally {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	}
}
