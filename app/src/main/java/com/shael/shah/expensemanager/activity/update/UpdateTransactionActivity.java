package com.shael.shah.expensemanager.activity.update;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toolbar;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.utils.DataSingleton;

public abstract class UpdateTransactionActivity extends Activity {

    /*****************************************************************
     * Private Variables
     *****************************************************************/

    public static final String EXTRA_EXPENSE_ID = "com.shael.shah.expensemanager.EXTRA_EXPENSE_ID";

    public DataSingleton instance;

    public EditText amountEditText;
    public EditText dateEditText;
    public EditText locationEditText;
    public EditText noteEditText;
    public Spinner recurringSpinner;
    public ArrayAdapter<String> recurringSpinnerAdapter;

    /*****************************************************************
     * Lifecycle Methods
     *****************************************************************/

    /*
     *  Initial method called by the system during activity startup.
     *  Responsible for getting a copy of all categories.
     *  Also responsible for setting up of the initial GUI.
     */
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(getLayoutResourceID());

        //Toolbar
        Toolbar toolbar = findViewById(R.id.updateExpenseToolbar);
        setActionBar(toolbar);

        //Find views to work with during add expense activity
        amountEditText = findViewById(R.id.amountEditText);
        dateEditText = findViewById(R.id.dateEditText);
        locationEditText = findViewById(R.id.locationEditText);
        noteEditText = findViewById(R.id.noteEditText);
        recurringSpinner = findViewById(R.id.recurringSpinner);

        instance = DataSingleton.getInstance();

        //Disables keyboard from automatically popping up when this activity starts
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void createRecurringSpinnerRows() {
        String recurringItems[] = new String[]{"None", "Daily", "Weekly", "Bi-Weekly", "Monthly", "Yearly"};
        recurringSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, recurringItems);
        recurringSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurringSpinner.setAdapter(recurringSpinnerAdapter);
    }

    protected abstract int getLayoutResourceID();

    protected abstract boolean saveTransaction();

    public abstract void delete(View view);

    public abstract void cancel(View view);

    public abstract void update(View view);

    protected abstract void populateInfoFields();
}
