package net.makimono.dictionary.util;

import android.util.DisplayMetrics;
import android.util.TypedValue;

public class TypedValueUtil {
	public static int getPixelForDip(float dip, DisplayMetrics metrics) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics);
	}
}
