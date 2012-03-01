package net.makimono.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.makimono.R;
import net.makimono.model.Gloss;
import net.makimono.model.KanjiEntry;
import net.makimono.model.Language;
import net.makimono.service.SearcherService;
import net.makimono.service.SearcherServiceConnection;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KanjiEntryActivity extends AbstractDefaultActivity {
	public static final String EXTRA_CODE_POINT = KanjiEntryActivity.class.getName() + ".EXTRA_CODE_POINT";

	@SuppressWarnings("serial")
	private static final Map<Language, Integer> LANGUAGE_ICONS = new HashMap<Language, Integer>() {
		{
			put(Language.en, R.drawable.ic_english);
			put(Language.de, R.drawable.ic_german);
			put(Language.fr, R.drawable.ic_french);
			put(Language.ru, R.drawable.ic_russian);
			put(Language.es, R.drawable.ic_spanish);
			put(Language.pt, R.drawable.ic_portugese);
		}
	};

	SearcherServiceConnection connection = new SearcherServiceConnection();

	private TextView literalTextView;
	private TextView onYomiTextView;
	private TextView kunYomiTextView;
	private LinearLayout meaningsGroupView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeContentView();
		bindSearcher();
		handleIntent(getIntent());
	}

	private void initializeContentView() {
		setContentView(R.layout.kanji_entry);
		literalTextView = (TextView) findViewById(R.id.kanji_literal);
		onYomiTextView = (TextView) findViewById(R.id.kanji_on_yomi);
		kunYomiTextView = (TextView) findViewById(R.id.kanji_kun_yomi);
		meaningsGroupView = (LinearLayout) findViewById(R.id.kanji_meanings);
	}

	private void bindSearcher() {
		Intent intent = new Intent(this, SearcherService.class);
		bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	private void handleIntent(Intent intent) {
		if (intent.hasExtra(EXTRA_CODE_POINT)) {
			int codePoint = intent.getIntExtra(EXTRA_CODE_POINT, 0);
			KanjiEntryTask task = new KanjiEntryTask(this);
			task.execute(codePoint);
		}
	}

	void updateView(KanjiEntry entry) {
		literalTextView.setText(entry.getLiteral());
		onYomiTextView.setText(StringUtils.join(entry.getOnYomi(), ", "));
		kunYomiTextView.setText(StringUtils.join(entry.getKunYomi(), ", "));

		meaningsGroupView.removeAllViews();
		ArrayList<Language> languages = PreferenceActivity.getConfiguredLanguages(PreferenceManager.getDefaultSharedPreferences(this));
		for (Language language : languages) {
			CharSequence gloss = Gloss.getGlossString(language, entry.getGlosses());
			if (gloss.length() > 0) {
				TextView textView = new TextView(this);
				textView.setText(gloss);
				textView.setCompoundDrawablesWithIntrinsicBounds(LANGUAGE_ICONS.get(language), 0, 0, 0);
				textView.setCompoundDrawablePadding(getPixelForDip(15));
				textView.setPadding(0, getPixelForDip(5), 0, getPixelForDip(5));
				textView.setGravity(Gravity.CENTER_VERTICAL);
				meaningsGroupView.addView(textView);
			}
		}
	}

	private int getPixelForDip(int dip) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) dip, getResources().getDisplayMetrics());
	}
}
