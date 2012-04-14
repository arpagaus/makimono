package net.makimono.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class KanjiWritingLayout extends ViewGroup {

	public KanjiWritingLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public KanjiWritingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public KanjiWritingLayout(Context context) {
		super(context);
	}

	@Override
	protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
		final int width = right - left;
		final int height = bottom - top;

		final int childCount = getChildCount();

		int rowCount = 1;
		int columnCount = childCount;

		int childWidth = (int) (width / columnCount);
		int childHeight = (int) (height / rowCount);

		int edgeLength = Math.min(childWidth, childHeight);

		// Find the maximum edge length
		while (childWidth < childHeight) {
			rowCount++;
			columnCount = Math.round((childCount + 0.5f) / ((float) rowCount));

			childWidth = (int) (width / columnCount);
			childHeight = (int) (height / rowCount);

			int length = Math.min(childWidth, childHeight);
			edgeLength = Math.max(edgeLength, length);
		}

		int childLeft = 0;
		int childTop = 0;
		int childRight = childLeft + edgeLength;
		int childBottom = childTop + edgeLength;

		for (int i = 0; i < childCount; i++) {
			final View child = getChildAt(i);
			child.layout(childLeft, childTop, childRight, childBottom);

			if (width - childRight < edgeLength) {
				childLeft = 0;
				childTop = childTop + edgeLength;
				childBottom = childTop + edgeLength;
			} else {
				childLeft = childRight;
			}
			childRight = childLeft + edgeLength;
		}

	}
}
