package net.makimono.util;

import java.util.HashMap;
import java.util.Map;

import net.makimono.R;
import net.makimono.model.Language;
import android.content.ContextWrapper;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

public class MeaningTextViewFactory {

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

	private ContextWrapper context;

	public MeaningTextViewFactory(ContextWrapper context) {
		super();
		this.context = context;
	}

	public TextView makeView(CharSequence text, Language language) {
		TextView textView = new TextView(context);
		textView.setText(text);
		textView.setCompoundDrawablesWithIntrinsicBounds(LANGUAGE_ICONS.get(language), 0, 0, 0);
		textView.setCompoundDrawablePadding(getPixelForDip(10));
		textView.setPadding(getPixelForDip(10), getPixelForDip(5), getPixelForDip(10), getPixelForDip(5));
		textView.setGravity(Gravity.CENTER_VERTICAL);
		return textView;
	}

	private int getPixelForDip(int dip) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) dip, context.getResources().getDisplayMetrics());
	}
}
