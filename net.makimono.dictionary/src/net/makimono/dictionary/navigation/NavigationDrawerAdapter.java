package net.makimono.dictionary.navigation;

import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import net.makimono.dictionary.R;

/**
 * Created by poliveira on 24/10/2014.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {

	private List<NavigationDrawerItem> navigationItems;
	private NavigationDrawerCallback navigationDrawerCallbacks;
	private int selectedPosition;
	private int touchedPosition = -1;

	public NavigationDrawerAdapter(List<NavigationDrawerItem> navigationItems) {
		this.navigationItems = navigationItems;
	}

	public NavigationDrawerCallback getNavigationDrawerCallbacks() {
		return navigationDrawerCallbacks;
	}

	public void setNavigationDrawerCallbacks(NavigationDrawerCallback navigationDrawerCallback) {
		this.navigationDrawerCallbacks = navigationDrawerCallback;
	}

	@Override
	public NavigationDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.drawer_row, viewGroup, false);
		final ViewHolder viewholder = new ViewHolder(view);
		viewholder.itemView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					touchPosition(viewholder.getAdapterPosition());
					return false;
				case MotionEvent.ACTION_CANCEL:
					touchPosition(-1);
					return false;
				case MotionEvent.ACTION_MOVE:
					return false;
				case MotionEvent.ACTION_UP:
					touchPosition(-1);
					return false;
				}
				return true;
			}
		});
		viewholder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (navigationDrawerCallbacks != null)
					navigationDrawerCallbacks.onNavigationDrawerItemSelected(viewholder.getAdapterPosition());
			}
		});
		return viewholder;
	}

	@Override
	public void onBindViewHolder(NavigationDrawerAdapter.ViewHolder viewHolder, final int position) {
		NavigationDrawerItem item = navigationItems.get(position);
		@SuppressWarnings("deprecation")
		Drawable drawable = viewHolder.textView.getResources().getDrawable(item.getDrawableId());
		viewHolder.textView.setText(item.getText());
		viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

		// TODO: selected menu position, change layout accordingly
		if (selectedPosition == position || touchedPosition == position) {
			viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getContext().getResources().getColor(R.color.colorSelected));
		} else {
			viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
		}
	}

	private void touchPosition(int position) {
		int lastPosition = touchedPosition;
		touchedPosition = position;
		if (lastPosition >= 0)
			notifyItemChanged(lastPosition);
		if (position >= 0)
			notifyItemChanged(position);
	}

	public void selectPosition(int position) {
		int lastPosition = selectedPosition;
		selectedPosition = position;
		notifyItemChanged(lastPosition);
		notifyItemChanged(position);
	}

	@Override
	public int getItemCount() {
		return navigationItems != null ? navigationItems.size() : 0;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView textView;

		public ViewHolder(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.item_name);
		}
	}
}
