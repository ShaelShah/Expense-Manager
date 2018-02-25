package com.shael.shah.expensemanager.activity.add;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.model.Category;
import com.shael.shah.expensemanager.model.Expense;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddExpenseActivity extends AddTransactionActivity {

    /*****************************************************************
     * Private Variables
     *****************************************************************/

    private ScrollView categoryScrollView;
    private Spinner paymentSpinner;

    private List<Category> categories;
    private List<RadioButton> categoryRadioButtons;

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

        //Find views to work with during add expense activity
        categoryScrollView = findViewById(R.id.categoryScrollView);
        paymentSpinner = findViewById(R.id.paymentSpinner);

        categories = instance.getCategories();
        categoryRadioButtons = new ArrayList<>();

        //Helper functions
        createCategoryRows();
        createRecurringSpinnerRows();
        createPaymentSpinnerRows();
        populateInfoFields();
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_add_expense;
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
        Category category = null;
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

        foundCategory:
        for (RadioButton rb : categoryRadioButtons) {
            if (rb.isChecked()) {
                String categoryName = rb.getText().toString();
                for (Category c : categories) {
                    if (c.getType().equals(categoryName)) {
                        category = c;
                        break foundCategory;
                    }
                }
            }
        }

        if (category == null) {
            Toast.makeText(this, "Please Select a Category", Toast.LENGTH_LONG).show();
            return false;
        }

        Expense.Builder builder = new Expense.Builder(date, amount, category, location).note(note);
        builder.paymentMethod(paymentSpinner.getSelectedItem().toString());
        builder.recurringPeriod(recurringSpinner.getSelectedItem().toString());
        instance.addExpense(builder.build());

        return true;
    }

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

    /*
     *  Iterates through all categories and inflates a category_select_row_layout for each
     *  plus a row for "add category..."
     */
    private void createCategoryRows() {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout scrollLinearLayout = categoryScrollView.findViewById(R.id.scrollLinearLayout);

        for (Category c : categories) {
            View item = inflater.inflate(R.layout.category_select_row_layout, scrollLinearLayout, false);
            item.findViewById(R.id.colorView).setBackgroundColor(c.getColor());

            RadioButton categoryRadioButton = item.findViewById(R.id.categoryRadioButton);
            categoryRadioButtons.add(categoryRadioButton);
            categoryRadioButton.setText(c.getType());
            categoryRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (RadioButton rb : categoryRadioButtons) {
                        if (rb != v)
                            rb.setChecked(false);
                    }
                }
            });

            scrollLinearLayout.addView(item);
            scrollLinearLayout.addView(createSeparatorView());
        }

        inflater.inflate(R.layout.add_category_row_layout, scrollLinearLayout, true);
    }

    /*
     *  OnClickListener for "Add Category..."
     *
     *  Creates a dialog (Alert Dialog) which allows the user to input a category name.
     *  Inflates a category_select_row_layout and inserts it above "add category..."
     */
    public void createAddCategoryDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.add_category_dialog_layout);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int ID) {
                AlertDialog categoryDialog = (AlertDialog) dialog;

                EditText categoryNameEditText = categoryDialog.findViewById(R.id.categoryNameEditText);
                if (instance.addCategory(categoryNameEditText.getText().toString())) {
                    LinearLayout scrollLinearLayout = categoryScrollView.findViewById(R.id.scrollLinearLayout);

                    View item = LayoutInflater.from(getApplicationContext()).inflate(R.layout.category_select_row_layout, scrollLinearLayout, false);
                    item.findViewById(R.id.colorView).setBackgroundColor(categories.get(categories.size() - 1).getColor());

                    RadioButton categoryRadioButton = item.findViewById(R.id.categoryRadioButton);
                    categoryRadioButtons.add(categoryRadioButton);
                    categoryRadioButton.setText(categories.get(categories.size() - 1).getType());
                    categoryRadioButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (RadioButton rb : categoryRadioButtons) {
                                if (rb != v)
                                    rb.setChecked(false);
                            }
                        }
                    });

                    scrollLinearLayout.addView(item, scrollLinearLayout.getChildCount() - 1);
                    scrollLinearLayout.addView(createSeparatorView(), scrollLinearLayout.getChildCount() - 1);

                    Toast.makeText(getApplicationContext(), "Category Added", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Category Already Exists", Toast.LENGTH_LONG).show();
                }

                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*
     *  Helper function used to populate the recurring period spinner.
     */
    private void createPaymentSpinnerRows() {
        String paymentItems[] = new String[]{"Credit", "Debit", "Cash"};
        ArrayAdapter<String> paymentSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentItems);
        paymentSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentSpinner.setAdapter(paymentSpinnerAdapter);
    }
}
