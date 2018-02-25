package com.shael.shah.expensemanager.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.model.Category;
import com.shael.shah.expensemanager.utils.DataSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public abstract class AddTransactionActivity extends Activity {

    /*****************************************************************
     * Private Variables
     *****************************************************************/

    public DataSingleton instance;

    public EditText amountEditText;
    public EditText dateEditText;
    public EditText locationEditText;
    public EditText noteEditText;
    //private ScrollView categoryScrollView;
    public Spinner recurringSpinner;
    //private Spinner paymentSpinner;

    //private List<Category> categories;
    //private List<RadioButton> categoryRadioButtons;

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

        //Find views to work with during add expense activity
        //categoryScrollView = findViewById(R.id.categoryScrollView);
        amountEditText = findViewById(R.id.amountEditText);
        dateEditText = findViewById(R.id.dateEditText);
        locationEditText = findViewById(R.id.locationEditText);
        noteEditText = findViewById(R.id.noteEditText);
        //paymentSpinner = findViewById(R.id.paymentSpinner);
        recurringSpinner = findViewById(R.id.recurringSpinner);

        instance = DataSingleton.getInstance();
        //categories = instance.getCategories();
        //categoryRadioButtons = new ArrayList<>();

        //Helper functions
        //createCategoryRows();
        createRecurringSpinnerRows();
        //createPaymentSpinnerRows();
        //populateInfoFields();

        //Disables keyboard from automatically popping up when this activity starts
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void cancel(View view) {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void save(View view) {
        if (saveTransaction()) {
            Intent intent = new Intent(this, LandingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    /*
     *  Helper function used to populate the recurring period spinner.
     */
    public void createRecurringSpinnerRows() {
        String recurringItems[] = new String[]{"None", "Daily", "Weekly", "Bi-Weekly", "Monthly", "Yearly"};
        ArrayAdapter<String> recurringSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, recurringItems);
        recurringSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurringSpinner.setAdapter(recurringSpinnerAdapter);
    }

    /*
     *  Helper function to create separators used inbetween categories.
     */
    public View createSeparatorView() {
        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        line.setBackgroundColor(Color.LTGRAY);

        return line;
    }

    public void populateInfoFields() {
        View.OnClickListener dateListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddTransactionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
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

    protected abstract int getLayoutResourceID();
    protected abstract boolean saveTransaction();
}


