package com.shael.shah.expensemanager.animation;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.shael.shah.expensemanager.model.Segment;

public class SegmentAnimation extends Animation {

    private Segment segment;

    private float oldAngle;
    private float newAngle;

    public SegmentAnimation(Segment segment) {
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
