package com.shael.shah.expensemanager.activity.update;

import android.app.Activity;
import android.os.Bundle;
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

    protected DataSingleton instance;

    protected EditText amountEditText;
    protected EditText dateEditText;
    protected EditText locationEditText;
    protected EditText noteEditText;
    protected Spinner recurringSpinner;
    protected ArrayAdapter<String> recurringSpinnerAdapter;

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

        createRecurringSpinnerRows();

        //Disables keyboard from automatically popping up when this activity starts
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*****************************************************************
     * Abstract Methods
     *****************************************************************/

    protected abstract int getLayoutResourceID();

    protected abstract void populateInfoFields();

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

    private void createRecurringSpinnerRows() {
        String recurringItems[] = new String[]{"None", "Daily", "Weekly", "Bi-Weekly", "Monthly", "Yearly"};
        recurringSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, recurringItems);
        recurringSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurringSpinner.setAdapter(recurringSpinnerAdapter);
    }
}
