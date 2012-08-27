package net.makimono.dictionary.adapter;

import java.util.HashMap;
import java.util.Map;

import net.makimono.dictionary.R;
import net.makimono.dictionary.model.ExampleEntry;
import net.makimono.dictionary.model.Language;
import net.makimono.dictionary.util.TypedValueUtil;
import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExampleSearchResultAdapter extends SearchResultAdapter {

	@SuppressWarnings("serial")
	public static final Map<Language, Integer> LANGUAGE_ICONS = new HashMap<Language, Integer>() {
		{
			put(Language.en, R.drawable.ic_english_small);
			put(Language.de, R.drawable.ic_german_small);
			put(Language.fr, R.drawable.ic_french_small);
			put(Language.ru, R.drawable.ic_russian_small);
			put(Language.es, R.drawable.ic_spanish_small);
			put(Language.pt, R.drawable.ic_portugese_small);
		}
	};

	private final int padding;
	private final float japaneseTextSize;

	public ExampleSearchResultAdapter(Context context) {
		super(context);
		padding = TypedValueUtil.getPixelForDip(5f, context.getResources().getDisplayMetrics());
		japaneseTextSize = context.getResources().getDimension(R.dimen.content_text_size);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ExampleEntry example = (ExampleEntry) getItem(position);
		LinearLayout layout = new LinearLayout(parent.getContext());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(padding, padding, padding, padding);

		if (example != null) {
			net.makimono.dictionary.view.TextView japaneseTextView = new net.makimono.dictionary.view.TextView(parent.getContext());
			japaneseTextView.setText(example.getSentence(Language.ja));
			japaneseTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, japaneseTextSize);
			japaneseTextView.setSingleLine(true);
			japaneseTextView.setEllipsize(TruncateAt.END);
			japaneseTextView.setPadding(padding, padding, padding, padding);
			layout.addView(japaneseTextView);

			for (Language language : getLanguages()) {
				if (example.hasTranslation(language)) {
					TextView textView = new TextView(parent.getContext());
					textView.setSingleLine(true);
					textView.setEllipsize(TruncateAt.END);
					textView.setCompoundDrawablesWithIntrinsicBounds(LANGUAGE_ICONS.get(language), 0, 0, 0);
					textView.setCompoundDrawablePadding(padding);

					textView.setText(example.getSentence(language));
					layout.addView(textView);
				}
			}
		}

		return layout;
	}
}
