package net.makimono.dictionary.activity;

import java.io.InputStream;
import java.util.ArrayList;
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

import org.apache.commons.lang3.math.NumberUtils;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class RadicalSearchActivity extends AbstractDefaultActivity {
	private static final String LOG_TAG = RadicalSearchActivity.class.getSimpleName();

	/**
	 * The font DroidSansJapanese does not seem to support the CJK radical
	 * characters. This map replaces those characters with their Kanji
	 * counterparts. Even though they might look the same, their code points
	 * differ.
	 */
	@SuppressWarnings("serial")
	private static final Map<String, String> CHARACTER_SUBSTITUTES = new HashMap<String, String>() {
		{
			put("⺅", "亻");
			put("⺹", "耂");
			put("⺾", "艹");
			put("辶", "辶");
			put("⻏", " 阝");
			put("⻖", "阝 ");
		}
	};

	private Set<String> selectedRadicals = new HashSet<String>();

	private TextView strokeCountText;
	private SeekBar minStrokeCountsBar;
	private SeekBar maxStrokeCountsBar;
	private GridView radicalsGridView;
	private Button searchButton;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.radical_search);

		strokeCountText = (TextView) findViewById(R.id.strokeCountText);

		minStrokeCountsBar = (SeekBar) findViewById(R.id.minStrokeCount);
		minStrokeCountsBar.setOnSeekBarChangeListener(new SeekBarListener());
		maxStrokeCountsBar = (SeekBar) findViewById(R.id.maxStrokeCount);
		maxStrokeCountsBar.setOnSeekBarChangeListener(new SeekBarListener());
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
			}
		});

		searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RadicalSearchActivity.this, KanjiSearchActivity.class);
				intent.setAction(net.makimono.dictionary.Intent.ACTION_RADICAL_SEARCH);
				intent.putExtra(net.makimono.dictionary.Intent.EXTRA_MIN_STROKES, minStrokeCountsBar.getProgress());
				intent.putExtra(net.makimono.dictionary.Intent.EXTRA_MAX_STROKES, maxStrokeCountsBar.getProgress());
				intent.putExtra(net.makimono.dictionary.Intent.EXTRA_RADICALS, selectedRadicals.toArray(new String[selectedRadicals.size()]));
				startActivity(intent);
			}
		});
	}

	private void updateStrokeIndexText() {
		String text = getString(R.string.the_kanji_has_between_x_and_y_strokes, minStrokeCountsBar.getProgress(), maxStrokeCountsBar.getProgress());
		strokeCountText.setText(text);
	}

	private void updateItem(View view, String radical) {
		if (selectedRadicals.contains(radical)) {
			view.setBackgroundResource(R.drawable.selected_radical_background);
		} else {
			view.setBackgroundDrawable(null);
		}
	}

	private class SeekBarListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			updateStrokeIndexText();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

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
					String[] radicals = properties.get(o).toString().split(";");
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
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewGroup layout = new LinearLayout(RadicalSearchActivity.this);

			TextView textView = new net.makimono.dictionary.view.TextView(RadicalSearchActivity.this);
			final String radical = strokesAndRadicals.get(position).toString();
			if (CHARACTER_SUBSTITUTES.containsKey(radical)) {
				textView.setText(CHARACTER_SUBSTITUTES.get(radical));
			} else {
				textView.setText(radical);
			}
			textView.setGravity(Gravity.CENTER);
			if (strokesAndRadicals.get(position) instanceof Integer) {
				textView.setBackgroundColor(Color.BLACK);
				textView.setTextColor(Color.WHITE);
			} else {
				textView.setTextSize(28);
				updateItem(textView, radical);
			}

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(64, 64, Gravity.CENTER);
			layoutParams.setMargins(2, 2, 2, 2);
			layout.addView(textView, layoutParams);
			return layout;
		}
	}
}
