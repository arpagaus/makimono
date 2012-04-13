package net.makimono.view;

import java.util.Collections;
import java.util.List;

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
import android.view.View;

import com.larvalabs.svgandroid.SVGParser;

public class KanjiWritingView extends View {

	private static final int VIRTUAL_AXIS_LENGTH = 109;
	private static final float VIRTUAL_AXIS_HALF_LENGTH = VIRTUAL_AXIS_LENGTH / 2f;

	private List<String> strokePaths = Collections.emptyList();
	private int strokeIndex;

	private Paint kanjiStrokesPaint;
	private Paint kanjiStrokeStartPaint;
	private Paint gridFramePaint;
	private Paint gridLinePaint;

	public KanjiWritingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public KanjiWritingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public KanjiWritingView(Context context) {
		super(context);
	}

	public void setStrokePaths(List<String> strokePaths) {
		strokeIndex = 0;
		this.strokePaths = strokePaths;
	}

	public int getStrokeIndex() {
		return strokeIndex;
	}

	public void setStrokeIndex(int strokeIndex) {
		int size = strokePaths.size();
		if (size == 0) {
			this.strokeIndex = 0;
		} else {
			this.strokeIndex = strokeIndex % size;
		}
	}

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

		canvas.drawLine(0, VIRTUAL_AXIS_HALF_LENGTH, VIRTUAL_AXIS_LENGTH, VIRTUAL_AXIS_HALF_LENGTH, getGridLinePaint());
		canvas.drawLine(VIRTUAL_AXIS_HALF_LENGTH, 0, VIRTUAL_AXIS_HALF_LENGTH, VIRTUAL_AXIS_LENGTH, getGridLinePaint());

		for (int i = 0; (i <= strokeIndex && i < strokePaths.size()); i++) {
			Path path = SVGParser.parsePath(strokePaths.get(i));
			canvas.drawPath(path, getKanjiStrokesPaint());

			if (i == strokeIndex) {
				PathMeasure pm = new PathMeasure(path, false);
				float coordinates[] = { 0f, 0f };
				pm.getPosTan(0, coordinates, null);
				canvas.drawPoint(coordinates[0], coordinates[1], getKanjiStrokeStartPaint());
			}
		}
	}
}
