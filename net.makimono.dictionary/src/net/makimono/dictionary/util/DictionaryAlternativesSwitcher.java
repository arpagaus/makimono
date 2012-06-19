package net.makimono.dictionary.util;

import java.util.List;

import net.makimono.dictionary.R;
import net.makimono.dictionary.model.DictionaryEntry;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextSwitcher;

public class DictionaryAlternativesSwitcher {
	private TextSwitcher expressionTextSwitcher;
	private TextSwitcher readingTextSwitcher;

	private ImageButton nextExpressionButton;
	private ImageButton previousExpressionButton;
	private ImageButton nextReadingButton;
	private ImageButton previousReadingButton;

	private DictionaryEntry entry;

	private int currentExpressionIndex;
	private int currentReadingIndex;

	public DictionaryAlternativesSwitcher(Activity activity) {
		expressionTextSwitcher = (TextSwitcher) activity.findViewById(R.id.entry_expression);
		readingTextSwitcher = (TextSwitcher) activity.findViewById(R.id.entry_reading);

		nextExpressionButton = (ImageButton) activity.findViewById(R.id.next_expression);
		nextExpressionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentExpressionIndex++;
				updateExpressionText();
			}
		});

		previousExpressionButton = (ImageButton) activity.findViewById(R.id.previous_expression);
		previousExpressionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentExpressionIndex--;
				updateExpressionText();
			}
		});

		nextReadingButton = (ImageButton) activity.findViewById(R.id.next_reading);
		nextReadingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentReadingIndex++;
				updateReadingText();
			}
		});

		previousReadingButton = (ImageButton) activity.findViewById(R.id.previous_reading);
		previousReadingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentReadingIndex--;
				updateReadingText();
			}
		});
	}

	public void updateEntry(DictionaryEntry entry) {
		this.entry = entry;

		updateReadingViewsVisibility();

		currentExpressionIndex = 0;
		currentReadingIndex = 0;
		updateExpressionText();
	}

	private void updateReadingViewsVisibility() {
		int visibility;
		if (this.entry.getExpressions().isEmpty()) {
			visibility = View.GONE;
		} else {
			visibility = View.VISIBLE;
		}
		readingTextSwitcher.setVisibility(visibility);
		previousReadingButton.setVisibility(visibility);
		nextReadingButton.setVisibility(visibility);
	}

	private void updateExpressionText() {
		List<String> expressions = entry.getExpressions();
		if (expressions.isEmpty()) {
			expressions = entry.getReadings();
		}

		currentExpressionIndex = updateTextSwitcher(expressionTextSwitcher, previousExpressionButton, nextExpressionButton, expressions, currentExpressionIndex);

		currentReadingIndex = 0;
		updateReadingText();
	}

	private void updateReadingText() {
		if (!entry.getExpressions().isEmpty()) {
			List<String> readings = entry.getReadings(entry.getExpressions().get(currentExpressionIndex));
			currentReadingIndex = updateTextSwitcher(readingTextSwitcher, previousReadingButton, nextReadingButton, readings, currentReadingIndex);
		}
	}

	private static int updateTextSwitcher(TextSwitcher switcher, ImageButton previousButton, ImageButton nextButton, List<String> alternatives, int index) {
		if (index < 0) {
			return 0;
		} else if (index >= alternatives.size()) {
			return alternatives.size() - 1;
		}

		switcher.setText(alternatives.get(index));

		int visibility;
		if (alternatives.size() <= 1) {
			visibility = View.INVISIBLE;
		} else {
			visibility = View.VISIBLE;
		}
		previousButton.setVisibility(visibility);
		nextButton.setVisibility(visibility);

		if (index == 0) {
			previousButton.setImageResource(R.drawable.ic_btn_previous_disabled);
		} else {
			previousButton.setImageResource(R.drawable.ic_btn_previous);
		}

		if (index == alternatives.size() - 1) {
			nextButton.setImageResource(R.drawable.ic_btn_next_disabled);
		} else {
			nextButton.setImageResource(R.drawable.ic_btn_next);
		}

		return index;
	}
}
