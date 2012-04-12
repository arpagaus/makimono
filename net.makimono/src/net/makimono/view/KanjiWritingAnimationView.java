package net.makimono.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

public class KanjiWritingAnimationView extends KanjiWritingView {
	private static final String LOG_TAG = KanjiWritingAnimationView.class.getName();

	private static final int TIMER_PERIOD = 1400;

	private Timer timer;

	public KanjiWritingAnimationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public KanjiWritingAnimationView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public KanjiWritingAnimationView(Context context) {
		super(context);
	}

	@Override
	protected void onAttachedToWindow() {
		Log.v(LOG_TAG, "onAttachedToWindow");
		super.onAttachedToWindow();

		if (timer == null) {
			timer = new Timer("Kanji animation", true);
			timer.scheduleAtFixedRate(new IncrementAndRedrawTask(), TIMER_PERIOD, TIMER_PERIOD);
		} else {
			Log.e(LOG_TAG, "The timer was not null");
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		Log.v(LOG_TAG, "onDetachedFromWindow");
		super.onDetachedFromWindow();

		timer.cancel();
		timer = null;
	}

	private class IncrementAndRedrawTask extends TimerTask {
		@Override
		public void run() {
			postInvalidate();
			strokeIndex = (strokeIndex + 1) % paths.length;
		}
	}
}
