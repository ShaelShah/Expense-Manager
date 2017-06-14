package com.shael.shah.expensemanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class CircleAnimationTestActivity extends Activity {

    Circle circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_animation_test);

        circle = (Circle) findViewById(R.id.circle);
    }

    public void startAnimation(View view) {
        CircleAngleAnimation animation = new CircleAngleAnimation(circle, 360);
        animation.setDuration(3000);
        circle.startAnimation(animation);
    }
}
