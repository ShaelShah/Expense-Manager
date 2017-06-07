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

    private static final String EXTRA_CATEGORY_TITLE = "com.shael.shah.expensemanager.EXTRA_CATEGORY_TITLE";
    private static final String EXTRA_EXPENSE_TYPE = "com.shael.shah.expensemanager.EXTRA_EXPENSE_TYPE";
    private static final String EXTRA_EXPENSE_OBJECT = "com.shael.shah.expensemanager.EXTRA_EXPENSE_OBJECT";

    private List<Expense> expenses;

    private TextView categoryTitleTextView;
    private TextView amountCategoryTextView;
    private ScrollView categoryTitleScrollView;

    /*****************************************************************
     * Lifecycle Methods
     *****************************************************************/

    /*
     *  Initial method called by the system during activity startup.
     *  Responsible for getting a copy of all expenses.
     *  Also responsible for setting up of the initial GUI.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_category_expenses);

        //Find views to work with during this activity
        categoryTitleTextView = (TextView) findViewById(R.id.categoryTitleTextView);
        amountCategoryTextView = (TextView) findViewById(R.id.amountCategoryTextView);
        categoryTitleScrollView = (ScrollView) findViewById(R.id.categoryTitleScrollView);

        expenses = Singleton.getInstance(this).getExpenses();

        //Get extra from intent to determine which expenses to display
        Intent intent = getIntent();
        String categoryTitle = intent.getStringExtra(EXTRA_CATEGORY_TITLE);
        categoryTitleTextView.setText(categoryTitle);

        populateScrollView(categoryTitle);
    }

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

    /*
     *  Iterates through all expenses to check which expenses were requested to
     *  be displayed.
     */
    private void populateScrollView(String categoryTitle) {
        LinearLayout scrollLinearLayout = (LinearLayout) categoryTitleScrollView.findViewById(R.id.categoryScrollViewLinearLayout);

        //Create a temp List<Expense> of all expenses to be displayed
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

        //Inflate a category_expense_row_layout for each expense
        BigDecimal amount = new BigDecimal(0);
        for (Expense e : tempExpenses) {
            final Expense expense = e;
            //TODO: Figure out what this third parameter is for
            View item = View.inflate(this, R.layout.expenses_row_layout, null);

            TextView dateTextView = (TextView) item.findViewById(R.id.categoryDateTextView);
            TextView locationTextView = (TextView) item.findViewById(R.id.categoryLocationTextView);
            TextView amountTextView = (TextView) item.findViewById(R.id.categoryAmountTextView);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
            dateTextView.setText(sdf.format(e.getDate()));
            locationTextView.setText(e.getLocation());
            amountTextView.setText(getString(R.string.currency, e.getAmount()));
            amount = amount.add(e.getAmount());

            scrollLinearLayout.addView(item);

            //TODO: Is it okay if the expense in this onClickListener final?
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CategoryExpenses.this, AddExpenseActivity.class);
                    intent.putExtra(EXTRA_EXPENSE_OBJECT, expense);

                    if (expense.isRecurring()) {
                        if (expense.isIncome()) {
                            intent.putExtra(EXTRA_EXPENSE_TYPE, "Income");
                        } else {
                            intent.putExtra(EXTRA_EXPENSE_TYPE, "Recurring");
                        }
                    } else {
                        intent.putExtra(EXTRA_EXPENSE_TYPE, "Normal");
                    }

                    startActivity(intent);
                }
            });
        }

        amountCategoryTextView.setText(getString(R.string.currency, amount));
    }
}
