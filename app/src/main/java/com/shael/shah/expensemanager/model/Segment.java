package com.shael.shah.expensemanager.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class Segment extends View
{

    private RectF rect;
    private Paint paint;

    private float startAngle;
    private float currentAngle;
    private float sweepAngle;

    public Segment(Context context)
    {
        super(context);
    }

    public Segment(Context context, AttributeSet attrs, float startAngle, float sweepAngle, int color, int alpha, int strokeWidth)
    {
        super(context, attrs);

        //Create RectF
        rect = new RectF(0, 0, 0, 0);

        //Create Paint and initialize
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        paint.setAlpha(alpha);

        //Setup member values
        this.startAngle = startAngle;
        this.sweepAngle = sweepAngle;
        this.currentAngle = 0;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawArc(rect, startAngle, currentAngle, false, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(width, height);
        float highStroke = 200;

        rect.set(0 + highStroke / 2, 0 + highStroke / 2, min - highStroke / 2, min - highStroke / 2);
    }

    public float getCurrentAngle()
    {
        return currentAngle;
    }

    public void setCurrentAngle(float currentAngle)
    {
        this.currentAngle = currentAngle;
    }

    public float getSweepAngle()
    {
        return sweepAngle;
    }
}
