package com.shael.shah.expensemanager.activity.display;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.model.Expense;
import com.shael.shah.expensemanager.model.Income;
import com.shael.shah.expensemanager.model.Transaction;
import com.shael.shah.expensemanager.utils.DataSingleton;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public abstract class DisplayTransactionsActivity extends Activity {

    /*****************************************************************
     * Private Variables
     *****************************************************************/

    private static final String EXTRA_TRANSACTION_DATE = "com.shael.shah.expensemanager.EXTRA_TRANSACTION_DATE";

    protected DataSingleton instance;
    private List<Transaction> filteredTransactions;

    private boolean amountSort = true;
    private boolean locationSort = true;

    private TextView amountTextView;
    private ScrollView transactionsScrollView;

    /*****************************************************************
     * Lifecycle Methods
     *****************************************************************/

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_display_transactions);

        Toolbar toolbar = findViewById(R.id.displayExpensesActivityToolbar);
        setActionBar(toolbar);

        if (getActionBar() != null)
            getActionBar().setTitle(null);

        amountTextView = findViewById(R.id.amountTextView);
        transactionsScrollView = findViewById(R.id.transactionsScrollView);
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(getTitleText());

        instance = DataSingleton.getInstance();

        Intent intent = getIntent();
        Date date = new Date(intent.getLongExtra(EXTRA_TRANSACTION_DATE, -1));
        filteredTransactions = new ArrayList<>();
        for (Transaction t : getTransactions()) {
            if (!t.isDelete() && t.getDate().compareTo(date) >= 0) {
                filteredTransactions.add(t);
            }
        }

        populateScrollView(filteredTransactions);
    }

    /*****************************************************************
     * Abstract Methods
     *****************************************************************/

    protected abstract String getTitleText();
    protected abstract List<Transaction> getTransactions();
    protected abstract Intent getTransactionIntent(int transactionID, boolean income);

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
                Collections.sort(filteredTransactions, new Comparator<Transaction>() {
                    @Override
                    public int compare(Transaction o1, Transaction o2) {
                        return amountSort ? o1.getAmount().compareTo(o2.getAmount()) : o2.getAmount().compareTo(o1.getAmount());
                    }
                });

                amountSort = !amountSort;
                populateScrollView(filteredTransactions);
                return true;

            case R.id.sort_location:
                Collections.sort(filteredTransactions, new Comparator<Transaction>() {
                    @Override
                    public int compare(Transaction o1, Transaction o2) {
                        return locationSort ? o1.getLocation().compareTo(o2.getLocation()) : o2.getLocation().compareTo(o1.getLocation());
                    }
                });

                locationSort = !locationSort;
                populateScrollView(filteredTransactions);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

    /*
     *  Iterates through all expenses to check which expenses were requested to
     *  be displayed.
     */
    private void populateScrollView(List<Transaction> transactionsToDisplay) {
        LinearLayout scrollLinearLayout = transactionsScrollView.findViewById(R.id.scrollViewLinearLayout);
        if (scrollLinearLayout.getChildCount() > 0)
            scrollLinearLayout.removeAllViews();

        //Inflate a category_expense_row_layout for each expense
        BigDecimal amount = new BigDecimal(0);
        for (Transaction t : transactionsToDisplay) {
            amount = amount.add(t.getAmount());
            scrollLinearLayout.addView(inflateTransactionDisplayRow(t));
        }

        amountTextView.setText(getString(R.string.currency, amount));
    }

    private View inflateTransactionDisplayRow(Transaction transaction) {
        //TODO: Figure out what this third parameter is for
        View item = View.inflate(this, R.layout.display_expenses_view, null);

        View view = item.findViewById(R.id.categoryColorView);
        int color = transaction instanceof Income ? ContextCompat.getColor(getApplicationContext(), R.color.green) : ((Expense) transaction).getCategory().getColor();
        view.setBackgroundColor(color);

        TextView dateTextView = item.findViewById(R.id.expenseDateTextView);
        TextView locationTextView = item.findViewById(R.id.expenseLocationTextView);
        TextView amountTextView = item.findViewById(R.id.expensesAmountTextView);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
        dateTextView.setText(sdf.format(transaction.getDate()));
        locationTextView.setText(transaction.getLocation());
        amountTextView.setText(getString(R.string.currency, transaction.getAmount()));

        int transactionID = transaction instanceof Income ? ((Income) transaction).getIncomeID() : ((Expense) transaction).getExpenseID();
        final Intent intent = getTransactionIntent(transactionID, transaction instanceof Income);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        return item;
    }
}
