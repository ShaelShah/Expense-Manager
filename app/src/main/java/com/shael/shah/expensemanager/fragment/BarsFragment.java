package com.shael.shah.expensemanager.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.activity.DisplayExpensesActivity;
import com.shael.shah.expensemanager.model.Category;
import com.shael.shah.expensemanager.model.Expense;
import com.shael.shah.expensemanager.utils.DataSingleton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BarsFragment extends Fragment {

    private static final String EXTRA_EXPENSE_LIST = "com.shael.shah.expensemanager.EXTRA_EXPENSE_LIST";
    private static final String EXTRA_EXPENSES_DISPLAY = "com.shael.shah.expensemanager.EXTRA_EXPENSES_DISPLAY";
    private static final String EXTRA_EXPENSES_TITLE = "com.shael.shah.expensemanager.EXTRA_EXPENSES_TITLE";

    private List<Expense> expenses;

    private ScrollView mainCategoryScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //expenses = getArguments().getParcelableArrayList(EXTRA_EXPENSE_LIST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_bars, container, false);

        mainCategoryScrollView = view.findViewById(R.id.mainCategoryScrollView);
        populateMainCategoryRows();

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

    /*
     *  Iterates through all of the expenses passed in through the List<Expense> parameter
     *  to provide a list of all expenses sorted by category.
     *
     *  Inflates a category_select_row_layout for each category.
     *  Initially removes all child views from the parent.
     */
    private void populateMainCategoryRows() {
        List<Category> categories = DataSingleton.getInstance().getCategories();
        //TODO: This function can be better optimized, instead of looping through all expenses for each category, loop through the expenses once and assign temporary lists for each category
        LinearLayout scrollLinearLayout = mainCategoryScrollView.findViewById(R.id.mainScrollLinearLayout);

        if (scrollLinearLayout.getChildCount() > 0) {
            scrollLinearLayout.removeAllViews();
        }

        for (final Category c : categories) {

            String title = c.getType();
            BigDecimal amount = new BigDecimal(0);

            for (Expense e : expenses) {
                if (!e.isDelete() && !e.isIncome() && e.getCategory().getType().equals(title)) {
                    amount = amount.add(e.getAmount());
                }
            }

            if (amount.signum() > 0) {
                //TODO: Look into View.inflate method (specifically the 3rd parameter)
                View item = View.inflate(getActivity(), R.layout.category_display_row_layout, null);

                View colorBox = item.findViewById(R.id.mainColorView);
                colorBox.setBackgroundColor(c.getColor());

                TextView categoryRowTitle = item.findViewById(R.id.categoryRowTitle);
                categoryRowTitle.setText(title);
                categoryRowTitle.setTextColor(c.getColor());

                TextView categoryRowAmount = item.findViewById(R.id.categoryRowAmount);
                categoryRowAmount.setTextColor(c.getColor());

                categoryRowAmount.setText(getString(R.string.currency, amount));
                scrollLinearLayout.addView(item);

                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String categoryTitle = ((TextView) v.findViewById(R.id.categoryRowTitle)).getText().toString();

                        //TODO: ArrayList used instead of List due to List not being instantiable
                        ArrayList<Expense> tempExpenses = new ArrayList<>();
                        for (Expense e : expenses) {
                            if (e.getCategory().getType().equals(categoryTitle))
                                tempExpenses.add(e);
                        }

                        Intent intent = new Intent(getActivity(), DisplayExpensesActivity.class);
                        intent.putExtra(EXTRA_EXPENSES_DISPLAY, tempExpenses);
                        intent.putExtra(EXTRA_EXPENSES_TITLE, c.getType());
                        startActivity(intent);
                    }
                });
            }
        }

        startAnimations();
    }


    /*
     *  Finds the color bar for each category and performs an animation starting from 0
     *  horizontally and ending at a percentage of how much that category makes up the
     *  net total spent, all scaled to the highest contributor set to 100%.
     */
    private void startAnimations() {
        LinearLayout scrollLinearLayout = mainCategoryScrollView.findViewById(R.id.mainScrollLinearLayout);

        List<View> colorBoxViews = new ArrayList<>();
        List<Float> ratioFloats = new ArrayList<>();

        if (scrollLinearLayout != null) {
            BigDecimal total = new BigDecimal(0);
            for (Expense e : expenses)
                total = total.add(e.getAmount());

            LinearLayout displayCategoryLinearLayout;
            //TODO: Can some of this code be refractored?
            for (int i = 0; i < scrollLinearLayout.getChildCount(); i++) {
                displayCategoryLinearLayout = (LinearLayout) scrollLinearLayout.getChildAt(i);
                View colorBox = displayCategoryLinearLayout.getChildAt(0);
                LinearLayout displayCategoryInformationLinearLayout = (LinearLayout) displayCategoryLinearLayout.getChildAt(1);
                TextView categoryRowAmount = (TextView) displayCategoryInformationLinearLayout.getChildAt(1);

                float amount = Float.parseFloat(categoryRowAmount.getText().toString().replaceAll("[^\\d.]", ""));
                float net = total.floatValue();

                colorBoxViews.add(colorBox);
                ratioFloats.add(amount / net);
            }

            if (!colorBoxViews.isEmpty() && !ratioFloats.isEmpty()) {
                if (colorBoxViews.size() == ratioFloats.size()) {
                    float max = Collections.max(ratioFloats);

                    for (int i = 0; i < colorBoxViews.size(); i++) {
                        scaleView(colorBoxViews.get(i), ratioFloats.get(i) / max);
                    }
                }
            }
        }
    }

    /*
     *  Horizontal expansion of a view.
     */
    private void scaleView(View v, float endScale) {
        Animation anim = new ScaleAnimation(0f, endScale, 1f, 1f);
        anim.setFillAfter(true);
        anim.setDuration(2000);
        v.startAnimation(anim);
    }

    public void updateExpenses(Date dateRange) {
        //this.expenses = expenses;
        populateMainCategoryRows();
    }
}
