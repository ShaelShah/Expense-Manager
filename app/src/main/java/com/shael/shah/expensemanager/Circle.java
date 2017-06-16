package com.shael.shah.expensemanager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class Circle extends View {

    private Path path;
    private RectF rect;
    private Paint paint;
    private Paint textPaint;

    private float startAngle;
    private float currentAngle;
    private float sweepAngle;

    private String category;

    public Circle(Context context, AttributeSet attrs, float startAngle, float sweepAngle, int color, int alpha, int strokeWidth, String category) {
        super(context, attrs);

        //Create RectF
        rect = new RectF(0, 0, 0, 0);

        //Create Path
        path = new Path();
        path.addArc(rect, startAngle, sweepAngle);

        //Create Paint and initialize
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        paint.setAlpha(alpha);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(16f);

        //Create TextPaint and initialize
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(color);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(24f);

        //Setup member values
        this.startAngle = startAngle;
        this.sweepAngle = sweepAngle;
        this.currentAngle = 0;
        this.category = category;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rect, startAngle, currentAngle, false, paint);
        canvas.drawTextOnPath(category, path, 0, 20, textPaint);
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(width, height);
        float highStroke = 200;

        rect.set(0 + highStroke / 2, 0 + highStroke / 2, min - highStroke / 2, min - highStroke / 2);
    }

    public float getCurrentAngle() {
        return currentAngle;
    }

    public void setCurrentAngle(float currentAngle) {
        this.currentAngle = currentAngle;
    }

    public float getSweepAngle() {
        return sweepAngle;
    }
}
