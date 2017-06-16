package com.shael.shah.expensemanager;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class CircleAnimationTestActivity extends Activity {

    List<Circle> circleSegments;
    List<Expense> expenses;
    List<Category> categories;

    FrameLayout circleSegmentsFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_animation_test);

        circleSegmentsFrameLayout = (FrameLayout) findViewById(R.id.circleSegmentsFrameLayout);

        circleSegments = new ArrayList<>();
        expenses = Singleton.getInstance(null).getExpenses();
        categories = Singleton.getInstance(null).getCategories();

        Circle budgetSegment = new Circle(this, null, 90, 360, Color.BLACK, 50, 120, "");
        circleSegmentsFrameLayout.addView(budgetSegment);
        circleSegments.add(budgetSegment);

        float total = 0;
        for (Expense e : expenses)
            total += e.getAmount().floatValue();

        float prevAmount = 90;
        for (Category c : categories) {

            float catAmount = 0;
            for (Expense e : expenses) {
                if (e.getCategory().equals(c))
                    catAmount += e.getAmount().floatValue();
            }

            if (catAmount != 0) {
                Circle segment = new Circle(this, null, prevAmount, 360 * (catAmount / total), c.getColor(), 80, 80, c.getType());
                prevAmount += 360 * (catAmount / total);
                circleSegments.add(segment);
                circleSegmentsFrameLayout.addView(segment);
            }
        }
    }

    public void startAnimation(View view) {
        int startOffset = 0;
        AnimationSet animationSet = new AnimationSet(true);
        for (Circle c : circleSegments) {
            CircleAngleAnimation animation = new CircleAngleAnimation(c);
            float duration = c.getSweepAngle() / 360;
            animation.setDuration((long) (duration * 3000));
            animation.setStartOffset(startOffset);
            animationSet.addAnimation(animation);
            startOffset += (duration * 3000);
        }

        circleSegmentsFrameLayout.startAnimation(animationSet);
    }
}
