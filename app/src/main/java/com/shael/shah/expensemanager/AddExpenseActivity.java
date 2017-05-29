package com.shael.shah.expensemanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddExpenseActivity extends Activity {

    /*****************************************************************
     * Private Variables
     ******************************************************************/

    private EditText amountEditText;
    private TextView categoryLabelTextView;
    private EditText dateEditText;
    private EditText locationEditText;
    private EditText noteEditText;
    private CheckBox incomeCheckbox;
    private CheckBox recurringCheckbox;
    private ScrollView categoryScrollView;
    private Spinner recurringSpinner;

    private List<Category> categories;
    private List<RadioButton> categoryRadioButtons;

    //TODO: May be beneficial to move away from Java Date class
    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR);
    private int month = calendar.get(Calendar.MONTH);
    private int day = calendar.get(Calendar.DAY_OF_MONTH);
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);

    /*****************************************************************
     * Lifecycle Methods
     *****************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        //Setup toolbar
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        //Find views to work with during add expense activity
        categoryScrollView = (ScrollView) findViewById(R.id.categoryScrollView);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        categoryLabelTextView = (TextView) findViewById(R.id.categoryLabelTextView);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        locationEditText = (EditText) findViewById(R.id.locationEditText);
        noteEditText = (EditText) findViewById(R.id.noteEditText);
        incomeCheckbox = (CheckBox) findViewById(R.id.incomeCheckbox);
        recurringCheckbox = (CheckBox) findViewById(R.id.recurringCheckbox);
        recurringSpinner = (Spinner) findViewById(R.id.recurringSpinner);

        categoryLabelTextView.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(amountEditText.getWindowToken(), 0);

        //Action Listeners
        recurringCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Spinner recurringSpinner = (Spinner) findViewById(R.id.recurringSpinner);
                if (isChecked)
                    recurringSpinner.setClickable(true);
                else
                    recurringSpinner.setClickable(false);
            }
        });

        //TODO: May be beneficial to move away from Java Date class
        dateEditText.setText(sdf.format(calendar.getTime()));
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddExpenseActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, day);

                                dateEditText.setText(sdf.format(calendar.getTime()));
                            }

                }, year, month, day);

                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }
        });

        categories = Singleton.getInstance(this).getCategories();
        categoryRadioButtons = new ArrayList<>();

        //Helper functions
        createCategoryRows();
        createSpinnerRows();
    }

    /*****************************************************************
     * Menu Methods
     *****************************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_expense, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO: Better handling/controlling of activities
        Intent intent = new Intent(this, MainActivity.class);
        switch (item.getItemId()) {
            case R.id.cancel_label:
                startActivity(intent);
                return true;
            case R.id.save_label:
                try {
                    saveExpense();
                    startActivity(intent);
                } catch (ParseException e) {
                    //TODO: Implement better exception handling
                    Log.d("onOptionsItemSelected", "Issue");
                    //TODO: Figure out this warning
                } catch (IllegalArgumentException e) {
                    //TODO: Implement better exception handling
                    Log.d("onOptionsItemSelected", "Issue");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Helper function used to convert user input to an expense object
    private void saveExpense() throws ParseException, IllegalArgumentException {
        //TODO: Implement boundary checking on input variables
        BigDecimal amount       = new BigDecimal(amountEditText.getText().toString());
        Date date               = sdf.parse(dateEditText.getText().toString());
        String location         = locationEditText.getText().toString();
        String note             = noteEditText.getText().toString();
        Boolean income          = incomeCheckbox.isChecked();
        Boolean recurring       = recurringCheckbox.isChecked();
        String recurringPeriod  = recurringSpinner.getSelectedItem().toString();

        Category category = null;
        boolean found = false;
        for (RadioButton rb : categoryRadioButtons) {
            if (rb.isChecked()) {
                String categoryName = rb.getText().toString();
                for (Category c : categories) {
                    if (c.getType().equals(categoryName)) {
                        category = c;
                        found = true;
                        break;
                    }
                }
            }

            if (found) {
                break;
            }
        }

        if (category == null) {
            //TODO: ParseException has not been implemented correctly
            throw new ParseException("Parse Exception", -1);
        }

        Expense expense = new Expense(date, amount, category, location, note, recurring, income, recurringPeriod);
        Singleton.getInstance(this).addExpense(expense);
    }

    /*****************************************************************
     * Helper Methods
     *****************************************************************/

    //Iterates through all categories and inflates a layout for each
    private void createCategoryRows() {
        LinearLayout scrollLinearLayout = (LinearLayout) categoryScrollView.findViewById(R.id.scrollLinearLayout);

        for (int i = 0; i < categories.size(); i++) {
            //TODO: Look into View.inflate method (specifically the 3rd parameter)
            View item = View.inflate(this, R.layout.category_row_layout, null);

            //TODO: Color should be set dynamically and uniquely for each category
            View colorBox = item.findViewById(R.id.colorView);
            colorBox.setBackgroundColor(Color.RED);

            RadioButton categoryRadioButton = (RadioButton) item.findViewById(R.id.categoryRadioButton);
            categoryRadioButtons.add(categoryRadioButton);
            categoryRadioButton.setText(categories.get(i).getType());
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

            if (i != categories.size() - 1) {
                View line = new View(this);
                line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                line.setBackgroundColor(Color.LTGRAY);
                scrollLinearLayout.addView(line);
            }
        }
    }

    //onClick method for recurring checkbox
    public void enableSpinner(View view) {
        //TODO: Should this be implemented using onClick or action listener?
        CheckBox recurringCheckBox = (CheckBox) view;

        if (recurringCheckBox.isChecked()) {
            recurringSpinner.setEnabled(true);
            recurringSpinner.setClickable(true);
        } else {
            recurringSpinner.setEnabled(false);
            recurringSpinner.setClickable(false);
        }
    }

    //Helper function used to populate recurring spinner
    private void createSpinnerRows() {
        recurringSpinner.setEnabled(false);
        recurringSpinner.setClickable(false);

        String items[] = new String[] {"Daily", "Weekly", "Monthly", "Yearly"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurringSpinner.setAdapter(spinnerAdapter);
    }
}
