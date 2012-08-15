package net.makimono.dictionary.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.makimono.dictionary.R;
import net.makimono.dictionary.service.SearcherService;
import net.makimono.dictionary.service.SearcherServiceConnection;
import net.makimono.dictionary.util.TypedValueUtil;
import net.makimono.dictionary.view.RangeSeekBar;
import net.makimono.dictionary.view.RangeSeekBar.OnRangeSeekBarChangeListener;

import org.apache.commons.lang3.math.NumberUtils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RadicalSearchActivity extends AbstractDefaultActivity {
	private static final String LOG_TAG = RadicalSearchActivity.class.getSimpleName();

	/**
	 * The font DroidSansJapanese does not seem to support the CJK radical
	 * characters. This map replaces those characters with their Kanji
	 * counterparts. Even though they might look the same, their Unicode code
	 * points differ.
	 */
	@SuppressWarnings("serial")
	public static final Map<String, String> CHARACTER_SUBSTITUTES = new HashMap<String, String>() {
		{
			put("⺅", "亻");
			put("⺹", "耂");
			put("⺾", "艹");
			put("辶", "辶");
			put("⻏", " 阝");
			put("⻖", "阝 ");
		}
	};

	private SearcherServiceConnection connection = new SearcherServiceConnection();

	/**
	 * <code>null</code> means that there is no selection restriction
	 */
	private Set<String> selectableRadicals;
	private Set<String> selectedRadicals = new HashSet<String>();

	private LinearLayout strokeCountLayout;
	private TextView strokeCountText;
	private RangeSeekBar<Integer> strokeCountsSeekBar;
	private GridView radicalsGridView;
	private Button searchButton;
	private Button resetButton;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		getSupportActionBar().setTitle(R.string.radical_search);
		setContentView(R.layout.radical_search);

		strokeCountLayout = (LinearLayout) findViewById(R.id.strokeCountLayout);
		Configuration configuration = getResources().getConfiguration();
		if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			strokeCountLayout.setOrientation(LinearLayout.HORIZONTAL);
		} else {
			strokeCountLayout.setOrientation(LinearLayout.VERTICAL);
		}

		strokeCountText = (TextView) findViewById(R.id.strokeCountText);

		strokeCountsSeekBar = new RangeSeekBar<Integer>(1, 33, this);
		strokeCountsSeekBar.setNotifyWhileDragging(true);
		strokeCountsSeekBar.setOnRangeSeekBarChangeListener(new SeekBarListener());
		((FrameLayout) findViewById(R.id.strokeCount)).addView(strokeCountsSeekBar);
		updateStrokeIndexText();

		radicalsGridView = (GridView) findViewById(R.id.radicals);
		radicalsGridView.setAdapter(new RadicalAdapter());
		radicalsGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Object item = adapterView.getAdapter().getItem(position);
				if (item == null) {
					Log.e(LOG_TAG, "item was null");
					return;
				}

				String radical = item.toString();
				Log.v(LOG_TAG, radical);

				if (NumberUtils.isNumber(radical)) {
					return;
				}

				if (!selectedRadicals.remove(radical)) {
					selectedRadicals.add(radical);
				}
				updateItem(((ViewGroup) view).getChildAt(0), radical);

				try {
					selectableRadicals = connection.getKanjiSearcher().getSelectableRadicals(selectedRadicals, strokeCountsSeekBar.getAbsoluteMinValue(), strokeCountsSeekBar.getAbsoluteMaxValue());
				} catch (IOException e) {
					selectableRadicals = null;
				}
				((BaseAdapter) radicalsGridView.getAdapter()).notifyDataSetChanged();
			}
		});

		searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RadicalSearchActivity.this, KanjiSearchActivity.class);
				intent.setAction(net.makimono.dictionary.Intent.ACTION_RADICAL_SEARCH);
				intent.putExtra(net.makimono.dictionary.Intent.EXTRA_MIN_STROKES, strokeCountsSeekBar.getSelectedMinValue());
				intent.putExtra(net.makimono.dictionary.Intent.EXTRA_MAX_STROKES, strokeCountsSeekBar.getSelectedMaxValue());
				intent.putExtra(net.makimono.dictionary.Intent.EXTRA_RADICALS, selectedRadicals.toArray(new String[selectedRadicals.size()]));
				startActivity(intent);
			}
		});

		resetButton = (Button) findViewById(R.id.resetButton);
		resetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				strokeCountsSeekBar.setSelectedMinValue(strokeCountsSeekBar.getAbsoluteMinValue());
				strokeCountsSeekBar.setSelectedMaxValue(strokeCountsSeekBar.getAbsoluteMaxValue());
				updateStrokeIndexText();

				selectedRadicals.clear();
				selectableRadicals = null;
				((BaseAdapter) radicalsGridView.getAdapter()).notifyDataSetChanged();
			}
		});

		bindSearcher();
	}

	private void updateStrokeIndexText() {
		String text = getString(R.string.between_x_and_y_strokes, strokeCountsSeekBar.getSelectedMinValue(), strokeCountsSeekBar.getSelectedMaxValue());
		strokeCountText.setText(text);
	}

	private void updateItem(View view, String radical) {
		if (selectedRadicals.contains(radical)) {
			view.setBackgroundResource(R.drawable.selected_radical_background);
		} else {
			view.setBackgroundDrawable(null);
		}
	}

	private class SeekBarListener implements OnRangeSeekBarChangeListener<Integer> {
		@Override
		public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
			updateStrokeIndexText();
		}
	}

	private void bindSearcher() {
		Intent intent = new Intent(this, SearcherService.class);
		bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(connection);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putStringArray(net.makimono.dictionary.Intent.EXTRA_RADICALS, selectedRadicals.toArray(new String[selectedRadicals.size()]));
		outState.putInt(net.makimono.dictionary.Intent.EXTRA_MIN_STROKES, strokeCountsSeekBar.getSelectedMinValue());
		outState.putInt(net.makimono.dictionary.Intent.EXTRA_MAX_STROKES, strokeCountsSeekBar.getSelectedMaxValue());
	}

	@Override
	protected void onRestoreInstanceState(Bundle instanceState) {
		super.onRestoreInstanceState(instanceState);
		selectedRadicals.addAll(Arrays.asList(instanceState.getStringArray(net.makimono.dictionary.Intent.EXTRA_RADICALS)));
		((BaseAdapter) radicalsGridView.getAdapter()).notifyDataSetChanged();

		strokeCountsSeekBar.setSelectedMinValue(instanceState.getInt(net.makimono.dictionary.Intent.EXTRA_MIN_STROKES, strokeCountsSeekBar.getAbsoluteMinValue()));
		strokeCountsSeekBar.setSelectedMaxValue(instanceState.getInt(net.makimono.dictionary.Intent.EXTRA_MAX_STROKES, strokeCountsSeekBar.getAbsoluteMaxValue()));
		updateStrokeIndexText();
	}

	private class RadicalAdapter extends BaseAdapter {

		private List<Object> strokesAndRadicals;

		RadicalAdapter() {
			strokesAndRadicals = new ArrayList<Object>();

			try {
				Properties properties = new Properties();
				InputStream input = getAssets().open("radicals.xml");
				properties.loadFromXML(input);
				input.close();

				SortedSet<Object> radicalCounts = new TreeSet<Object>(new Comparator<Object>() {
					@Override
					public int compare(Object lhs, Object rhs) {
						return Integer.valueOf(lhs.toString()).compareTo(Integer.valueOf(rhs.toString()));
					}
				});
				radicalCounts.addAll(properties.keySet());

				for (Object o : radicalCounts) {
					strokesAndRadicals.add(Integer.valueOf(o.toString()));
					Collection<String> radicals = new TreeSet<String>(Arrays.asList(properties.get(o).toString().split(";")));
					for (String r : radicals) {
						strokesAndRadicals.add(r);
					}
				}
			} catch (Exception e) {
				Log.e(LOG_TAG, "Failed to load 'radicals.xml'", e);
			}
		}

		@Override
		public int getCount() {
			return strokesAndRadicals.size();
		}

		@Override
		public Object getItem(int i) {
			return strokesAndRadicals.get(i);
		}

		@Override
		public long getItemId(int id) {
			return id;
		}

		@Override
		public boolean isEnabled(int position) {
			if (strokesAndRadicals.get(position) instanceof Integer) {
				return false;
			}
			return !isNotSelectableRadical(strokesAndRadicals.get(position).toString());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = new net.makimono.dictionary.view.TextView(RadicalSearchActivity.this);
			final String radical = strokesAndRadicals.get(position).toString();
			if (CHARACTER_SUBSTITUTES.containsKey(radical)) {
				textView.setText(CHARACTER_SUBSTITUTES.get(radical));
			} else {
				textView.setText(radical);
			}
			textView.setGravity(Gravity.CENTER);
			if (strokesAndRadicals.get(position) instanceof Integer) {
				textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
				textView.setBackgroundResource(R.drawable.radical_stroke_count_background);
				textView.setTextColor(Color.WHITE);
				textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			} else {
				textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
				updateItem(textView, radical);

				if (isNotSelectableRadical(radical)) {
					textView.setTextColor(Color.LTGRAY);
				}
			}

			int size = TypedValueUtil.getPixelForDip(36, getResources().getDisplayMetrics());
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
			int padding = TypedValueUtil.getPixelForDip(2, getResources().getDisplayMetrics());
			layoutParams.setMargins(padding, padding, padding, padding);

			ViewGroup layout = new LinearLayout(RadicalSearchActivity.this);
			layout.addView(textView, layoutParams);
			return layout;
		}

		private boolean isNotSelectableRadical(final String radical) {
			return selectableRadicals != null && !selectableRadicals.contains(radical);
		}
	}
}
