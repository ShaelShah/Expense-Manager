package com.shael.shah.expensemanager.activity.add;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.utils.DataSingleton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public abstract class AddTransactionActivity extends Activity
{

    /*****************************************************************
     * Private Variables
     *****************************************************************/

    protected DataSingleton instance;

    protected EditText amountEditText;
    protected EditText dateEditText;
    protected EditText locationEditText;
    protected EditText noteEditText;
    protected Spinner recurringSpinner;

    /*****************************************************************
     * Lifecycle Methods
     *****************************************************************/

    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(getLayoutResourceID());

        //Find views to work with during add expense activity
        amountEditText = findViewById(R.id.amountEditText);
        dateEditText = findViewById(R.id.dateEditText);
        locationEditText = findViewById(R.id.locationEditText);
        noteEditText = findViewById(R.id.noteEditText);
        recurringSpinner = findViewById(R.id.recurringSpinner);

        instance = DataSingleton.getInstance();

        createRecurringSpinnerRows();
        populateInfoFields();

        //Disables keyboard from automatically popping up when this activity starts
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*****************************************************************
     * Abstract Methods
     *****************************************************************/

    protected abstract int getLayoutResourceID();

    protected abstract boolean saveTransaction();

    /*****************************************************************
     * ActionListeners
     *****************************************************************/

    public void cancel(View view)
    {
//        Intent intent = new Intent(this, LandingActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    public void save(View view)
    {
        if (saveTransaction())
        {
//            Intent intent = new Intent(this, LandingActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

    private void populateInfoFields()
    {
        View.OnClickListener dateListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddTransactionActivity.this,
                        new DatePickerDialog.OnDateSetListener()
                        {

                            public void onDateSet(DatePicker datePicker, int year, int month, int day)
                            {
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);

                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, day);

                                dateEditText.setText(sdf.format(calendar.getTime()));
                            }

                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }
        };

        dateEditText.setText(R.string.today);
        dateEditText.setOnClickListener(dateListener);
    }

    /*
     *  Helper function used to populate the recurring period spinner.
     */
    private void createRecurringSpinnerRows()
    {
        String recurringItems[] = new String[]{"None", "Daily", "Weekly", "Bi-Weekly", "Monthly", "Yearly"};
        ArrayAdapter<String> recurringSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, recurringItems);
        recurringSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurringSpinner.setAdapter(recurringSpinnerAdapter);
    }
}


