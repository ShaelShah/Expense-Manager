package com.shael.shah.expensemanager.activity;

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
import android.widget.Toast;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.model.Income;
import com.shael.shah.expensemanager.utils.DataSingleton;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddIncomeActivity extends Activity {

    /*****************************************************************
     * Private Variables
     *****************************************************************/

    private DataSingleton instance;

    private EditText amountEditText;
    private EditText dateEditText;
    private EditText locationEditText;
    private EditText noteEditText;
    private Spinner recurringSpinner;

    /*****************************************************************
     * Lifecycle Methods
     *****************************************************************/

    /*
     *  Initial method called by the system during activity startup.
     *  Responsible for getting a copy of all categories.
     *  Also responsible for setting up of the initial GUI.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        //Find views to work with during add expense activity
        amountEditText = findViewById(R.id.amountEditText);
        dateEditText = findViewById(R.id.dateEditText);
        locationEditText = findViewById(R.id.locationEditText);
        noteEditText = findViewById(R.id.noteEditText);
        recurringSpinner = findViewById(R.id.recurringSpinner);

        instance = DataSingleton.getInstance();

        //Helper functions
        createRecurringSpinnerRows();
        populateInfoFields();

        //Disables keyboard from automatically popping up when this activity starts
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*****************************************************************
     * Functionality Methods
     *****************************************************************/

    /*
     *  Converts user input to an Expense object.
     *
     *  Returns true on a successful save of the expense, false otherwise.
     */
    private boolean saveTransaction() {
        BigDecimal amount;
        Date date;
        NumberFormat format = NumberFormat.getCurrencyInstance();

        try {
            String amountEntered = amountEditText.getText().toString();
            String formatted = format.format(Double.parseDouble(amountEntered)).replaceAll("[^\\d.]", "");
            amount = new BigDecimal(formatted);
            date = dateEditText.getText().toString().equals("Today") ? Calendar.getInstance().getTime() : new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA).parse(dateEditText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid Amount Format", Toast.LENGTH_LONG).show();
            return false;
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Invalid Amount Entered", Toast.LENGTH_LONG).show();
            return false;
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid Date Entered", Toast.LENGTH_LONG).show();
            return false;
        }

        if (locationEditText.getText().length() == 0) {
            Toast.makeText(this, "Please Enter a Location", Toast.LENGTH_LONG).show();
            return false;
        }

        String location = locationEditText.getText().toString();
        String note = noteEditText.getText().toString();

        Income.Builder builder = new Income.Builder(date, amount, location).note(note);
        builder.recurringPeriod(recurringSpinner.getSelectedItem().toString());
        instance.addIncome(builder.build());

        return true;
    }

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

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
     *  Helper function used to de-clutter onCreate method.
     *
     *  This activity is used for 2 use cases; creating a new expense, modifying/deleting
     *  an old expense. To support this, the intent that created this activity is checked for
     *  an extra and the GUI is set up appropriately.
     */
    private void populateInfoFields() {
        View.OnClickListener dateListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddIncomeActivity.this,
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

    /*
     *  Helper function used to populate the recurring period spinner.
     */
    private void createRecurringSpinnerRows() {
        String recurringItems[] = new String[]{"None", "Daily", "Weekly", "Bi-Weekly", "Monthly", "Yearly"};
        ArrayAdapter<String> recurringSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, recurringItems);
        recurringSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurringSpinner.setAdapter(recurringSpinnerAdapter);
    }
}
