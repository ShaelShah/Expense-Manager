package com.shael.shah.expensemanager.activity.update;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.activity.LandingActivity;
import com.shael.shah.expensemanager.model.Income;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateIncomeActivity extends UpdateTransactionActivity {

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

        createRecurringSpinnerRows();
        populateInfoFields();
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_update_income;
    }

    /*****************************************************************
     * Functionality Methods
     *****************************************************************/

    /*
     *  Converts user input to an Expense object.
     *
     *  Returns true on a successful save of the expense, false otherwise.
     */
    @Override
    protected boolean saveTransaction() {
        BigDecimal amount;
        Date date;
        NumberFormat format = NumberFormat.getCurrencyInstance();

        try {
            Double amountEntered = Double.parseDouble(amountEditText.getText().toString().replaceAll("[^\\d.]", ""));
            String formatted = format.format(amountEntered).replaceAll("[^\\d.]", "");
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

    @Override
    public void delete(View view) {
        int incomeID = getIntent().getIntExtra(EXTRA_EXPENSE_ID, -1);
        Income income = instance.getIncome(incomeID);
        instance.deleteIncome(income);

        Toast.makeText(this, "Transaction Deleted", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void cancel(View view) {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void update(View view) {
        int incomeID = getIntent().getIntExtra(EXTRA_EXPENSE_ID, -1);
        Income income = instance.getIncome(incomeID);
        instance.deleteIncome(income);

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
    @Override
    public void populateInfoFields() {
        View.OnClickListener dateListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateIncomeActivity.this,
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

        int incomeID = getIntent().getIntExtra(EXTRA_EXPENSE_ID, -1);
        Income income = instance.getIncome(incomeID);
        amountEditText.setText(getString(R.string.currency, income.getAmount()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
        dateEditText.setText(sdf.format(income.getDate()));
        locationEditText.setText(income.getLocation());
        noteEditText.setText(income.getNote());
        dateEditText.setOnClickListener(dateListener);
        recurringSpinner.setSelection(recurringSpinnerAdapter.getPosition(income.getRecurringPeriod()));
    }
}
