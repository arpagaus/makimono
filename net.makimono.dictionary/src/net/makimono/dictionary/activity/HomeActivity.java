package net.makimono.dictionary.activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.makimono.dictionary.Application;
import net.makimono.dictionary.R;
import net.makimono.dictionary.util.ExternalStorageUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class HomeActivity extends AbstractDefaultActivity {
	private static final String LOG_TAG = HomeActivity.class.getSimpleName();
	private static final String INDEXES_FILE_NAME = "indexes.zip";

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
	protected void onDestroy() {
		super.onDestroy();

		FileExtractorTask fileExtractorTask = ((Application) getApplication()).getFileExtractorTask();
		if (fileExtractorTask != null && fileExtractorTask.isAlive()) {
			fileExtractorTask.dismissDialog();
			return;
		}
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
		FileExtractorTask fileExtractorTask = ((Application) getApplication()).getFileExtractorTask();
		if (fileExtractorTask != null && fileExtractorTask.isAlive()) {
			fileExtractorTask.showDialog(this);
			return;
		}

		try {
			File destination = ExternalStorageUtil.getExternalFilesDir(this);
			if (isFileExtractionNecessary(destination)) {
				fileExtractorTask = new FileExtractorTask(destination);
				((Application) getApplication()).setFileExtractorTask(fileExtractorTask);
				fileExtractorTask.showDialog(this);
				fileExtractorTask.start();
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error when checking index files", e);
		}
	}

	private boolean isFileExtractionNecessary(File destination) throws NameNotFoundException {
		int indexFilesVersion = PreferenceManager.getDefaultSharedPreferences(this).getInt(PreferenceActivity.INDEX_FILES_VERSION, 0);

		PackageManager manager = this.getPackageManager();
		PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);

		return info.versionCode > indexFilesVersion;
	}

	private ZipInputStream getArchiveInputStream(String fileName) throws IOException {
		InputStream bufferedInputStream = new BufferedInputStream(getAssets().open(fileName));
		ZipInputStream zipInputStream = new ZipInputStream(bufferedInputStream);
		return zipInputStream;
	}

	public class FileExtractorTask extends Thread {
		private File destination;
		private ProgressDialog progressDialog;

		private final static int INDEXES_FILE_COUNT = 20;
		private int currentProgress;

		public FileExtractorTask(File destination) {
			super(FileExtractorTask.class.getSimpleName());
			setDaemon(false);
			this.destination = destination;
		}

		private synchronized void showDialog(Context context) {
			progressDialog = new ProgressDialog(context);
			progressDialog.setTitle("Initialization");
			progressDialog.setMessage("Extracting dictionary data files");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		private synchronized void dismissDialog() {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}

		private synchronized void updateDialog() {
			if (progressDialog != null) {
				progressDialog.setMax(INDEXES_FILE_COUNT);
				progressDialog.setProgress(currentProgress);
			}
		}

		@Override
		public void run() {
			long startTime = System.currentTimeMillis();

			try {
				FileUtils.deleteDirectory(destination);
				ZipInputStream zipInputStream = getArchiveInputStream(INDEXES_FILE_NAME);

				ZipEntry entry;
				while ((entry = zipInputStream.getNextEntry()) != null) {
					long time = System.currentTimeMillis();
					if (!entry.isDirectory()) {
						File file = new File(destination, entry.getName());
						file.getParentFile().mkdirs();
						FileOutputStream outputStream = new FileOutputStream(file);
						IOUtils.copy(zipInputStream, outputStream);
						IOUtils.closeQuietly(outputStream);

						currentProgress++;
						updateDialog();
						Log.d(LOG_TAG, "Extracting " + entry.getName() + " took " + (System.currentTimeMillis() - time) + "ms");
					}
				}

				IOUtils.closeQuietly(zipInputStream);

				PackageManager manager = HomeActivity.this.getPackageManager();
				PackageInfo info = manager.getPackageInfo(HomeActivity.this.getPackageName(), 0);

				Editor editor = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this.getApplicationContext()).edit();
				editor.putInt(PreferenceActivity.INDEX_FILES_VERSION, info.versionCode);
				editor.commit();
			} catch (Exception e) {
				Log.e("", "Error when extracting index files", e);
			} finally {
				dismissDialog();
			}

			Log.i(LOG_TAG, "FileExtractorTask finished in " + (System.currentTimeMillis() - startTime) + "ms");
		}
	}
}
