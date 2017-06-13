package com.shael.shah.expensemanager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Circle extends View {

    private static final int START_ANGLE_POINT = 90;
    private static final int STROKE_WIDTH = 30;

    private final List<Paint> paint;
    private final List<RectF> rect;
    private final List<Float> amount;
    private float total = 0;

    private float angle;

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new ArrayList<>();
        rect = new ArrayList<>();
        amount = new ArrayList<>();

        List<Expense> expenses = Singleton.getInstance(null).getExpenses();
        List<Category> categories = Singleton.getInstance(null).getCategories();

        Log.d("TEST - Categories ", categories.toString());

        for (Category c : categories) {
            Paint catPaint = new Paint();
            catPaint.setAntiAlias(true);
            catPaint.setStyle(Paint.Style.STROKE);
            catPaint.setStrokeWidth(STROKE_WIDTH);
            catPaint.setColor(c.getColor());
            paint.add(catPaint);

            RectF catRect = new RectF(0, 0, 0, 0);
            rect.add(catRect);

            float catAmount = 0;
            for (Expense e : expenses) {
                if (e.getCategory().getType().equals(c.getType())) {
                    catAmount += e.getAmount().doubleValue();
                }
            }
            amount.add(catAmount);
            total += catAmount;

            Log.d("TEST - Category", "Category " + c.getType());
            Log.d("TEST - Amount", "Amount " + catAmount);
            Log.d("TEST - Total", "Total " + total);
        }

        angle = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(rect.get(0), START_ANGLE_POINT, angle * (amount.get(0) / total), false, paint.get(0));
        for (int i = 1; i < rect.size() - 1; i++) {
            float startingAngle = START_ANGLE_POINT + angle * (amount.get(i - 1) / total);
            Log.d("ANGLE", "Starting Angle: " + startingAngle);
            canvas.drawArc(rect.get(i), startingAngle, angle * (amount.get(i) / total), false, paint.get(i));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        float highStroke = 100;

        for (RectF r : rect)
            r.set(0 + highStroke / 2, 0 + highStroke / 2, min - highStroke / 2, min - highStroke / 2);
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
