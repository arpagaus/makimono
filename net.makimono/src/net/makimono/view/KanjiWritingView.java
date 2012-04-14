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

	boolean showStrokeIndex = true;

	private Paint kanjiStrokesPaint;
	private Paint kanjiLastStrokePaint;
	private Paint kanjiStrokeStartPaint;
	private Paint gridFramePaint;
	private Paint gridLinePaint;
	private Paint strokeIndexPaint;

	public KanjiWritingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public KanjiWritingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public KanjiWritingView(Context context) {
		super(context);
	}

	List<String> getStrokePaths() {
		return strokePaths;
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
			kanjiStrokesPaint.setAntiAlias(true);
			kanjiStrokesPaint.setColor(Color.GRAY);
			kanjiStrokesPaint.setStyle(Paint.Style.STROKE);
			kanjiStrokesPaint.setStrokeJoin(Paint.Join.ROUND);
			kanjiStrokesPaint.setStrokeCap(Paint.Cap.ROUND);
			kanjiStrokesPaint.setStrokeWidth(6);
		}
		return kanjiStrokesPaint;
	}

	public Paint getKanjiLastStrokePaint() {
		if (kanjiLastStrokePaint == null) {
			kanjiLastStrokePaint = new Paint(getKanjiStrokesPaint());
			kanjiLastStrokePaint.setColor(Color.BLACK);
		}
		return kanjiLastStrokePaint;
	}

	public Paint getKanjiStrokeStartPaint() {
		if (kanjiStrokeStartPaint == null) {
			kanjiStrokeStartPaint = new Paint(getKanjiStrokesPaint());
			kanjiStrokeStartPaint.setColor(Color.RED);
			kanjiStrokeStartPaint.setStyle(Style.FILL);
			kanjiStrokeStartPaint.setStrokeWidth(8);
		}
		return kanjiStrokeStartPaint;
	}

	public Paint getGridFramePaint() {
		if (gridFramePaint == null) {
			gridFramePaint = new Paint();
			gridFramePaint.setColor(Color.LTGRAY);
			gridFramePaint.setStrokeWidth(4);
			gridFramePaint.setStyle(Paint.Style.STROKE);
		}
		return gridFramePaint;
	}

	public Paint getGridLinePaint() {
		if (gridLinePaint == null) {
			gridLinePaint = new Paint(getGridFramePaint());
			gridLinePaint.setPathEffect(new DashPathEffect(new float[] { 10, 10 }, 1));
		}
		return gridLinePaint;
	}

	public Paint getStrokeIndexPaint() {
		if (strokeIndexPaint == null) {
			strokeIndexPaint = new Paint();
			strokeIndexPaint.setAntiAlias(true);
			strokeIndexPaint.setColor(Color.BLACK);
			strokeIndexPaint.setTextSize(10);
		}

		return strokeIndexPaint;
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
				canvas.drawPath(path, getKanjiLastStrokePaint());

				PathMeasure pm = new PathMeasure(path, false);
				float coordinates[] = { 0f, 0f };
				pm.getPosTan(0, coordinates, null);
				canvas.drawPoint(coordinates[0], coordinates[1], getKanjiStrokeStartPaint());
			}

		}

		if (showStrokeIndex) {
			canvas.drawText(String.valueOf(strokeIndex + 1), 8, 15, getStrokeIndexPaint());
		}
	}
}
