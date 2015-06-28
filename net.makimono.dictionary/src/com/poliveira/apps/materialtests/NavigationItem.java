package com.poliveira.apps.materialtests;

/**
 * Created by poliveira on 24/10/2014.
 */
public class NavigationItem {
	private final String mText;
	private final int mDrawableId;

	public NavigationItem(String text, int drawableId) {
		mText = text;
		mDrawableId = drawableId;
	}

	public int getDrawableId() {
		return mDrawableId;
	}

	public String getText() {
		return mText;
	}
}
