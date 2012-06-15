package net.makimono.dictionary.activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipException;

import net.makimono.dictionary.Application;
import net.makimono.dictionary.R;
import net.makimono.dictionary.util.ExternalStorageUtil;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class HomeActivity extends AbstractDefaultActivity {
	private static final String LOG_TAG = HomeActivity.class.getSimpleName();
	private static final String INDEXES_FILE_NAME = "indexes.bin";

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
			if (!checkIndexFiles(destination)) {
				fileExtractorTask = new FileExtractorTask(destination);
				((Application) getApplication()).setFileExtractorTask(fileExtractorTask);
				fileExtractorTask.showDialog(this);
				fileExtractorTask.start();
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error when checking index files", e);
		}
	}

	private boolean checkIndexFiles(File destination) throws ZipException, IOException {
		long time = System.currentTimeMillis();
		ArchiveInputStream archiveInputStream = getArchiveInputStream(INDEXES_FILE_NAME);
		try {
			ArchiveEntry entry;
			while ((entry = archiveInputStream.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					File file = new File(destination, entry.getName());
					if (!file.exists() || file.length() != entry.getSize()) {
						Log.i(LOG_TAG, "Missing or wrong size: " + entry.getName() + " (file.exists()=" + file.exists() + ", (file.length() - zipEntry.getSize())=" + (file.length() - entry.getSize())
								+ ")");
						return false;
					}
				}
			}
		} finally {
			IOUtils.closeQuietly(archiveInputStream);
			Log.i(LOG_TAG, "checkIndexFiles took " + (System.currentTimeMillis() - time) + "ms");
		}
		Log.d(LOG_TAG, "All index files present");
		return false;

		// ZipFile zipFile = new ZipFile(getExpansionFile());
		// Enumeration<? extends ZipEntry> entries = zipFile.entries();
		//
		// while (entries.hasMoreElements()) {
		// ZipEntry zipEntry = entries.nextElement();
		// if (zipEntry.isDirectory()) {
		// continue;
		// }
		//
		// File file = new File(destination, zipEntry.getName());
		// if (!file.exists() || file.length() != zipEntry.getSize()) {
		// Log.i(LOG_TAG, "Missing or wrong size: " + zipEntry.getName() +
		// " (file.exists()=" + file.exists() +
		// ", (file.length() - zipEntry.getSize())=" + (file.length() -
		// zipEntry.getSize())
		// + ")");
		// zipFile.close();
		// return false;
		// }
		// }
	}

	private ArchiveInputStream getArchiveInputStream(String fileName) throws IOException {
		InputStream bufferedInputStream = new BufferedInputStream(getAssets().open(fileName));
		CompressorInputStream compressorInputStream = new GzipCompressorInputStream(bufferedInputStream);
		ArchiveInputStream archiveInputStream = new TarArchiveInputStream(compressorInputStream);
		return archiveInputStream;
	}

	public class FileExtractorTask extends Thread {
		private File destination;
		private ProgressDialog progressDialog;

		private long maxProgress;
		private long currentProgress;

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
				progressDialog.setMax((int) maxProgress);
				progressDialog.setProgress((int) currentProgress);
			}
		}

		@Override
		public void run() {
			long startTime = System.currentTimeMillis();

			try {
				FileUtils.deleteDirectory(destination);

				AssetFileDescriptor fileDescriptor = getAssets().openFd(INDEXES_FILE_NAME);
				System.out.println("fileDescriptor.getLength()=" + fileDescriptor.getLength());
				maxProgress = fileDescriptor.getLength() / 1048576;
				currentProgress = 0;

				ArchiveInputStream archiveInputStream = getArchiveInputStream(INDEXES_FILE_NAME);

				ArchiveEntry entry;
				while ((entry = archiveInputStream.getNextEntry()) != null) {
					long time = System.currentTimeMillis();
					if (!entry.isDirectory()) {
						File file = new File(destination, entry.getName());
						file.getParentFile().mkdirs();
						FileOutputStream outputStream = new FileOutputStream(file);
						IOUtils.copy(archiveInputStream, outputStream);
						IOUtils.closeQuietly(outputStream);
					}

					currentProgress = archiveInputStream.getBytesRead() / 1048576;
					updateDialog();
					Log.d(LOG_TAG, "Extracting " + entry.getName() + " took " + (System.currentTimeMillis() - time) + "ms");
				}

				IOUtils.closeQuietly(archiveInputStream);
			} catch (Exception e) {
				Log.e("", "Error when extracting index files", e);
			} finally {
				dismissDialog();
			}

			Log.i(LOG_TAG, "FileExtractorTask finished in " + (System.currentTimeMillis() - startTime) + "ms");
		}
	}
}
