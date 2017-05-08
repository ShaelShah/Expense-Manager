package com.shael.shah.expensemanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddExpenseActivity extends Activity {

    private Toolbar toolbar;

    private EditText amountEditText;
    private EditText dateEditText;
    private EditText locationEditText;
    private EditText noteEditText;
    private CheckBox incomeCheckbox;
    private CheckBox recurringCheckbox;
    private ScrollView categoryScrollView;

    private List<Category> categories;
    private List<RadioButton> categoryRadioButtons;

    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR);
    private int month = calendar.get(Calendar.MONTH);
    private int day = calendar.get(Calendar.DAY_OF_MONTH);

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        categoryScrollView = (ScrollView) findViewById(R.id.categoryScrollView);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        locationEditText = (EditText) findViewById(R.id.locationEditText);
        noteEditText = (EditText) findViewById(R.id.noteEditText);
        incomeCheckbox = (CheckBox) findViewById(R.id.incomeCheckbox);
        recurringCheckbox = (CheckBox) findViewById(R.id.recurringCheckbox);

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setActionBar(toolbar);

        categories = Singleton.getInstance(this).getCategories();
        categoryRadioButtons = new ArrayList<>();

        createCategoryRows();
        createSpinnerRows();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_expense, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(this, MainActivity.class);
        switch (item.getItemId()) {
            case R.id.cancel_label:
                startActivity(intent);
                return true;

            case R.id.save_label:
                try{
                    saveExpense();
                    startActivity(intent);
                } catch (ParseException e) {

                } catch (IllegalArgumentException e) {

                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveExpense() throws ParseException, IllegalArgumentException {
        Double amount = Double.parseDouble(amountEditText.getText().toString());
        Date date = sdf.parse(dateEditText.getText().toString());
        String location = locationEditText.getText().toString();
        String note = noteEditText.getText().toString();
        Boolean income = incomeCheckbox.isSelected();
        Boolean recurring = recurringCheckbox.isSelected();

        Category category = null;
        for (RadioButton rb : categoryRadioButtons) {
            if (rb.isSelected()) {
                category = new Category(rb.getText().toString());
            }
        }
        Expense expense = new Expense(date, amount, category, location, note, income, recurring);
        Singleton.getInstance(this).addExpense(expense);
    }

    private void createCategoryRows() {
        LinearLayout scrollLinearLayout = (LinearLayout) categoryScrollView.findViewById(R.id.scrollLinearLayout);

        int i = 0;
        for (Category c : categories) {
            LinearLayout linearLayout = new LinearLayout(this);
            //ViewGroup.LayoutParams params = scrollLinearLayout.getLayoutParams();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(params);
            linearLayout.setId(i++);

            View item = View.inflate(this, R.layout.category_row_layout, null);

            View colorBox = item.findViewById(R.id.colorView);
            colorBox.setBackgroundColor(Color.RED);

            //TextView categoryNameTextView = (TextView) item.findViewById(R.id.categoryNameTextView);
            //categoryNameTextView.setText(c.getType());

            RadioButton categoryRadioButton = (RadioButton) item.findViewById(R.id.categoryRadioButton);
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

            linearLayout.addView(item);
            scrollLinearLayout.addView(linearLayout);
        }
    }

    public void enableSpinner(View view) {
        Spinner recurringSpinner = (Spinner) findViewById(R.id.recurringSpinner);


    }

    private void createSpinnerRows() {
        Spinner recurringSpinner = (Spinner) findViewById(R.id.recurringSpinner);

        String items[] = new String[] {"Daily", "Weekly", "Monthly", "Yearly"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurringSpinner.setAdapter(spinnerAdapter);
    }
}
