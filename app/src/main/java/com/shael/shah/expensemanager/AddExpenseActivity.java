package com.shael.shah.expensemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

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
     *****************************************************************/

    private static final String EXTRA_EXPENSE_TYPE = "com.shael.shah.expensemanager.EXTRA_EXPENSE_TYPE";
    private static final String EXTRA_EXPENSE_OBJECT = "com.shael.shah.expensemanager.EXTRA_EXPENSE_OBJECT";

    private LinearLayout toolbarLinearLayout;
    private EditText amountEditText;
    private EditText dateEditText;
    private EditText locationEditText;
    private EditText noteEditText;
    private ScrollView categoryScrollView;
    private Spinner recurringSpinner;

    private List<Category> categories;
    private List<RadioButton> categoryRadioButtons;
    private ArrayAdapter<String> spinnerAdapter;

    //TODO: May be beneficial to move away from Java Date class
    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR);
    private int month = calendar.get(Calendar.MONTH);
    private int day = calendar.get(Calendar.DAY_OF_MONTH);
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);

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

        String expenseType = getIntent().getStringExtra(EXTRA_EXPENSE_TYPE);
        if (expenseType.equals("Normal"))
            setContentView(R.layout.activity_add_expense);
        else
            setContentView(R.layout.activity_add_expense_recurring);

        //Disables keyboard from automatically popping up when this activity starts
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Find views to work with during add expense activity
        toolbarLinearLayout = (LinearLayout) findViewById(R.id.toolbarLinearLayout);
        categoryScrollView = (ScrollView) findViewById(R.id.categoryScrollView);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        locationEditText = (EditText) findViewById(R.id.locationEditText);
        noteEditText = (EditText) findViewById(R.id.noteEditText);
        if (!expenseType.equals("Normal"))
            recurringSpinner = (Spinner) findViewById(R.id.recurringSpinner);

        categories = Singleton.getInstance(null).getCategories();
        categoryRadioButtons = new ArrayList<>();

        //Helper functions
        createCategoryRows();
        if (!expenseType.equals("Normal"))
            createSpinnerRows();
        populateInfoFields();
    }

    /*****************************************************************
     * Functionality Methods
     *****************************************************************/

    /*
     *  Converts user input to an Expense object.
     *
     *  Returns true on a successful save of the expense, false otherwise.
     */
    private boolean saveExpense() {

        BigDecimal amount;
        Date date;
        Category category = null;

        String amountString = amountEditText.getText().toString().replaceAll("[^\\d.]", "");
        try {
            amount = new BigDecimal(amountString);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid Amount Entered", Toast.LENGTH_LONG).show();
            return false;
        }

        try {
            if (dateEditText.getText().toString().equals("Today")) {
                date = calendar.getTime();
            } else {
                date = sdf.parse(dateEditText.getText().toString());
            }
        } catch (ParseException e) {
            e.printStackTrace();
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

        String expenseType = getIntent().getStringExtra(EXTRA_EXPENSE_TYPE);
        if (category == null) {
            if (!expenseType.equals("Income")) {
                Toast.makeText(this, "Please Select a Category", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        Expense expense = null;
        String recurringPeriod;
        switch (expenseType) {
            case "Normal":
            expense = new Expense(date, amount, category, location, note, false, false, "");
                break;

            case "Income":
                recurringPeriod = recurringSpinner.getSelectedItem().toString();
                expense = new Expense(date, amount, category, location, note, true, true, recurringPeriod);
                break;

            case "Recurring":
                recurringPeriod = recurringSpinner.getSelectedItem().toString();
                expense = new Expense(date, amount, category, location, note, true, false, recurringPeriod);
                break;
        }

        Singleton.getInstance(this).addExpense(expense);
        return true;
    }

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

    /*
     *  Helper function used to de-clutter onCreate method.
     *
     *  This activity is used for 2 functionalities; creating a new expense, modifying/deleting
     *  an old expense. To support this, the intent that created this activity is checked for
     *  an extra and the GUI is set up appropriately.
     */
    private void populateInfoFields() {
        Button cancel = createToolbarButtons("Cancel");
        Button save = createToolbarButtons("Save");
        View lineOne = createDividerView();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddExpenseActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        if (!getIntent().hasExtra(EXTRA_EXPENSE_OBJECT)) {

            toolbarLinearLayout.addView(cancel);
            toolbarLinearLayout.addView(lineOne);
            toolbarLinearLayout.addView(save);

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (saveExpense()) {
                        Intent intent = new Intent(AddExpenseActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            });

            //TODO: May be beneficial to move away from Java Date class
            dateEditText.setText(R.string.today);
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
        } else {
            Button delete = createToolbarButtons("Delete");
            View lineTwo = createDividerView();

            toolbarLinearLayout.addView(delete);
            toolbarLinearLayout.addView(lineOne);
            toolbarLinearLayout.addView(cancel);
            toolbarLinearLayout.addView(lineTwo);
            toolbarLinearLayout.addView(save);

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Expense expense = (Expense) getIntent().getSerializableExtra(EXTRA_EXPENSE_OBJECT);
                    Singleton.getInstance(null).removeExpense(expense);

                    if (saveExpense()) {
                        Intent intent = new Intent(AddExpenseActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Expense expense = (Expense) getIntent().getSerializableExtra(EXTRA_EXPENSE_OBJECT);
                    Singleton.getInstance(null).removeExpense(expense);

                    Intent intent = new Intent(AddExpenseActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

            Expense expense = (Expense) getIntent().getSerializableExtra(EXTRA_EXPENSE_OBJECT);
            amountEditText.setText(getString(R.string.currency, expense.getAmount()));
            dateEditText.setText(sdf.format(expense.getDate()));
            locationEditText.setText(expense.getLocation());
            noteEditText.setText(expense.getNote());

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

            if (!getIntent().getStringExtra(EXTRA_EXPENSE_TYPE).equals("Normal")) {
                int position = spinnerAdapter.getPosition(expense.getRecurringPeriod());
                recurringSpinner.setSelection(position);
            }

            if (expense.getCategory() != null) {
                String categoryTitle = expense.getCategory().getType();
                for (RadioButton rb : categoryRadioButtons) {
                    if (rb.getText().toString().equals(categoryTitle)) {
                        rb.setChecked(true);
                        break;
                    }
                }
            }
        }
    }

    /*
     *  Iterates through all categories and inflates a category_select_row_layout for each
     *  plus a row for "add category..."
     */
    private void createCategoryRows() {
        LinearLayout scrollLinearLayout = (LinearLayout) categoryScrollView.findViewById(R.id.scrollLinearLayout);

        for (int i = 0; i < categories.size(); i++) {
            //TODO: Look into View.inflate method (specifically the 3rd parameter)
            View item = View.inflate(this, R.layout.category_select_row_layout, null);

            View colorBox = item.findViewById(R.id.colorView);
            colorBox.setBackgroundColor(categories.get(i).getColor());

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

            View line = new View(this);
            line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            line.setBackgroundColor(Color.LTGRAY);
            scrollLinearLayout.addView(line);
        }

        LinearLayout addCategoryTextView = (LinearLayout) View.inflate(this, R.layout.add_category_row_layout, null);
        scrollLinearLayout.addView(addCategoryTextView);
    }

    /*
     *  OnClickListener for "Add Category..."
     *
     *  Creates a dialog (Alert Dialog) which allows the user to input a category name.
     *  Inflates a category_select_row_layout and inserts it above "add category..."
     */
    public void createAddCategoryDialog(View view) {
        LayoutInflater inflater = LayoutInflater.from(AddExpenseActivity.this);

        AlertDialog.Builder builder = new AlertDialog.Builder(AddExpenseActivity.this);
        builder.setView(inflater.inflate(R.layout.add_category_dialog_layout, null));

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Dialog categoryDialog = (Dialog) dialog;
                EditText categoryNameEditText = (EditText) categoryDialog.findViewById(R.id.categoryNameEditText);

                if (Singleton.getInstance(null).addCategory(categoryNameEditText.getText().toString())) {
                    Singleton.getInstance(null).saveLists();

                    LinearLayout scrollLinearLayout = (LinearLayout) categoryScrollView.findViewById(R.id.scrollLinearLayout);

                    //TODO: Look into View.inflate method (specifically the 3rd parameter)
                    View item = View.inflate(AddExpenseActivity.this, R.layout.category_select_row_layout, null);

                    View colorBox = item.findViewById(R.id.colorView);
                    colorBox.setBackgroundColor(categories.get(categories.size() - 1).getColor());

                    RadioButton categoryRadioButton = (RadioButton) item.findViewById(R.id.categoryRadioButton);
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

                    View line = new View(AddExpenseActivity.this);
                    line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                    line.setBackgroundColor(Color.LTGRAY);

                    scrollLinearLayout.addView(item, scrollLinearLayout.getChildCount() - 1);
                    scrollLinearLayout.addView(line, scrollLinearLayout.getChildCount() - 1);

                    Toast.makeText(AddExpenseActivity.this, "Category Added", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddExpenseActivity.this, "Category Already Exists", Toast.LENGTH_LONG).show();
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
    private void createSpinnerRows() {
        String items[] = new String[] {"Daily", "Weekly", "Monthly", "Yearly"};
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurringSpinner.setAdapter(spinnerAdapter);
    }

    /*
     *  Helper function to create toolbar buttons.
     */
    private Button createToolbarButtons(String title) {
        Button button = new Button(new ContextThemeWrapper(this, android.R.style.Widget_Material_Light_Button_Borderless));
        button.setBackgroundColor(Color.LTGRAY);
        button.setText(title);
        button.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        return button;
    }

    /*
     *  Helper function to create dividers used in toolbar.
     */
    private View createDividerView() {
        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT));
        divider.setBackgroundColor(Color.BLACK);

        return divider;
    }
}
