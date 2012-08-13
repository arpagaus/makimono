package net.makimono.dictionary.view;

import net.makimono.dictionary.R;
import net.makimono.dictionary.util.TypedValueUtil;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

public class NonScrollingListView extends LinearLayout {

	private OnItemClickListener onItemClickListener;
	private ListAdapter adapter;
	private DataSetObserver dataSetObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			super.onChanged();
			updateView();
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
			updateView();
		}

	};

	public NonScrollingListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public NonScrollingListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NonScrollingListView(Context context) {
		super(context);
	}

	public ListAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(ListAdapter adapter) {
		if (this.adapter != null) {
			this.adapter.unregisterDataSetObserver(dataSetObserver);
		}

		adapter.registerDataSetObserver(dataSetObserver);
		this.adapter = adapter;
	}

	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	private void updateView() {
		removeAllViews();

		if (adapter != null) {
			for (int i = 0; i < adapter.getCount(); i++) {
				if (i != 0) {
					addView(createDivider());
				}
				final int index = i;
				final View convertView = adapter.getView(i, null, this);
				convertView.setBackgroundResource(R.drawable.clickable_background);
				int horizontalPadding = TypedValueUtil.getPixelForDip(5, getResources().getDisplayMetrics());
				int verticalPadding = TypedValueUtil.getPixelForDip(10, getResources().getDisplayMetrics());
				convertView.setPadding(verticalPadding, horizontalPadding, verticalPadding, horizontalPadding);
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (onItemClickListener != null) {
							onItemClickListener.onItemClick(null, convertView, index, adapter.getItemId(index));
						}
					}
				});
				addView(convertView);
			}
		}
	}

	private View createDivider() {
		View separator = new View(getContext());
		separator.setBackgroundResource(R.drawable.secondary_separator);
		separator.setMinimumHeight(1);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int verticalMargin = TypedValueUtil.getPixelForDip(10, getResources().getDisplayMetrics());
		layoutParams.rightMargin = verticalMargin;
		layoutParams.leftMargin = verticalMargin;
		separator.setLayoutParams(layoutParams);
		return separator;
	}
}
