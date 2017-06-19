package com.shael.shah.expensemanager;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class SegmentsFragment extends Fragment {

    private static final String EXTRA_EXPENSE_LIST = "com.shael.shah.expensemanager.EXTRA_EXPENSE_LIST";

    private List<Segment> segments;
    private FrameLayout segmentsFrameLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Expense> expenses = getArguments().getParcelableArrayList(EXTRA_EXPENSE_LIST);
        View view = inflater.inflate(R.layout.fragment_segments, container, false);

        segmentsFrameLayout = (FrameLayout) view.findViewById(R.id.segmentsFrameLayout);
        createCircleView(expenses);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        startAnimations();
    }

    private void createCircleView(List<Expense> expenses) {

        segmentsFrameLayout.removeAllViews();
        segments = new ArrayList<>();

        List<Category> categories = Singleton.getInstance(null).getCategories();

        Segment budgetSegment = new Segment(getActivity(), null, 90, 360, Color.BLACK, 100, 100);
        segments.add(budgetSegment);
        segmentsFrameLayout.addView(budgetSegment);

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
                Segment segment = new Segment(getActivity(), null, prevAmount, 360 * (catAmount / total), c.getColor(), 255, 80);
                prevAmount += 360 * (catAmount / total);

                segments.add(segment);
                segmentsFrameLayout.addView(segment);
            }
        }

        startAnimations();
    }

    private void startAnimations() {
        int startOffset = 0;
        AnimationSet animationSet = new AnimationSet(true);
        for (Segment c : segments) {
            CircleAngleAnimation animation = new CircleAngleAnimation(c);
            float duration = c.getSweepAngle() / 360;
            animation.setDuration((long) (duration * 1500));
            animation.setStartOffset(startOffset);
            animationSet.addAnimation(animation);
            startOffset += (duration * 1500);
        }

        segmentsFrameLayout.startAnimation(animationSet);
    }

    //public void updateExpenses(Bundle bundle) {
    //    List<Expense> expenses = bundle.getParcelableArrayList(EXTRA_EXPENSE_LIST);
    //    createCircleView(expenses);
    //}

    public void updateExpenses(List<Expense> expenses) {
        createCircleView(expenses);
    }
}
