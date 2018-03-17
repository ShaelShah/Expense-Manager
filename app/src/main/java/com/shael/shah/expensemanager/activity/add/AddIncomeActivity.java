package com.shael.shah.expensemanager.activity.add;

import android.os.Bundle;
import android.widget.Toast;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.model.Income;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddIncomeActivity extends AddTransactionActivity {

    /*****************************************************************
     * Lifecycle Methods
     *****************************************************************/

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_add_income;
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
}
