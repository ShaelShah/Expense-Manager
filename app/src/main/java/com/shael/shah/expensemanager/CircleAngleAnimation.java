package com.shael.shah.expensemanager;

import android.view.animation.Animation;
import android.view.animation.Transformation;

class CircleAngleAnimation extends Animation {

    private Segment segment;

    private float oldAngle;
    private float newAngle;

    CircleAngleAnimation(Segment segment) {
        this.oldAngle = segment.getCurrentAngle();
        this.newAngle = segment.getSweepAngle();
        this.segment = segment;
    }

    @Override
    protected void applyTransformation(float linearTime, Transformation transformation) {
        float angle = oldAngle + ((newAngle - oldAngle) * linearTime);

        segment.setCurrentAngle(angle);
        segment.requestLayout();
    }
}
