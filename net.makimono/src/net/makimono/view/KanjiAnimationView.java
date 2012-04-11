package net.makimono.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.larvalabs.svgandroid.SVGParser;

public class KanjiAnimationView extends View {
	private static final String LOG_TAG = KanjiAnimationView.class.getName();

	private static final int TIMER_PERIOD = 1500;
	private static final int VIRTUAL_AXIS_LENGTH = 109;

	private Timer timer;
	private int currentStroke;

	private Paint kanjiStrokesPaint;
	private Paint kanjiStrokeStartPaint;
	private Paint gridFramePaint;
	private Paint gridLinePaint;

	public KanjiAnimationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public KanjiAnimationView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public KanjiAnimationView(Context context) {
		super(context);
	}

	String[] paths = new String[] { "M44.87,11.18c0.21,1.14-0.26,1.78-1.06,2.55c-6.79,6.53-16.5,14.26-28.29,18.71", "M19.8,16.22c8.45,2.53,17.95,8.03,24.42,15.53",
			"M31.73,31.67c0.06,0.85-0.11,2.27-0.51,3.43c-3.07,8.89-8.7,21.14-20.78,32.8", "M11.36,43.66c1.16,0.47,3.28,0.54,4.45,0.47c10.11-0.63,19.75-2.01,33.08-2.44c1.93-0.06,3.1,0.23,4.06,0.46",
			"M19.52,59.1c0.3,0.3,0.95,2.21,1.01,2.8s1.96,23.75,1.9,27.44",
			"M20.94,60.79c1.9-0.21,24.65-2.82,26.07-2.95c1.67-0.15,3.81,0.91,3.69,3.45c-0.45,9.21-0.7,12.96-2.03,19.15c-1.9,8.87-5.12,2.55-5.75,1.51",
			"M34.56,49.5c0.3,0.35,0.95,2.6,1.01,3.29c0.06,0.69-0.04,42.38-0.1,46.71", "M67.25,13.5c0.03,0.86,0.07,2.22-0.07,3.45C66.32,24.24,63,40.25,54.64,50",
			"M63.22,37.49c0.91,0.36,2.64,0.93,4.01,0.61c1.67-0.39,19.68-4.65,21.26-4.85c8-1-1.33,10.59-3,12.5", "M72.6,44c0.07,1.25,0.16,3.21-0.14,5.02C69.62,66.48,64.15,84.51,48.5,97",
			"M70.92,59c3.45,8.53,15.92,28.6,21.59,35.65c1.2,1.49,2.1,2.37,3.24,2.85" };

	public Paint getKanjiStrokesPaint() {
		if (kanjiStrokesPaint == null) {
			kanjiStrokesPaint = new Paint();
			kanjiStrokesPaint.setDither(true);
			kanjiStrokesPaint.setAntiAlias(true);
			kanjiStrokesPaint.setColor(Color.BLACK);
			kanjiStrokesPaint.setStyle(Paint.Style.STROKE);
			kanjiStrokesPaint.setStrokeJoin(Paint.Join.ROUND);
			kanjiStrokesPaint.setStrokeCap(Paint.Cap.ROUND);
			kanjiStrokesPaint.setStrokeWidth(6);
		}
		return kanjiStrokesPaint;
	}

	public Paint getKanjiStrokeStartPaint() {
		if (kanjiStrokeStartPaint == null) {
			kanjiStrokeStartPaint = new Paint(getKanjiStrokesPaint());
			kanjiStrokeStartPaint.setColor(Color.RED);
			kanjiStrokeStartPaint.setStyle(Style.FILL);
			kanjiStrokeStartPaint.setStrokeWidth(10);
		}
		return kanjiStrokeStartPaint;
	}

	public Paint getGridFramePaint() {
		if (gridFramePaint == null) {
			gridFramePaint = new Paint(getKanjiStrokesPaint());
			gridFramePaint.setColor(Color.LTGRAY);
			gridFramePaint.setStrokeWidth(4);
			gridFramePaint.setStyle(Paint.Style.STROKE);
		}
		return gridFramePaint;
	}

	public Paint getGridLinePaint() {
		if (gridLinePaint == null) {
			gridLinePaint = new Paint(getGridFramePaint());
			gridLinePaint.setPathEffect(new DashPathEffect(new float[] { 10, 4 }, 1));
		}
		return gridLinePaint;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		final int currentAxisLength = Math.min(getWidth(), getHeight());
		canvas.scale(currentAxisLength / (float) VIRTUAL_AXIS_LENGTH, currentAxisLength / (float) VIRTUAL_AXIS_LENGTH);

		canvas.drawRect(new Rect(2, 2, VIRTUAL_AXIS_LENGTH - 2, VIRTUAL_AXIS_LENGTH - 2), getGridFramePaint());
		canvas.drawLine(0, VIRTUAL_AXIS_LENGTH / 2f, VIRTUAL_AXIS_LENGTH, VIRTUAL_AXIS_LENGTH / 2f, getGridLinePaint());
		canvas.drawLine(VIRTUAL_AXIS_LENGTH / 2f, 0, VIRTUAL_AXIS_LENGTH / 2f, VIRTUAL_AXIS_LENGTH, getGridLinePaint());

		for (int i = 0; i <= currentStroke; i++) {
			Path path = SVGParser.parsePath(paths[i]);
			canvas.drawPath(path, getKanjiStrokesPaint());

			if (i == currentStroke) {
				PathMeasure pm = new PathMeasure(path, false);
				float coordinates[] = { 0f, 0f };
				pm.getPosTan(0, coordinates, null);
				canvas.drawPoint(coordinates[0], coordinates[1], getKanjiStrokeStartPaint());
			}
		}
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
			currentStroke = (currentStroke + 1) % paths.length;
		}
	}
}
