package com.shael.shah.expensemanager.activity;

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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.db.AppDatabase;
import com.shael.shah.expensemanager.db.Category;
import com.shael.shah.expensemanager.db.Expense;

import java.math.BigDecimal;
import java.text.NumberFormat;
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

    private AppDatabase appDatabase;

    private LinearLayout toolbarLinearLayout;
    private EditText amountEditText;
    private CheckBox incomeCheckbox;
    private EditText dateEditText;
    private EditText locationEditText;
    private EditText noteEditText;
    private ScrollView categoryScrollView;
    private Spinner recurringSpinner;
    private Spinner paymentSpinner;

    private List<Category> categories;
    private List<RadioButton> categoryRadioButtons;
    private ArrayAdapter<String> recurringSpinnerAdapter;
    private ArrayAdapter<String> paymentSpinnerAdapter;

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
        setContentView(R.layout.activity_add_expense);

        /*String expenseType = getIntent().getStringExtra(EXTRA_EXPENSE_TYPE);
        switch (expenseType) {
            case "Normal":
                setContentView(R.layout.activity_add_expense);
                paymentSpinner = (Spinner) findViewById(R.id.paymentSpinner);

                createPaymentSpinnerRows();
                break;

            case "Income":
                setContentView(R.layout.activity_add_income);
                recurringSpinner = (Spinner) findViewById(R.id.recurringSpinner);

                createRecurringSpinnerRows();
                break;

            case "Recurring":
                setContentView(R.layout.activity_add_expense_recurring);
                paymentSpinner = (Spinner) findViewById(R.id.paymentSpinner);
                recurringSpinner = (Spinner) findViewById(R.id.recurringSpinner);

                createRecurringSpinnerRows();
                createPaymentSpinnerRows();
                break;
        }*/

        appDatabase = AppDatabase.getAppDatabase(getApplicationContext());

        //Find views to work with during add expense activity
        toolbarLinearLayout = findViewById(R.id.toolbarLinearLayout);
        categoryScrollView = findViewById(R.id.categoryScrollView);
        amountEditText = findViewById(R.id.amountEditText);
        incomeCheckbox = findViewById(R.id.incomeCheckbox);
        dateEditText = findViewById(R.id.dateEditText);
        locationEditText = findViewById(R.id.locationEditText);
        noteEditText = findViewById(R.id.noteEditText);
        paymentSpinner = findViewById(R.id.paymentSpinner);
        recurringSpinner = findViewById(R.id.recurringSpinner);

        //categories = DataSingleton.getInstance().getCategories();
        categories = appDatabase.categoryDao().getAllCategories();
        categoryRadioButtons = new ArrayList<>();

        //Helper functions
        createCategoryRows();
        populateInfoFields();
        createRecurringSpinnerRows();
        createPaymentSpinnerRows();

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
    private boolean saveExpense() {
        String amountString;
        BigDecimal amount = null;
        Date date = null;
        Category category = null;

        try {
            NumberFormat format = NumberFormat.getCurrencyInstance();
            amountString = format.format(Double.parseDouble(amountEditText.getText().toString())).replaceAll("[^\\d.]", "");
            amount = new BigDecimal(amountString);
            date = dateEditText.getText().toString().equals("Today") ? Calendar.getInstance().getTime() : new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA).parse(dateEditText.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Invalid Amount Entered", Toast.LENGTH_LONG).show();
            return false;
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid Date Entered", Toast.LENGTH_LONG).show();
            return false;
        }

        //amountString = amountEditText.getText().toString().replaceAll("[^\\d.]", "");

        /*try {
            amount = new BigDecimal(amountString);
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Invalid Amount Entered", Toast.LENGTH_LONG).show();
            return false;
        }

        try {
            date = dateEditText.getText().toString().equals("Today") ? Calendar.getInstance().getTime() : new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA).parse(dateEditText.getText().toString())
            if (dateEditText.getText().toString().equals("Today")) {
                date = Calendar.getInstance().getTime();
            } else {
                date = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA).parse(dateEditText.getText().toString());
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid Date Entered", Toast.LENGTH_LONG).show();
            return false;
        }*/

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

//        String expenseType = getIntent().getStringExtra(EXTRA_EXPENSE_TYPE);
        if (category == null) {
//            if (!expenseType.equals("Income")) {
            Toast.makeText(this, "Please Select a Category", Toast.LENGTH_LONG).show();
            return false;
//            }
        }

        Expense.Builder builder = new Expense.Builder(date, amount, category, location).note(note);
        builder.income(incomeCheckbox.isChecked());
        builder.paymentMethod(paymentSpinner.getSelectedItem().toString());
        builder.recurringPeriod(recurringSpinner.getSelectedItem().toString());

        /*switch (expenseType) {
            case "Normal":
                builder.paymentMethod(paymentSpinner.getSelectedItem().toString());
                break;

            case "Income":
                if (recurringSpinner.getSelectedItem().toString().equals("None")) {
                    builder.recurring(false);
                } else {
                    builder.recurring(true)
                            .income(true)
                            .recurringPeriod(recurringSpinner.getSelectedItem().toString());
                }
                break;

            case "Recurring":
                if (recurringSpinner.getSelectedItem().toString().equals("None")) {
                    Toast.makeText(this, "Recurring Period cannot be None", Toast.LENGTH_LONG).show();
                    return false;
                }

                builder.recurring(true)
                        .income(false)
                        .recurringPeriod(recurringSpinner.getSelectedItem().toString())
                        .paymentMethod(paymentSpinner.getSelectedItem().toString());
                break;
        }*/

        appDatabase.expenseDao().insert(builder.build());
        return true;
    }

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

    /*
     *  Helper function used to de-clutter onCreate method.
     *
     *  This activity is used for 2 use cases; creating a new expense, modifying/deleting
     *  an old expense. To support this, the intent that created this activity is checked for
     *  an extra and the GUI is set up appropriately.
     */
    private void populateInfoFields() {
        Button cancel = createToolbarButton("Cancel");
        View lineOne = createDividerView();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddExpenseActivity.this, LandingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        View.OnClickListener dateListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddExpenseActivity.this,
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

        if (!getIntent().hasExtra(EXTRA_EXPENSE_OBJECT)) {

            Button save = createToolbarButton("Save");

            toolbarLinearLayout.addView(cancel);
            toolbarLinearLayout.addView(lineOne);
            toolbarLinearLayout.addView(save);

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (saveExpense()) {
                        Intent intent = new Intent(AddExpenseActivity.this, LandingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            });

            dateEditText.setText(R.string.today);
            dateEditText.setOnClickListener(dateListener);
        } /*else {
            Button save = createToolbarButton("Update");
            Button delete = createToolbarButton("Delete");
            View lineTwo = createDividerView();

            toolbarLinearLayout.addView(delete);
            toolbarLinearLayout.addView(lineOne);
            toolbarLinearLayout.addView(cancel);
            toolbarLinearLayout.addView(lineTwo);
            toolbarLinearLayout.addView(save);

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Expense expense = getIntent().getParcelableExtra(EXTRA_EXPENSE_OBJECT);
                    appDatabase.expenseDao().delete(expense);

                    if (saveExpense()) {
                        Intent intent = new Intent(AddExpenseActivity.this, LandingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Expense expense = getIntent().getParcelableExtra(EXTRA_EXPENSE_OBJECT);
                    if (appDatabase.expenseDao().delete(expense) != 0)
                        Toast.makeText(AddExpenseActivity.this, "Expense Deleted", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(AddExpenseActivity.this, "Could not find Expense", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(AddExpenseActivity.this, LandingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

            Expense expense = getIntent().getParcelableExtra(EXTRA_EXPENSE_OBJECT);

            amountEditText.setText(getString(R.string.currency, expense.getAmount()));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
            dateEditText.setText(sdf.format(expense.getDate()));
            locationEditText.setText(expense.getLocation());
            noteEditText.setText(expense.getNote());
            dateEditText.setOnClickListener(dateListener);

            String expenseType = getIntent().getStringExtra(EXTRA_EXPENSE_TYPE);
            switch (expenseType) {
                case "Normal":
                    paymentSpinner.setSelection(paymentSpinnerAdapter.getPosition(expense.getPaymentMethod()));
                    break;

                case "Income":
                    recurringSpinner.setSelection(recurringSpinnerAdapter.getPosition(expense.getRecurringPeriod()));
                    break;

                case "Recurring":
                    paymentSpinner.setSelection(paymentSpinnerAdapter.getPosition(expense.getPaymentMethod()));
                    recurringSpinner.setSelection(recurringSpinnerAdapter.getPosition(expense.getRecurringPeriod()));
                    break;
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
        }*/
    }

    /*
     *  Iterates through all categories and inflates a category_select_row_layout for each
     *  plus a row for "add category..."
     */
    private void createCategoryRows() {
        LinearLayout scrollLinearLayout = categoryScrollView.findViewById(R.id.scrollLinearLayout);

        for (int i = 0; i < categories.size(); i++) {
            //TODO: Look into View.inflate method (specifically the 3rd parameter)
            View item = View.inflate(this, R.layout.category_select_row_layout, null);

            View colorBox = item.findViewById(R.id.colorView);
            colorBox.setBackgroundColor(categories.get(i).getColour());

            RadioButton categoryRadioButton = item.findViewById(R.id.categoryRadioButton);
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
            scrollLinearLayout.addView(createSeparatorView());
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
                EditText categoryNameEditText = categoryDialog.findViewById(R.id.categoryNameEditText);

                Category newCategory = new Category(categoryNameEditText.getText().toString());
                if (appDatabase.categoryDao().insert(newCategory) != 0) {
                    //DataSingleton.getInstance().saveLists();
                    categories.add(newCategory);

                    LinearLayout scrollLinearLayout = categoryScrollView.findViewById(R.id.scrollLinearLayout);

                    //TODO: Look into View.inflate method (specifically the 3rd parameter)
                    View item = View.inflate(AddExpenseActivity.this, R.layout.category_select_row_layout, null);

                    View colorBox = item.findViewById(R.id.colorView);
                    colorBox.setBackgroundColor(categories.get(categories.size() - 1).getColour());

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
    private void createRecurringSpinnerRows() {
        String recurringItems[] = new String[]{"None", "Daily", "Weekly", "Bi-Weekly", "Monthly", "Yearly"};
        recurringSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, recurringItems);
        recurringSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurringSpinner.setAdapter(recurringSpinnerAdapter);
    }

    /*
     *  Helper function used to populate the recurring period spinner.
     */
    private void createPaymentSpinnerRows() {
        String paymentItems[] = new String[]{"Credit", "Debit", "Cash"};
        paymentSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentItems);
        paymentSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentSpinner.setAdapter(paymentSpinnerAdapter);
    }

    /*
     *  Helper function to create toolbar buttons.
     */
    private Button createToolbarButton(String title) {
        Button button = new Button(new ContextThemeWrapper(this, android.R.style.Widget_Material_Light_Button_Borderless));
        button.setBackgroundColor(Color.LTGRAY);
        button.setText(title);
        button.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        return button;
    }

    /*
    *  Helper function to create separators used inbetween categories.
    */
    private View createSeparatorView() {
        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        line.setBackgroundColor(Color.LTGRAY);

        return line;
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
