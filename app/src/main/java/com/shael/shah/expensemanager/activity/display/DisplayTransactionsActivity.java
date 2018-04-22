package com.shael.shah.expensemanager.activity.display;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.model.Expense;
import com.shael.shah.expensemanager.model.Income;
import com.shael.shah.expensemanager.model.Transaction;
import com.shael.shah.expensemanager.utils.DataSingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public abstract class DisplayTransactionsActivity extends Activity
{

    /*****************************************************************
     * Private Variables
     *****************************************************************/

    private static final String EXTRA_TRANSACTION_DATE = "com.shael.shah.expensemanager.EXTRA_TRANSACTION_DATE";

    protected DataSingleton instance;
    private List<Transaction> filteredTransactions;

    private boolean amountSort = true;
    private boolean locationSort = true;

    private ListView transactionListView;
    private TextView amountTextView;

    /*****************************************************************
     * Lifecycle Methods
     *****************************************************************/

    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.activity_display_transactions);

        Toolbar toolbar = findViewById(R.id.displayExpensesActivityToolbar);
        setActionBar(toolbar);

        if (getActionBar() != null)
        {
            getActionBar().setTitle(null);
        }

        amountTextView = findViewById(R.id.amountTextView);
        transactionListView = findViewById(R.id.transactionsListView);
        final TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(getTitleText());

        instance = DataSingleton.getInstance();

        Intent intent = getIntent();
        Date date = new Date(intent.getLongExtra(EXTRA_TRANSACTION_DATE, -1));
        filteredTransactions = new ArrayList<>();
        for (Transaction t : getTransactions())
        {
            if (t.getDate().compareTo(date) >= 0)
            {
                filteredTransactions.add(t);
            }
        }

        final DisplayTransactionAdapter adapter = new DisplayTransactionAdapter(this, filteredTransactions);
        transactionListView.setAdapter(adapter);
        transactionListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        transactionListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
        {

            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b)
            {
                int checkedItems = transactionListView.getCheckedItemCount();
                actionMode.setTitle(String.valueOf(checkedItems) + " Selected");
                actionMode.invalidate();
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
            {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.display_expenses_menu_context, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
            {
                return true;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.delete_transaction:
                        AlertDialog.Builder builder = new AlertDialog.Builder(DisplayTransactionsActivity.this);
                        builder.setTitle("Confirm Delete");
                        builder.setMessage("Are you sure you wanted to delete the selected transaction?");

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int ID)
                            {
                                dialog.dismiss();
                            }
                        });

                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int ID)
                            {
                                int deleteCount = 0;
                                SparseBooleanArray selectedItems = transactionListView.getCheckedItemPositions();
                                for (int i = transactionListView.getCount() - 1; i >= 0; i--)
                                {
                                    if (selectedItems.get(i))
                                    {
                                        Transaction transaction = filteredTransactions.get(i);
                                        if (transaction instanceof Expense)
                                        {
                                            if (instance.deleteExpense((Expense) transaction))
                                            {
                                                deleteCount++;
                                            }
                                        } else
                                        {
                                            if (instance.deleteIncome((Income) transaction))
                                            {
                                                deleteCount++;
                                            }
                                        }

                                        filteredTransactions.remove(i);
                                    }
                                }

                                dialog.dismiss();
                                adapter.notifyDataSetChanged();
                                actionMode.finish();

                                if (deleteCount > 0)
                                {
                                    Toast.makeText(DisplayTransactionsActivity.this, "Transaction(s) deleted", Toast.LENGTH_LONG).show();
                                } else
                                {
                                    Toast.makeText(DisplayTransactionsActivity.this, "Transaction(s) could not be deleted", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode)
            {
            }
        });

        transactionListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                final Transaction transaction = (Transaction) adapterView.getItemAtPosition(i);
                int transactionID = transaction instanceof Income ? ((Income) transaction).getIncomeID() : ((Expense) transaction).getExpenseID();
                Intent intent = getTransactionIntent(transactionID, transaction instanceof Income);
                startActivity(intent);
            }
        });

        //populateScrollView(filteredTransactions);
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.display_expenses_menu, menu);
        return true;
    }

    /*
     *  Method called by Android to handle all menu operations.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.filter:
                //createFilterDialog();
                return true;

            case R.id.sort_amount:
                Collections.sort(filteredTransactions, new Comparator<Transaction>()
                {
                    @Override
                    public int compare(Transaction o1, Transaction o2)
                    {
                        return amountSort ? o1.getAmount().compareTo(o2.getAmount()) : o2.getAmount().compareTo(o1.getAmount());
                    }
                });

                amountSort = !amountSort;
                //populateScrollView(filteredTransactions);
                return true;

            case R.id.sort_location:
                Collections.sort(filteredTransactions, new Comparator<Transaction>()
                {
                    @Override
                    public int compare(Transaction o1, Transaction o2)
                    {
                        return locationSort ? o1.getLocation().compareTo(o2.getLocation()) : o2.getLocation().compareTo(o1.getLocation());
                    }
                });

                locationSort = !locationSort;
                //populateScrollView(filteredTransactions);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
