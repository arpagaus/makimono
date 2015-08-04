package net.makimono.dictionary.activity;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;
import net.makimono.dictionary.Application;
import net.makimono.dictionary.R;
import net.makimono.dictionary.navigation.NavigationDrawerCallbacks;
import net.makimono.dictionary.navigation.NavigationDrawerFragment;
import net.makimono.dictionary.util.ExternalStorageUtil;

public class MainActivity extends AppCompatActivity implements NavigationDrawerCallbacks {
	private static final String LOG_TAG = MainActivity.class.getSimpleName();
	private static final String INDEXES_FILE_NAME = "indexes.zip";

	private Toolbar mToolbar;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private Fragment currentFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
		mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

		initializeIndexFiles();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();

		Fragment fragment = null;

		if (position == 0) {
			getSupportActionBar().setTitle(R.string.dictionary);
			fragment = new DictionarySearchFragment();
		}
		if (position == 1) {
			getSupportActionBar().setTitle(R.string.kanji);
			fragment = new KanjiSearchFragment();
		}
		if (position == 2) {
			getSupportActionBar().setTitle(R.string.example);
			fragment = new ExampleSearchFragment();
		}

		if (position == 3) {
			getSupportActionBar().setTitle(R.string.settings);
			if (currentFragment != null) {
				getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
			}

			PreferenceFragment preferenceFragment = new PreferenceFragment();
			getFragmentManager().beginTransaction().replace(R.id.container, preferenceFragment, PreferenceFragment.class.getSimpleName()).commit();
		} else {
			android.app.Fragment preferenceFragment = getFragmentManager().findFragmentByTag(PreferenceFragment.class.getSimpleName());
			if (preferenceFragment != null) {
				getFragmentManager().beginTransaction().remove(preferenceFragment).commit();
			}
		}

		if (position == 4) {
			getSupportActionBar().setTitle(R.string.about);
			fragment = new AboutFragment();
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
		}
		currentFragment = fragment;
	}

	@Override
	public void onBackPressed() {
		if (mNavigationDrawerFragment.isDrawerOpen()) {
			mNavigationDrawerFragment.closeDrawer();
		} else {
			super.onBackPressed();
		}
	}

	private void initializeIndexFiles() {
		FileExtractorTask fileExtractorTask = ((Application) getApplication()).getFileExtractorTask();
		if (fileExtractorTask != null && fileExtractorTask.isAlive()) {
			fileExtractorTask.showDialog(this);
			return;
		}

		try {
			final File destination = ExternalStorageUtil.getExternalFilesDir(this);
			if (isFileExtractionNecessary(destination)) {
				AlertDialog dialog = new AlertDialog.Builder(this).create();
				dialog.setTitle(R.string.welcome);
				dialog.setMessage(getString(R.string.welcome_info_message));
				dialog.setCancelable(false);
				dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						FileExtractorTask fileExtractorTask = new FileExtractorTask(destination);
						((Application) getApplication()).setFileExtractorTask(fileExtractorTask);
						fileExtractorTask.showDialog(MainActivity.this);
						fileExtractorTask.start();
					}
				});
				dialog.show();
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error when checking index files", e);
		}
	}

	private boolean isFileExtractionNecessary(File destination) throws NameNotFoundException {
		int indexFilesVersion = PreferenceManager.getDefaultSharedPreferences(this).getInt(PreferenceEnum.INDEX_FILES_VERSION.key(), 0);

		PackageManager manager = this.getPackageManager();
		PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);

		return info.versionCode > indexFilesVersion;
	}

	private static void closeStream(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException e) {
			Log.e(LOG_TAG, "Failed to close stream properly", e);
		}
	}

	public class FileExtractorTask extends Thread {
		private File destination;
		private ProgressDialog progressDialog;

		private final static int INDEXES_FILE_COUNT = 30;
		private int currentProgress;

		public FileExtractorTask(File destination) {
			super(FileExtractorTask.class.getSimpleName());
			setDaemon(false);
			this.destination = destination;
		}

		private synchronized void showDialog(Context context) {
			progressDialog = new ProgressDialog(context);
			progressDialog.setTitle(R.string.initialization);
			progressDialog.setMessage(getString(R.string.initialization_progress_message));
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

				InputStream bufferedInputStream = new BufferedInputStream(getAssets().open(INDEXES_FILE_NAME), AssetManager.ACCESS_STREAMING);
				ZipInputStream zipInputStream = new ZipInputStream(bufferedInputStream);

				ZipEntry entry;
				while ((entry = zipInputStream.getNextEntry()) != null) {
					long time = System.currentTimeMillis();
					if (!entry.isDirectory()) {
						File file = new File(destination, entry.getName());
						file.getParentFile().mkdirs();
						FileOutputStream outputStream = new FileOutputStream(file);
						IOUtils.copy(zipInputStream, outputStream);

						closeStream(outputStream);

						currentProgress++;
						updateDialog();
						Log.d(LOG_TAG, "Extracting " + entry.getName() + " took " + (System.currentTimeMillis() - time) + "ms");
					}
				}

				closeStream(zipInputStream);

				if (currentProgress < INDEXES_FILE_COUNT) {
					Log.e(LOG_TAG, "Not all files were extracted as expected");
				} else {
					PackageManager manager = MainActivity.this.getPackageManager();
					PackageInfo info = manager.getPackageInfo(MainActivity.this.getPackageName(), 0);

					Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this.getApplicationContext()).edit();
					editor.putInt(PreferenceEnum.INDEX_FILES_VERSION.key(), info.versionCode);
					editor.commit();
				}

			} catch (Exception e) {
				Log.e("", "Error when extracting index files", e);
			} finally {
				dismissDialog();
			}

			Log.i(LOG_TAG, "FileExtractorTask finished in " + (System.currentTimeMillis() - startTime) + "ms");
		}

	}
}
