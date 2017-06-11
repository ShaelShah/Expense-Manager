package com.shael.shah.expensemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
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

public class DisplayExpensesActivity extends Activity {

    /*****************************************************************
     * Private Variables
     *****************************************************************/

    private static final String EXTRA_EXPENSES_DISPLAY = "com.shael.shah.expensemanager.EXTRA_EXPENSES_DISPLAY";
    private static final String EXTRA_EXPENSES_TITLE = "com.shael.shah.expensemanager.EXTRA_EXPENSES_TITLE";
    private static final String EXTRA_EXPENSE_TYPE = "com.shael.shah.expensemanager.EXTRA_EXPENSE_TYPE";
    private static final String EXTRA_EXPENSE_OBJECT = "com.shael.shah.expensemanager.EXTRA_EXPENSE_OBJECT";

    ArrayList<Expense> allExpenses;
    ArrayList<Expense> filteredExpenses;

    private TextView expensesTitleTextView;
    private TextView amountExpensesTextView;
    private ScrollView expensesTitleScrollView;

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
     *  Responsible for getting a copy of all expenses.
     *  Also responsible for setting up of the initial GUI.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_expenses);

        Intent intent = getIntent();
        allExpenses = (ArrayList<Expense>) intent.getSerializableExtra(EXTRA_EXPENSES_DISPLAY);
        filteredExpenses = allExpenses;
        String title = intent.getStringExtra(EXTRA_EXPENSES_TITLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.displayExpensesActivityToolbar);
        setActionBar(toolbar);

        //Find views to work with during this activity
        expensesTitleTextView = (TextView) findViewById(R.id.expensesTitleTextView);
        amountExpensesTextView = (TextView) findViewById(R.id.amountExpensesTextView);
        expensesTitleScrollView = (ScrollView) findViewById(R.id.expensesTitleScrollView);
        expensesTitleTextView.setText(title);

        populateScrollView(allExpenses);
    }

    /*****************************************************************
     * Menu Methods
     *****************************************************************/

    /*
     *  Method called by Android to set up layout for the toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_expenses_menu, menu);
        return true;
    }

    /*
     *  Method called by Android to handle all menu operations.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.filter:

                createFilterDialog();
                return true;

            case R.id.sort_amount:
                return true;

            case R.id.sort_location:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createFilterDialog() {
        final List<RadioButton> categoryRadioButtons = new ArrayList<>();
        List<Category> categories = Singleton.getInstance(null).getCategories();

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = layoutInflater.inflate(R.layout.filter_options_dialog, null);

        final EditText startDateEditText = (EditText) view.findViewById(R.id.startDateEditText);
        final EditText endDateEditText = (EditText) view.findViewById(R.id.endDateEditText);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView textView = (TextView) v;

                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        textView.setText(sdf.format(calendar.getTime()));
                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(DisplayExpensesActivity.this, onDateSetListener, year, month, day);
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }
        };

        startDateEditText.setOnClickListener(onClickListener);
        endDateEditText.setOnClickListener(onClickListener);

        LinearLayout scrollLinearLayout = (LinearLayout) view.findViewById(R.id.displayExpensesCategoryLinearLayout);
        scrollLinearLayout.addView(inflateCategorySelectRow("All", Color.BLACK, true, categoryRadioButtons));

        for (Category c : categories) {
            scrollLinearLayout.addView(createLine());
            scrollLinearLayout.addView(inflateCategorySelectRow(c.getType(), c.getColor(), false, categoryRadioButtons));
        }

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Dialog displayDialog = (Dialog) dialog;
                EditText startDateEditText = (EditText) displayDialog.findViewById(R.id.startDateEditText);
                EditText endDateEditText = (EditText) displayDialog.findViewById(R.id.endDateEditText);

                Date startDate = null;
                Date endDate = null;

                try {
                    startDate = sdf.parse(startDateEditText.getText().toString());
                    endDate = sdf.parse(endDateEditText.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String category = getCategoryTitle(categoryRadioButtons);

                populateScrollView(startDate, endDate, category);
                dialog.dismiss();
            }
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private View inflateExpenseDisplayRow(final Expense expense) {
        //TODO: Figure out what this third parameter is for
        View item = View.inflate(this, R.layout.display_expenses_row_layout, null);

        View view = item.findViewById(R.id.categoryColorView);
        view.setBackgroundColor(expense.getCategory().getColor());

        TextView dateTextView = (TextView) item.findViewById(R.id.expenseDateTextView);
        TextView locationTextView = (TextView) item.findViewById(R.id.expenseLocationTextView);
        TextView amountTextView = (TextView) item.findViewById(R.id.expensesAmountTextView);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
        dateTextView.setText(sdf.format(expense.getDate()));
        locationTextView.setText(expense.getLocation());
        amountTextView.setText(getString(R.string.currency, expense.getAmount()));

        //TODO: Is it okay if the expense in this onClickListener is final?
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayExpensesActivity.this, AddExpenseActivity.class);
                intent.putExtra(EXTRA_EXPENSE_OBJECT, expense);

                if (expense.isRecurring() && expense.isIncome())
                    intent.putExtra(EXTRA_EXPENSE_TYPE, "Income");
                else if (expense.isRecurring() && !expense.isIncome())
                    intent.putExtra(EXTRA_EXPENSE_TYPE, "Recurring");
                else
                    intent.putExtra(EXTRA_EXPENSE_TYPE, "Normal");

                startActivity(intent);
            }
        });

        return item;
    }

    //TODO: Third parameter is actually a return value, might be bad practice
    private View inflateCategorySelectRow(String category, int color, boolean checked, final List<RadioButton> radioGroup) {
        //TODO: Look into View.inflate method (specifically the 3rd parameter)
        View row = View.inflate(this, R.layout.category_select_row_layout, null);

        View colorBox = row.findViewById(R.id.colorView);
        colorBox.setBackgroundColor(color);

        RadioButton categoryRadioButton = (RadioButton) row.findViewById(R.id.categoryRadioButton);
        categoryRadioButton.setText(category);
        categoryRadioButton.setChecked(checked);
        radioGroup.add(categoryRadioButton);

        categoryRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (RadioButton rb : radioGroup) {
                    if (rb != v)
                        rb.setChecked(false);
                }
            }
        });

        return row;
    }

    private View createLine() {
        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        line.setBackgroundColor(Color.LTGRAY);

        return line;
    }

    private String getCategoryTitle(List<RadioButton> radioButtons) {
        for (RadioButton rb : radioButtons) {
            if (rb.isChecked()) {
                return rb.getText().toString();
            }
        }

        return null;
    }

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

    /*
     *  Iterates through all expenses to check which expenses were requested to
     *  be displayed.
     */
    private void populateScrollView(List<Expense> expensesToDisplay) {

        LinearLayout scrollLinearLayout = (LinearLayout) expensesTitleScrollView.findViewById(R.id.expensesScrollViewLinearLayout);
        if (scrollLinearLayout.getChildCount() > 0)
            scrollLinearLayout.removeAllViews();

//        List<Expense> categoryExpenses = new ArrayList<>();
//        if (category != null && !category.equals("") && !category.equals("All")) {
//            for (Expense e : allExpenses) {
//                if (e.getCategory().getType().equals(category))
//                    categoryExpenses.add(e);
//            }
//        } else {
//            categoryExpenses = allExpenses;
//        }
//
//        List<Expense> dateExpenses = new ArrayList<>();
//        TODO: Can probably be refractored
//        if (startDate != null && endDate != null) {
//            for (Expense e : categoryExpenses) {
//                if (e.getDate().compareTo(startDate) > 0 && e.getDate().compareTo(endDate) < 0)
//                    dateExpenses.add(e);
//            }
//        } else if (startDate != null && endDate == null) {
//            for (Expense e : categoryExpenses) {
//                if (e.getDate().compareTo(startDate) > 0)
//                    dateExpenses.add(e);
//            }
//        } else if (startDate == null && endDate != null) {
//            for (Expense e : categoryExpenses) {
//                if (e.getDate().compareTo(endDate) < 0)
//                    dateExpenses.add(e);
//            }
//        } else {
//            dateExpenses = categoryExpenses;
//        }

        //Inflate a category_expense_row_layout for each expense
        BigDecimal amount = new BigDecimal(0);
        for (Expense e : expensesToDisplay) {
            amount = amount.add(e.getAmount());
            scrollLinearLayout.addView(inflateExpenseDisplayRow(e));
        }

        amountExpensesTextView.setText(getString(R.string.currency, amount));
    }
}
