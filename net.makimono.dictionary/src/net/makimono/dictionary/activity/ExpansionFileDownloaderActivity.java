package net.makimono.dictionary.activity;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.CRC32;

import net.makimono.dictionary.R;
import net.makimono.dictionary.service.ExpansionFileDownloaderService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Messenger;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.android.vending.expansion.zipfile.ZipResourceFile.ZipEntryRO;
import com.google.android.vending.expansion.downloader.Constants;
import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.google.android.vending.expansion.downloader.DownloaderServiceMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderClient;
import com.google.android.vending.expansion.downloader.IDownloaderService;
import com.google.android.vending.expansion.downloader.IStub;

/**
 * This is sample code for a project built against the downloader library. It
 * implements the IDownloaderClient that the client marshaler will talk to as
 * messages are delivered from the DownloaderService.
 */
public class ExpansionFileDownloaderActivity extends SherlockActivity implements IDownloaderClient {
	private static final String LOG_TAG = ExpansionFileDownloaderActivity.class.getSimpleName();
	private ProgressBar mPB;

	private TextView mStatusText;
	private TextView mProgressFraction;
	private TextView mProgressPercent;
	private TextView mAverageSpeed;
	private TextView mTimeRemaining;

	private View mDashboard;
	private View mCellMessage;

	private Button mPauseButton;
	private Button mWiFiSettingsButton;

	private boolean mStatePaused;
	private int mState;

	private IDownloaderService mRemoteService;

	private IStub mDownloaderClientStub;

	private void setState(int newState) {
		if (mState != newState) {
			mState = newState;
			mStatusText.setText(Helpers.getDownloaderStringResourceIDFromState(newState));
		}
	}

	private void setButtonPausedState(boolean paused) {
		mStatePaused = paused;
		int stringResourceID = paused ? R.string.text_button_resume : R.string.text_button_pause;
		mPauseButton.setText(stringResourceID);
	}

	/**
	 * This is a little helper class that demonstrates simple testing of an
	 * Expansion APK file delivered by Market. You may not wish to hard-code
	 * things such as file lengths into your executable... and you may wish to
	 * turn this code off during application development.
	 */
	public static class XAPKFile {
		public final int fileVersion;
		public final long fileSize;

		XAPKFile(int fileVersion, long fileSize) {
			this.fileVersion = fileVersion;
			this.fileSize = fileSize;
		}
	}

	public static final XAPKFile MAIN_FILE = new XAPKFile(1, 25170186L);

	/**
	 * Go through each of the Expansion APK files defined in the project and
	 * determine if the files are present and match the required size. Free
	 * applications should definitely consider doing this, as this allows the
	 * application to be launched for the first time without having a network
	 * connection present. Paid applications that use LVL should probably do at
	 * least one LVL check that requires the network to be present, so this is
	 * not as necessary.
	 * 
	 * @return true if they are present.
	 */
	boolean expansionFilesDelivered() {
		String fileName = Helpers.getExpansionAPKFileName(this, true, MAIN_FILE.fileVersion);
		if (!Helpers.doesFileExist(this, fileName, MAIN_FILE.fileSize, false))
			return false;
		return true;
	}

	/**
	 * Calculating a moving average for the validation speed so we don't get
	 * jumpy calculations for time etc.
	 */
	static private final float SMOOTHING_FACTOR = 0.005f;

	/**
	 * Used by the async task
	 */
	private boolean mCancelValidation;

