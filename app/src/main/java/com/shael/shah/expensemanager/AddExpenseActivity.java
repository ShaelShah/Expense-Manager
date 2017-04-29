package com.shael.shah.expensemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddExpenseActivity extends Activity {

    private TextView amountTextview;
    private ScrollView categoryScrollView;

    private List<Category> categories;
    private List<RadioButton> categoryRadioButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        amountTextview = (TextView) findViewById(R.id.amountTextView);
        categoryScrollView = (ScrollView) findViewById(R.id.categoryScrollView);

        categories = Singleton.getInstance(this).getCategories();
        categoryRadioButtons = new ArrayList<>();

        createCategoryRows();
    }

    private void createCategoryRows() {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout scrollLinearLayout = (LinearLayout) categoryScrollView.findViewById(R.id.scrollLinearLayout);

        int i = 0;
        for (Category c : categories) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setId(i++);

            //View item = layoutInflater.inflate(R.layout.category_row_layout, null, false);
            View item = View.inflate(this, R.layout.category_row_layout, null);

            View colorBox = item.findViewById(R.id.colorView);
            colorBox.setBackgroundColor(Color.RED);

            TextView categoryNameTextView = (TextView) item.findViewById(R.id.categoryNameTextView);
            categoryNameTextView.setText(c.getType());

            RadioButton categoryRadioButton = (RadioButton) item.findViewById(R.id.categoryRadioButton);
            categoryRadioButtons.add(categoryRadioButton);
            categoryRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (RadioButton rb : categoryRadioButtons) {
                        if (rb != v)
                            rb.setChecked(false);
                    }
                }
            });

            linearLayout.addView(item);
            scrollLinearLayout.addView(linearLayout);
        }
    }

    public void goBackToHomeScreen(View view) {
        Date date = new Date();
        Category category = new Category("Sports");
        Expense expense = new Expense(date, 23.45, category, "ACC", "", false, false);

        Singleton.getInstance(this).addExpense(expense);
        Singleton.getInstance(this).addCategory("Sports");

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
