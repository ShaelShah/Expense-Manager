package com.shael.shah.expensemanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CategoryExpenses extends Activity {

    private List<Expense> expenses;

    private TextView categoryTitleTextView;
    private TextView amountCategoryTextView;
    private ScrollView categoryTitleScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_category_expenses);

        Intent intent = getIntent();
        String categoryTitle = intent.getStringExtra("CategoryTitle");

        expenses = Singleton.getInstance(this).getExpenses();

        categoryTitleTextView = (TextView) findViewById(R.id.categoryTitleTextView);
        amountCategoryTextView = (TextView) findViewById(R.id.amountCategoryTextView);
        categoryTitleScrollView = (ScrollView) findViewById(R.id.categoryTitleScrollView);

        categoryTitleTextView.setText(categoryTitle);

        Log.d("Category Title Prior", categoryTitle);

        BigDecimal amount = new BigDecimal(0);
        for (Expense e : expenses) {
            Log.d("Expense Category Title", e.getCategory().getType());
            if (e.getCategory().getType().equals(categoryTitle)) {
                Log.d("Expense Amount", e.getAmount().toString());
                amount = amount.add(e.getAmount());
            }
        }
        Log.d("Amount", amount.toString());
        amountCategoryTextView.setText("$" + amount);

        populateScrollView(categoryTitle);
    }

    private void populateScrollView(String categoryTitle) {
        LinearLayout scrollLinearLayout = (LinearLayout) categoryTitleScrollView.findViewById(R.id.categoryScrollViewLinearLayout);

        for (Expense e : expenses) {
            if (e.getCategory().getType().equals(categoryTitle)) {
                final Expense expense = e;
                View item = View.inflate(this, R.layout.category_expenses_row_layout, null);

                TextView dateTextView = (TextView) item.findViewById(R.id.categoryDateTextView);
                TextView locationTextView = (TextView) item.findViewById(R.id.categoryLocationTextView);
                TextView amountTextView = (TextView) item.findViewById(R.id.categoryAmountTextView);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
                dateTextView.setText(sdf.format(e.getDate()));
                locationTextView.setText(e.getLocation());
                amountTextView.setText("$" + e.getAmount());

                scrollLinearLayout.addView(item);

                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CategoryExpenses.this, AddExpenseActivity.class);
                        intent.putExtra("ExpenseObject", expense);
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
