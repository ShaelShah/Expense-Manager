package com.shael.shah.expensemanager.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.animation.SegmentAnimation;
import com.shael.shah.expensemanager.model.Category;
import com.shael.shah.expensemanager.model.Expense;
import com.shael.shah.expensemanager.model.Segment;
import com.shael.shah.expensemanager.utils.DataSingleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SegmentsFragment extends Fragment {

    private static final String EXTRA_EXPENSE_DATE = "com.shael.shah.expensemanager.EXTRA_EXPENSE_DATE";

    private DataSingleton instance;
    private List<Expense> expenses;
    private List<Segment> segments;
    private FrameLayout segmentsFrameLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = DataSingleton.getInstance();
        expenses = instance.getExpenses();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Date date = (Date) getArguments().getSerializable(EXTRA_EXPENSE_DATE);
        View view = inflater.inflate(R.layout.fragment_segments, container, false);

        segmentsFrameLayout = view.findViewById(R.id.segmentsFrameLayout);
        createCircleView(date);

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

    private void createCircleView(Date date) {
        List<Expense> expensesToDisplay = new ArrayList<>();
        for (Expense e : expenses) {
            if (e.getDate().compareTo(date) >= 0)
                expensesToDisplay.add(e);
        }

        segmentsFrameLayout.removeAllViews();
        segments = new ArrayList<>();

        List<Category> categories = DataSingleton.getInstance().getCategories();

        Segment budgetSegment = new Segment(getActivity(), null, 90, 360, Color.BLACK, 100, 110);
        segments.add(budgetSegment);
        segmentsFrameLayout.addView(budgetSegment);

        float total = 0;
        for (Expense e : expensesToDisplay) {
            if (!e.isIncome())
                total += e.getAmount().floatValue();
        }

        float prevAmount = 90;
        for (Category c : categories) {

            float catAmount = 0;
            for (Expense e : expensesToDisplay) {
                if (e.getCategory() != null && e.getCategory().equals(c))
                    catAmount += e.getAmount().floatValue();
            }

            if (catAmount != 0) {
                //Segment segment = new Segment(getActivity(), null, prevAmount + 2, (360 - prevAmount) + 90 - 2, c.getColor(), 255, 80);
                Segment segment = new Segment(getActivity(), null, prevAmount + 2, (360 * (catAmount / total)) - 2, c.getColour(), 255, 80);
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
            SegmentAnimation animation = new SegmentAnimation(c);
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

    public void updateExpenses(Date dateRange) {
        createCircleView(dateRange);
    }
}
