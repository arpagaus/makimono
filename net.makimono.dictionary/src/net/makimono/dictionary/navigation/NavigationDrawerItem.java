package net.makimono.dictionary.navigation;

/**
 * Created by poliveira on 24/10/2014.
 */
public class NavigationDrawerItem {
	private final String text;
	private final int drawableId;

	public NavigationDrawerItem(String text, int drawableId) {
		this.text = text;
		this.drawableId = drawableId;
	}

	public int getDrawableId() {
		return drawableId;
	}

	public String getText() {
		return text;
	}
}
