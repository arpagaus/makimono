package net.makimono.util;

import java.io.File;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

public class ExternalStorageUtil {

	public static File getExternalFilesDir(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			return context.getExternalFilesDir(null);
		} else {
			return new File(Environment.getExternalStorageDirectory(), "/Android/data/" + context.getPackageName() + "/files/");
		}
	}
}
