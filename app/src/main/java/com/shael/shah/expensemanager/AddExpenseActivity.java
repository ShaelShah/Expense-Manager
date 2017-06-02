package com.shael.shah.expensemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
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

    private LinearLayout toolbarLinearLayout;
    private EditText amountEditText;
    //TODO: Maybe this should be a local variable
    private EditText dateEditText;
    private EditText locationEditText;
    private EditText noteEditText;
    private CheckBox incomeCheckbox;
    private CheckBox recurringCheckbox;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Find views to work with during add expense activity
        toolbarLinearLayout = (LinearLayout) findViewById(R.id.toolbarLinearLayout);
        categoryScrollView = (ScrollView) findViewById(R.id.categoryScrollView);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        locationEditText = (EditText) findViewById(R.id.locationEditText);
        noteEditText = (EditText) findViewById(R.id.noteEditText);
        incomeCheckbox = (CheckBox) findViewById(R.id.incomeCheckbox);
        recurringCheckbox = (CheckBox) findViewById(R.id.recurringCheckbox);
        recurringSpinner = (Spinner) findViewById(R.id.recurringSpinner);

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

        categories = Singleton.getInstance(null).getCategories();
        categoryRadioButtons = new ArrayList<>();

        //Helper functions
        createCategoryRows();
        createAddCategoryActionListener();
        createSpinnerRows();
        populateInfoFields();
    }

    /*****************************************************************
     * Functionality Methods
     *****************************************************************/

    //Helper function used to convert user input to an expense object
    private boolean saveExpense() {

        BigDecimal amount = null;
        Date date = null;
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
            date = sdf.parse(dateEditText.getText().toString());
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
        Boolean income = incomeCheckbox.isChecked();
        Boolean recurring = recurringCheckbox.isChecked();
        String recurringPeriod = recurringSpinner.getSelectedItem().toString();

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
            if (!income) {
                Toast.makeText(this, "Please Select a Category", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        Expense expense = new Expense(date, amount, category, location, note, recurring, income, recurringPeriod);
        Singleton.getInstance(this).addExpense(expense);

        return true;
    }

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

    //Iterates through all categories and inflates a layout for each
    private void createCategoryRows() {
        LinearLayout scrollLinearLayout = (LinearLayout) categoryScrollView.findViewById(R.id.scrollLinearLayout);

        for (int i = 0; i < categories.size(); i++) {
            //TODO: Look into View.inflate method (specifically the 3rd parameter)
            View item = inflateRow(i);
            scrollLinearLayout.addView(item);

            View line = new View(this);
            line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            line.setBackgroundColor(Color.LTGRAY);
            scrollLinearLayout.addView(line);
        }

        TextView addCategoryTextView = (TextView) createAddCategoryActionListener();
        scrollLinearLayout.addView(addCategoryTextView);
    }

    private View inflateRow(int index) {
        //TODO: Look into View.inflate method (specifically the 3rd parameter)
        View item = View.inflate(this, R.layout.category_row_layout, null);

        View colorBox = item.findViewById(R.id.colorView);
        colorBox.setBackgroundColor(categories.get(index).getColor());

        RadioButton categoryRadioButton = (RadioButton) item.findViewById(R.id.categoryRadioButton);
        categoryRadioButtons.add(categoryRadioButton);
        categoryRadioButton.setText(categories.get(index).getType());
        categoryRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (RadioButton rb : categoryRadioButtons) {
                    if (rb != v)
                        rb.setChecked(false);
                }
            }
        });

        return item;
    }

    private View createAddCategoryActionListener() {
        int paddingDP = (int) (10 * getResources().getDisplayMetrics().density + 0.5f);

        TextView addCategoryTextView = new TextView(this);
        addCategoryTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addCategoryTextView.setText("Add Category...");
        addCategoryTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        addCategoryTextView.setTypeface(null, Typeface.ITALIC);
        addCategoryTextView.setPadding(paddingDP, paddingDP, paddingDP, paddingDP);

        addCategoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(AddExpenseActivity.this);

                AlertDialog.Builder builder = new AlertDialog.Builder(AddExpenseActivity.this);
                builder.setView(inflater.inflate(R.layout.add_category_dialog_layout, null));

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog categoryDialog = (Dialog) dialog;
                        EditText categoryNameEditText = (EditText) categoryDialog.findViewById(R.id.categoryNameEditText);
                        Log.d("createAddCategoryAL", categoryNameEditText.getText().toString());
                        if (Singleton.getInstance(null).addCategory(categoryNameEditText.getText().toString())) {
                            LinearLayout scrollLinearLayout = (LinearLayout) categoryScrollView.findViewById(R.id.scrollLinearLayout);
                            scrollLinearLayout.addView(addNewCategoryRow(), scrollLinearLayout.getChildCount() - 1);
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
        });

        return addCategoryTextView;
    }

    private View addNewCategoryRow() {
        //TODO: Look into View.inflate method (specifically the 3rd parameter)
        View item = View.inflate(this, R.layout.category_row_layout, null);

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

        return item;
    }

    //onClick method for recurring checkbox
    private void enableSpinner(View view) {
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
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurringSpinner.setAdapter(spinnerAdapter);
    }

    //TODO: Function is messy, refractor
    //Helper function used to de-clutter onCreate method
    private void populateInfoFields() {

        if (!getIntent().hasExtra("ExpenseObject")) {

            Button cancel = createToolbarButtons("Cancel");
            Button save = createToolbarButtons("Save");

            View lineOne = createDividerView();

            toolbarLinearLayout.addView(cancel);
            toolbarLinearLayout.addView(lineOne);
            toolbarLinearLayout.addView(save);

            //TODO: There might be a better way to do this
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddExpenseActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

            //TODO: There might be a better way to do this
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
        } else {
            Button delete = createToolbarButtons("Delete");
            Button cancel = createToolbarButtons("Cancel");
            Button save = createToolbarButtons("Save");

            View lineOne = createDividerView();
            View lineTwo = createDividerView();

            toolbarLinearLayout.addView(delete);
            toolbarLinearLayout.addView(lineOne);
            toolbarLinearLayout.addView(cancel);
            toolbarLinearLayout.addView(lineTwo);
            toolbarLinearLayout.addView(save);

            //TODO: There might be a better way to do this
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddExpenseActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

            //TODO: There might be a better way to do this
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Expense expense = (Expense) getIntent().getSerializableExtra("ExpenseObject");
                    Singleton.getInstance(null).removeExpense(expense);

                    if (saveExpense()) {
                        Intent intent = new Intent(AddExpenseActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            });

            //TODO: There might be a better way to do this
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Expense expense = (Expense) getIntent().getSerializableExtra("ExpenseObject");
                    Singleton.getInstance(null).removeExpense(expense);

                    Intent intent = new Intent(AddExpenseActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

            Expense expense = (Expense) getIntent().getSerializableExtra("ExpenseObject");
            //TODO: Do not concatenate with setText
            amountEditText.setText("$" + expense.getAmount());
            dateEditText.setText(sdf.format(expense.getDate()));
            locationEditText.setText(expense.getLocation());
            noteEditText.setText(expense.getNote());
            incomeCheckbox.setChecked(expense.isIncome());
            recurringCheckbox.setChecked(expense.isRecurring());

            int position = spinnerAdapter.getPosition(expense.getRecurringPeriod());
            recurringSpinner.setSelection(position);

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

    //Helper function to create toolbar buttons
    private Button createToolbarButtons(String title) {
        Button button = new Button(new ContextThemeWrapper(this, android.R.style.Widget_Material_Light_Button_Borderless));
        button.setBackgroundColor(Color.LTGRAY);
        button.setText(title);
        button.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        return button;
    }

    //Helper function to create dividers used in toolbar
    private View createDividerView() {
        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT));
        divider.setBackgroundColor(Color.BLACK);

        return divider;
    }
}
