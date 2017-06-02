package com.shael.shah.expensemanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryExpenses extends Activity {

    /*****************************************************************
     * Private Variables
     *****************************************************************/

    private List<Expense> expenses;

    private TextView categoryTitleTextView;
    private TextView amountCategoryTextView;
    private ScrollView categoryTitleScrollView;

    /*****************************************************************
     * Lifecycle Methods
     *****************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_category_expenses);

        categoryTitleTextView = (TextView) findViewById(R.id.categoryTitleTextView);
        amountCategoryTextView = (TextView) findViewById(R.id.amountCategoryTextView);
        categoryTitleScrollView = (ScrollView) findViewById(R.id.categoryTitleScrollView);

        expenses = Singleton.getInstance(this).getExpenses();

        Intent intent = getIntent();
        String categoryTitle = intent.getStringExtra("CategoryTitle");
        categoryTitleTextView.setText(categoryTitle);

        populateScrollView(categoryTitle);
    }

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

    private void populateScrollView(String categoryTitle) {
        LinearLayout scrollLinearLayout = (LinearLayout) categoryTitleScrollView.findViewById(R.id.categoryScrollViewLinearLayout);

        List<Expense> tempExpenses = new ArrayList<>();
        switch (categoryTitle) {
            case "Net Total":
                tempExpenses = expenses;
                break;
            case "Income":
                for (Expense e : expenses) {
                    if (e.isIncome()) {
                        tempExpenses.add(e);
                    }
                }
                break;
            case "Expenses":
                for (Expense e : expenses) {
                    if (!e.isIncome()) {
                        tempExpenses.add(e);
                    }
                }
                break;
            default:
                for (Expense e : expenses) {
                    if (e.getCategory() != null) {
                        if (e.getCategory().getType().equals(categoryTitle)) {
                            tempExpenses.add(e);
                        }
                    }
                }
                break;
        }

        BigDecimal amount = new BigDecimal(0);
        for (Expense e : tempExpenses) {
            final Expense expense = e;
            //TODO: Figure out what this third parameter is for
            View item = View.inflate(this, R.layout.category_expenses_row_layout, null);

            TextView dateTextView = (TextView) item.findViewById(R.id.categoryDateTextView);
            TextView locationTextView = (TextView) item.findViewById(R.id.categoryLocationTextView);
            TextView amountTextView = (TextView) item.findViewById(R.id.categoryAmountTextView);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
            dateTextView.setText(sdf.format(e.getDate()));
            locationTextView.setText(e.getLocation());
            //TODO: Don't concatenate in setText
            amountTextView.setText("$" + e.getAmount());
            amount = amount.add(e.getAmount());

            scrollLinearLayout.addView(item);

            //TODO: Is it okay if the expense in this onClickListener final?
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CategoryExpenses.this, AddExpenseActivity.class);
                    intent.putExtra("ExpenseObject", expense);
                    startActivity(intent);
                }
            });
        }

        //TODO: Don't concatenate in setText
        amountCategoryTextView.setText("$" + amount);
    }
}
