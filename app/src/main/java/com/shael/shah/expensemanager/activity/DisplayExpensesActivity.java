package com.shael.shah.expensemanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.utils.DataSingleton;
import com.shael.shah.expensemanager.model.Expense;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DisplayExpensesActivity extends Activity {

    /*****************************************************************
     * Private Variables
     *****************************************************************/

    private static final String EXTRA_EXPENSES_TITLE = "com.shael.shah.expensemanager.EXTRA_EXPENSES_TITLE";
    private static final String EXTRA_EXPENSE_DATE = "com.shael.shah.expensemanager.EXTRA_EXPENSE_DATE";
    private static final String EXTRA_EXPENSE_OBJECT = "com.shael.shah.expensemanager.EXTRA_EXPENSE_OBJECT";

    private List<Expense> filteredExpenses;

    private boolean amountSort = true;
    private boolean locationSort = true;

    private TextView amountIncomesTextView;
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
        setContentView(R.layout.activity_display_expenses);

        Toolbar toolbar = findViewById(R.id.displayExpensesActivityToolbar);
        setActionBar(toolbar);
        getActionBar().setTitle(null);

        DataSingleton instance = DataSingleton.getInstance();
        List<Expense> expenses = instance.getExpenses();

        Intent intent = getIntent();
        Date date = new Date(intent.getLongExtra(EXTRA_EXPENSE_DATE, -1));
        String title = intent.getStringExtra(EXTRA_EXPENSES_TITLE);

        //Find views to work with during this activity
        amountIncomesTextView = findViewById(R.id.amountIncomesTextView);
        amountExpensesTextView = findViewById(R.id.amountExpensesTextView);

        if (title.equals("Incomes"))
            amountExpensesTextView.setVisibility(View.GONE);

        if (title.equals("Expenses"))
            amountIncomesTextView.setVisibility(View.GONE);

        expensesTitleScrollView = findViewById(R.id.expensesTitleScrollView);
        TextView expensesTitleTextView = findViewById(R.id.expensesTitleTextView);
        expensesTitleTextView.setText(title);

        filteredExpenses = new ArrayList<>();
        for (Expense e : expenses) {
            if (!e.isDelete())
                if (e.getDate().compareTo(date) >= 0)
                    if ((title.equals("Incomes") && e.isIncome()) || (title.equals("Expenses") && !e.isIncome()) || title.equals("All Transactions"))
                        filteredExpenses.add(e);
        }
        populateScrollView(filteredExpenses);
    }

    /*****************************************************************
     * Menu Methods
     *****************************************************************/

    /*
     *  Method called by Android to set up layout for the toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_expenses_menu, menu);
        return true;
    }

    /*
     *  Method called by Android to handle all menu operations.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.filter:
                //createFilterDialog();
                return true;

            case R.id.sort_amount:
                Collections.sort(filteredExpenses, new Comparator<Expense>() {
                    @Override
                    public int compare(Expense o1, Expense o2) {
                        if (amountSort)
                            return o1.getAmount().compareTo(o2.getAmount());
                        else
                            return o2.getAmount().compareTo(o1.getAmount());
                    }
                });

                amountSort = !amountSort;
                populateScrollView(filteredExpenses);
                return true;

            case R.id.sort_location:
                Collections.sort(filteredExpenses, new Comparator<Expense>() {
                    @Override
                    public int compare(Expense o1, Expense o2) {
                        if (locationSort)
                            return o1.getLocation().compareTo(o2.getLocation());
                        else
                            return o2.getLocation().compareTo(o1.getLocation());
                    }
                });

                locationSort = !locationSort;
                populateScrollView(filteredExpenses);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private View inflateExpenseDisplayRow(final Expense expense) {
        //TODO: Figure out what this third parameter is for
        View item = View.inflate(this, R.layout.display_expenses_row_layout, null);

        View view = item.findViewById(R.id.categoryColorView);
        //noinspection deprecation
        int color = expense.getCategory() == null ? getResources().getColor(R.color.lightGreen) : expense.getCategory().getColor();
        view.setBackgroundColor(color);

        TextView dateTextView = item.findViewById(R.id.expenseDateTextView);
        TextView locationTextView = item.findViewById(R.id.expenseLocationTextView);
        TextView amountTextView = item.findViewById(R.id.expensesAmountTextView);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
        dateTextView.setText(sdf.format(expense.getDate()));
        locationTextView.setText(expense.getLocation());
        amountTextView.setText(getString(R.string.currency, expense.getAmount()));

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayExpensesActivity.this, UpdateExpenseActivity.class);
                intent.putExtra(EXTRA_EXPENSE_OBJECT, expense);
                startActivity(intent);
            }
        });

        return item;
    }

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

    /*
     *  Iterates through all expenses to check which expenses were requested to
     *  be displayed.
     */
    private void populateScrollView(List<Expense> expensesToDisplay) {

        LinearLayout scrollLinearLayout = expensesTitleScrollView.findViewById(R.id.expensesScrollViewLinearLayout);
        if (scrollLinearLayout.getChildCount() > 0)
            scrollLinearLayout.removeAllViews();

        //Inflate a category_expense_row_layout for each expense
        BigDecimal amountEarned = new BigDecimal(0);
        BigDecimal amountSpent = new BigDecimal(0);
        for (Expense e : expensesToDisplay) {
            if (e.isIncome())
                amountEarned = amountEarned.add(e.getAmount());
            else
                amountSpent = amountSpent.add(e.getAmount());

            scrollLinearLayout.addView(inflateExpenseDisplayRow(e));
        }

        if (amountIncomesTextView != null)
            amountIncomesTextView.setText(getString(R.string.currency, amountEarned));

        if (amountExpensesTextView != null)
            amountExpensesTextView.setText(getString(R.string.currency, amountSpent));
    }
}
