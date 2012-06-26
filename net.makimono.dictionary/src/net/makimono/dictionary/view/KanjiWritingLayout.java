package net.makimono.dictionary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class KanjiWritingLayout extends ViewGroup {

	private int edgeLength;

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
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int height = MeasureSpec.getSize(heightMeasureSpec);

		final int childCount = getChildCount();

		if (childCount == 0) {
			setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
			return;
		}

		int rowCount = 1;
		int columnCount = childCount;

		int childWidth = (int) (width / columnCount);
		int childHeight = (int) (height / rowCount);

		edgeLength = Math.min(childWidth, childHeight);

		// Find the maximum edge length
		while (childWidth < childHeight) {
			rowCount++;
			columnCount = (int) Math.ceil(childCount / (double) rowCount);

			childWidth = (int) (width / columnCount);
			childHeight = (int) (height / rowCount);

			int length = Math.min(childWidth, childHeight);
			edgeLength = Math.max(edgeLength, length);
			if (edgeLength > length) {
				rowCount--;
				columnCount = Math.round((childCount + 0.5f) / ((float) rowCount));
				break;
			}
		}

		if (edgeLength == 0) {
			setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
			return;
		}

		columnCount = Math.min(childCount, width / edgeLength);

		widthMeasureSpec = MeasureSpec.makeMeasureSpec(columnCount * edgeLength, MeasureSpec.AT_MOST);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(rowCount * edgeLength, MeasureSpec.AT_MOST);

		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
		final int width = right - left;

		int childLeft = 0;
		int childTop = 0;
		int childRight = childLeft + edgeLength;
		int childBottom = childTop + edgeLength;

		for (int i = 0; i < getChildCount(); i++) {
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
