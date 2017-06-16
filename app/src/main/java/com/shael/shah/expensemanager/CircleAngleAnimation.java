package com.shael.shah.expensemanager;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CircleAngleAnimation extends Animation {

    private Circle circle;

    private float oldAngle;
    private float newAngle;

    public CircleAngleAnimation(Circle circle) {
        this.oldAngle = circle.getCurrentAngle();
        this.newAngle = circle.getSweepAngle();
        this.circle = circle;
    }

    @Override
    protected void applyTransformation(float linearTime, Transformation transformation) {
        float angle = oldAngle + ((newAngle - oldAngle) * linearTime);

        circle.setCurrentAngle(angle);
        circle.requestLayout();
    }
}
