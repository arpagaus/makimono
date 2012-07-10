package net.makimono.dictionary.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class TextView extends android.widget.TextView {

	private static Typeface japaneseTypeface;

	public TextView(Context context) {
		super(context);
		initialize();
	}

	public TextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public TextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	private void initialize() {
		if (japaneseTypeface == null) {
			japaneseTypeface = Typeface.createFromAsset(getContext().getAssets(), "DroidSansJapanese.ttf");
		}
		setTypeface(japaneseTypeface);
	}
}
