package net.makimono.dictionary.navigation;

/**
 * Created by poliveira on 24/10/2014.
 */
public class NavigationDrawerItem {
	private final String mText;
	private final int mDrawableId;

	public NavigationDrawerItem(String text, int drawableId) {
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