	/**
	 * Go through each of the Expansion APK files and open each as a zip file.
	 * Calculate the CRC for each file and return false if any fail to match.
	 * 
	 * @return true if XAPKZipFile is successful
	 */
	void validateXAPKZipFiles() {
		AsyncTask<Object, DownloadProgressInfo, Boolean> validationTask = new AsyncTask<Object, DownloadProgressInfo, Boolean>() {

			@Override
			protected void onPreExecute() {
				mDashboard.setVisibility(View.VISIBLE);
				mCellMessage.setVisibility(View.GONE);
				mStatusText.setText(R.string.text_verifying_download);
				mPauseButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mCancelValidation = true;
					}
				});
				mPauseButton.setText(R.string.text_button_cancel_verify);
				super.onPreExecute();
			}

			@Override
			protected Boolean doInBackground(Object... params) {
				String fileName = Helpers.getExpansionAPKFileName(ExpansionFileDownloaderActivity.this, true, MAIN_FILE.fileVersion);
				if (!Helpers.doesFileExist(ExpansionFileDownloaderActivity.this, fileName, MAIN_FILE.fileSize, false))
					return false;
				fileName = Helpers.generateSaveFileName(ExpansionFileDownloaderActivity.this, fileName);
				ZipResourceFile zrf;
				byte[] buf = new byte[1024 * 256];
				try {
					zrf = new ZipResourceFile(fileName);
					ZipEntryRO[] entries = zrf.getAllEntries();
					/**
					 * First calculate the total compressed length
					 */
					long totalCompressedLength = 0;
					for (ZipEntryRO entry : entries) {
						totalCompressedLength += entry.mCompressedLength;
					}
					float averageVerifySpeed = 0;
					long totalBytesRemaining = totalCompressedLength;
					long timeRemaining;
					/**
					 * Then calculate a CRC for every file in the Zip file,
					 * comparing it to what is stored in the Zip directory
					 */
					for (ZipEntryRO entry : entries) {
						if (-1 != entry.mCRC32) {
							long offset = entry.getOffset();
							long length = entry.mCompressedLength;
							CRC32 crc = new CRC32();
							RandomAccessFile raf = new RandomAccessFile(fileName, "r");
							raf.seek(offset);
							long startTime = SystemClock.uptimeMillis();
							while (length > 0) {
								int seek = (int) (length > buf.length ? buf.length : length);
								raf.readFully(buf, 0, seek);
								crc.update(buf, 0, seek);
								length -= seek;
								long currentTime = SystemClock.uptimeMillis();
								long timePassed = currentTime - startTime;
								if (timePassed > 0) {
									float currentSpeedSample = (float) seek / (float) timePassed;
									if (0 != averageVerifySpeed) {
										averageVerifySpeed = SMOOTHING_FACTOR * currentSpeedSample + (1 - SMOOTHING_FACTOR) * averageVerifySpeed;
									} else {
										averageVerifySpeed = currentSpeedSample;
									}
									totalBytesRemaining -= seek;
									timeRemaining = (long) (totalBytesRemaining / averageVerifySpeed);
									this.publishProgress(new DownloadProgressInfo(totalCompressedLength, totalCompressedLength - totalBytesRemaining, timeRemaining, averageVerifySpeed));
								}
								startTime = currentTime;
								if (mCancelValidation)
									return true;
							}
							if (crc.getValue() != entry.mCRC32) {
								Log.e(Constants.TAG, "CRC does not match for entry: " + entry.mFileName);
								Log.e(Constants.TAG, "In file: " + entry.getZipFileName());
								return false;
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}

			@Override
			protected void onProgressUpdate(DownloadProgressInfo... values) {
				onDownloadProgress(values[0]);
				super.onProgressUpdate(values);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					mDashboard.setVisibility(View.VISIBLE);
					mCellMessage.setVisibility(View.GONE);
					mStatusText.setText(R.string.text_validation_complete);
					mPauseButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							finish();
						}
					});
					mPauseButton.setText(android.R.string.ok);
				} else {
					mDashboard.setVisibility(View.VISIBLE);
					mCellMessage.setVisibility(View.GONE);
					mStatusText.setText(R.string.text_validation_failed);
					mPauseButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							finish();
						}
					});
					mPauseButton.setText(android.R.string.cancel);
				}
				super.onPostExecute(result);
			}

		};
		validationTask.execute(new Object());
	}

	/**
	 * If the download isn't present, we initialize the download UI. This ties
	 * all of the controls into the remote service calls.
	 */
	private void initializeDownloadUI() {
		mDownloaderClientStub = DownloaderClientMarshaller.CreateStub(this, ExpansionFileDownloaderService.class);
		setContentView(R.layout.expansion_downloader);

		mPB = (ProgressBar) findViewById(R.id.progressBar);
		mStatusText = (TextView) findViewById(R.id.statusText);
		mProgressFraction = (TextView) findViewById(R.id.progressAsFraction);
		mProgressPercent = (TextView) findViewById(R.id.progressAsPercentage);
		mAverageSpeed = (TextView) findViewById(R.id.progressAverageSpeed);
		mTimeRemaining = (TextView) findViewById(R.id.progressTimeRemaining);
		mDashboard = findViewById(R.id.downloaderDashboard);
		mCellMessage = findViewById(R.id.approveCellular);
		mPauseButton = (Button) findViewById(R.id.pauseButton);
		mWiFiSettingsButton = (Button) findViewById(R.id.wifiSettingsButton);

		mPauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mStatePaused) {
					mRemoteService.requestContinueDownload();
				} else {
					mRemoteService.requestPauseDownload();
				}
				setButtonPausedState(!mStatePaused);
			}
		});

		mWiFiSettingsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			}
		});

		Button resumeOnCell = (Button) findViewById(R.id.resumeOverCellular);
		resumeOnCell.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mRemoteService.setDownloadFlags(IDownloaderService.FLAGS_DOWNLOAD_OVER_CELLULAR);
				mRemoteService.requestContinueDownload();
				mCellMessage.setVisibility(View.GONE);
			}
		});

	}

	/**
	 * Called when the activity is first create; we wouldn't create a layout in
	 * the case where we have the file and are moving to another activity
	 * without downloading.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 * Before we do anything, are the files we expect already here and
		 * delivered (presumably by Market) For free titles, this is probably
		 * worth doing. (so no Market request is necessary)
		 */
		if (!expansionFilesDelivered()) {

			try {
				Intent launchIntent = ExpansionFileDownloaderActivity.this.getIntent();
				Intent intentToLaunchThisActivityFromNotification = new Intent(ExpansionFileDownloaderActivity.this, ExpansionFileDownloaderActivity.this.getClass());
				intentToLaunchThisActivityFromNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intentToLaunchThisActivityFromNotification.setAction(launchIntent.getAction());

				if (launchIntent.getCategories() != null) {
					for (String category : launchIntent.getCategories()) {
						intentToLaunchThisActivityFromNotification.addCategory(category);
					}
				}

				// Build PendingIntent used to open this activity from
				// Notification
				PendingIntent pendingIntent = PendingIntent.getActivity(ExpansionFileDownloaderActivity.this, 0, intentToLaunchThisActivityFromNotification, PendingIntent.FLAG_UPDATE_CURRENT);
				// Request to start the download
				int startResult = DownloaderClientMarshaller.startDownloadServiceIfRequired(this, pendingIntent, ExpansionFileDownloaderService.class);

				if (startResult != DownloaderClientMarshaller.NO_DOWNLOAD_REQUIRED) {
					// The DownloaderService has started downloading the files,
					// show progress
					initializeDownloadUI();
					return;
				} // otherwise, download not needed so we fall through to
					// starting the movie
			} catch (NameNotFoundException e) {
				Log.e(LOG_TAG, "Cannot find own package! MAYDAY!");
				e.printStackTrace();
			}

		}

		initializeDownloadUI();
		validateXAPKZipFiles();
	}

	/**
	 * Connect the stub to our service on resume.
	 */
	@Override
	protected void onResume() {
		if (null != mDownloaderClientStub) {
			mDownloaderClientStub.connect(this);
		}
		super.onResume();
	}

	/**
	 * Disconnect the stub from our service on stop
	 */
	@Override
	protected void onStop() {
		if (null != mDownloaderClientStub) {
			mDownloaderClientStub.disconnect(this);
		}
		super.onStop();
	}

	/**
	 * Critical implementation detail. In onServiceConnected we create the
	 * remote service and marshaler. This is how we pass the client information
	 * back to the service so the client can be properly notified of changes. We
	 * must do this every time we reconnect to the service.
	 */
	@Override
	public void onServiceConnected(Messenger m) {
		mRemoteService = DownloaderServiceMarshaller.CreateProxy(m);
		mRemoteService.onClientUpdated(mDownloaderClientStub.getMessenger());
	}

	/**
	 * The download state should trigger changes in the UI --- it may be useful
	 * to show the state as being indeterminate at times. This sample can be
	 * considered a guideline.
	 */
	@Override
	public void onDownloadStateChanged(int newState) {
		setState(newState);
		boolean showDashboard = true;
		boolean showCellMessage = false;
		boolean paused;
		boolean indeterminate;
		switch (newState) {
		case IDownloaderClient.STATE_IDLE:
			// STATE_IDLE means the service is listening, so it's
			// safe to start making calls via mRemoteService.
			paused = false;
			indeterminate = true;
			break;
		case IDownloaderClient.STATE_CONNECTING:
		case IDownloaderClient.STATE_FETCHING_URL:
			showDashboard = true;
			paused = false;
			indeterminate = true;
			break;
		case IDownloaderClient.STATE_DOWNLOADING:
			paused = false;
			showDashboard = true;
			indeterminate = false;
			break;

		case IDownloaderClient.STATE_FAILED_CANCELED:
		case IDownloaderClient.STATE_FAILED:
		case IDownloaderClient.STATE_FAILED_FETCHING_URL:
		case IDownloaderClient.STATE_FAILED_UNLICENSED:
			paused = true;
			showDashboard = false;
			indeterminate = false;
			break;
		case IDownloaderClient.STATE_PAUSED_NEED_CELLULAR_PERMISSION:
		case IDownloaderClient.STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION:
			showDashboard = false;
			paused = true;
			indeterminate = false;
			showCellMessage = true;
			break;
		case IDownloaderClient.STATE_PAUSED_BY_REQUEST:
			paused = true;
			indeterminate = false;
			break;
		case IDownloaderClient.STATE_PAUSED_ROAMING:
		case IDownloaderClient.STATE_PAUSED_SDCARD_UNAVAILABLE:
			paused = true;
			indeterminate = false;
			break;
		case IDownloaderClient.STATE_COMPLETED:
			showDashboard = false;
			paused = false;
			indeterminate = false;
			validateXAPKZipFiles();
			return;
		default:
			paused = true;
			indeterminate = true;
			showDashboard = true;
		}
		int newDashboardVisibility = showDashboard ? View.VISIBLE : View.GONE;
		if (mDashboard.getVisibility() != newDashboardVisibility) {
			mDashboard.setVisibility(newDashboardVisibility);
		}
		int cellMessageVisibility = showCellMessage ? View.VISIBLE : View.GONE;
		if (mCellMessage.getVisibility() != cellMessageVisibility) {
			mCellMessage.setVisibility(cellMessageVisibility);
		}
		mPB.setIndeterminate(indeterminate);
		setButtonPausedState(paused);
	}

	/**
	 * Sets the state of the various controls based on the progressinfo object
	 * sent from the downloader service.
	 */
	@Override
	public void onDownloadProgress(DownloadProgressInfo progress) {
		mAverageSpeed.setText(getString(R.string.kilobytes_per_second, Helpers.getSpeedString(progress.mCurrentSpeed)));
		mTimeRemaining.setText(getString(R.string.time_remaining, Helpers.getTimeRemaining(progress.mTimeRemaining)));

		progress.mOverallTotal = progress.mOverallTotal;
		mPB.setMax((int) (progress.mOverallTotal >> 8));
		mPB.setProgress((int) (progress.mOverallProgress >> 8));
		mProgressPercent.setText(Long.toString(progress.mOverallProgress * 100 / progress.mOverallTotal) + "%");
		mProgressFraction.setText(Helpers.getDownloadProgressString(progress.mOverallProgress, progress.mOverallTotal));
	}

	@Override
	protected void onDestroy() {
		this.mCancelValidation = true;
		super.onDestroy();
	}

}