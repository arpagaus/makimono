package net.makimono.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;

public class ExpansionFileAlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			DownloaderClientMarshaller.startDownloadServiceIfRequired(context, intent, ExpansionFileDownloaderService.class);
		} catch (NameNotFoundException e) {
			Log.e(ExpansionFileAlarmReceiver.class.getSimpleName(), "Failed to start downloader service", e);
		}
	}
}