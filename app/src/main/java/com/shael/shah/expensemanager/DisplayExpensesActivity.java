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

public class DisplayExpensesActivity extends Activity {

    /*****************************************************************
     * Private Variables
     *****************************************************************/

    private static final String EXTRA_EXPENSES_DISPLAY = "com.shael.shah.expensemanager.EXTRA_EXPENSES_DISPLAY";
    private static final String EXTRA_EXPENSES_TITLE = "com.shael.shah.expensemanager.EXTRA_EXPENSES_TITLE";
    private static final String EXTRA_EXPENSE_TYPE = "com.shael.shah.expensemanager.EXTRA_EXPENSE_TYPE";
    private static final String EXTRA_EXPENSE_OBJECT = "com.shael.shah.expensemanager.EXTRA_EXPENSE_OBJECT";

    private List<Expense> expenses;

    private TextView expensesTitleTextView;
    private TextView amountExpensesTextView;
    private ScrollView expensesTitleScrollView;

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
        setContentView(R.layout.activity_show_expenses);

        //Find views to work with during this activity
        expensesTitleTextView = (TextView) findViewById(R.id.expensesTitleTextView);
        amountExpensesTextView = (TextView) findViewById(R.id.amountExpensesTextView);
        expensesTitleScrollView = (ScrollView) findViewById(R.id.expensesTitleScrollView);

        //expenses = Singleton.getInstance(this).getExpenses();
        populateScrollView();
    }

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

    /*
     *  Iterates through all expenses to check which expenses were requested to
     *  be displayed.
     */
    private void populateScrollView() {
        //Get extra from intent to determine which expenses to display
        Intent intent = getIntent();
        //TODO: Check this warning
        ArrayList<Expense> expensesToDisplay = (ArrayList<Expense>) intent.getSerializableExtra(EXTRA_EXPENSES_DISPLAY);
        String title = intent.getStringExtra(EXTRA_EXPENSES_TITLE);
        expensesTitleTextView.setText(title);

        LinearLayout scrollLinearLayout = (LinearLayout) expensesTitleScrollView.findViewById(R.id.expensesScrollViewLinearLayout);

        //Inflate a category_expense_row_layout for each expense
        BigDecimal amount = new BigDecimal(0);
        for (Expense e : expensesToDisplay) {
            final Expense expense = e;
            //TODO: Figure out what this third parameter is for
            View item = View.inflate(this, R.layout.display_expenses_row_layout, null);

            View view = item.findViewById(R.id.categoryColorView);
            view.setBackgroundColor(e.getCategory().getColor());

            TextView dateTextView = (TextView) item.findViewById(R.id.expenseDateTextView);
            TextView locationTextView = (TextView) item.findViewById(R.id.expenseLocationTextView);
            TextView amountTextView = (TextView) item.findViewById(R.id.expensesAmountTextView);

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
                    Intent intent = new Intent(DisplayExpensesActivity.this, AddExpenseActivity.class);
                    intent.putExtra(EXTRA_EXPENSE_OBJECT, expense);

                    if (expense.isRecurring()) {
                        if (expense.isIncome())
                            intent.putExtra(EXTRA_EXPENSE_TYPE, "Income");
                        else
                            intent.putExtra(EXTRA_EXPENSE_TYPE, "Recurring");
                    } else {
                        intent.putExtra(EXTRA_EXPENSE_TYPE, "Normal");
                    }

                    startActivity(intent);
                }
            });
        }

        amountExpensesTextView.setText(getString(R.string.currency, amount));
    }
}
