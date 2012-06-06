package net.makimono.util;

import java.util.List;

import net.makimono.R;
import net.makimono.model.DictionaryEntry;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextSwitcher;

public class DictionaryAlternativesSwitcher {

	private TextSwitcher expressionTextSwitcher;
	private TextSwitcher readingTextSwitcher;

	private View nextExpressionButton;
	private View previousExpressionButton;
	private View nextReadingButton;
	private View previousReadingButton;

	private DictionaryEntry entry;

	private int currentExpressionIndex;
	private int currentReadingIndex;

	public DictionaryAlternativesSwitcher(Activity activity) {
		expressionTextSwitcher = (TextSwitcher) activity.findViewById(R.id.entry_expression);
		readingTextSwitcher = (TextSwitcher) activity.findViewById(R.id.entry_reading);

		nextExpressionButton = activity.findViewById(R.id.next_expression);
		nextExpressionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentExpressionIndex++;
				updateExpressionText();
			}
		});

		previousExpressionButton = activity.findViewById(R.id.previous_expression);
		previousExpressionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentExpressionIndex--;
				updateExpressionText();
			}
		});

		nextReadingButton = activity.findViewById(R.id.next_reading);
		nextReadingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentReadingIndex++;
				updateReadingText();
			}
		});

		previousReadingButton = activity.findViewById(R.id.previous_reading);
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

		updateTextSwitcher(expressionTextSwitcher, previousExpressionButton, nextExpressionButton, expressions, currentExpressionIndex);

		currentReadingIndex = 0;
		updateReadingText();
	}

	private void updateReadingText() {
		if (!entry.getExpressions().isEmpty()) {
			List<String> readings = entry.getReadings(entry.getExpressions().get(currentExpressionIndex));
			updateTextSwitcher(readingTextSwitcher, previousReadingButton, nextReadingButton, readings, currentReadingIndex);
		}
	}

	private void updateTextSwitcher(TextSwitcher switcher, View previousButton, View nextButton, List<String> alternatives, int index) {
		if (index < alternatives.size()) {
			switcher.setText(alternatives.get(index % alternatives.size()));
		}

		if (index == 0) {
			previousButton.setVisibility(View.INVISIBLE);
		} else {
			previousButton.setVisibility(View.VISIBLE);
		}

		if (index == alternatives.size() - 1) {
			nextButton.setVisibility(View.INVISIBLE);
		} else {
			nextButton.setVisibility(View.VISIBLE);
		}
	}

}