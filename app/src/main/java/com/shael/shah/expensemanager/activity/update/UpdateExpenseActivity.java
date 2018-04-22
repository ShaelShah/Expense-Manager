package com.shael.shah.expensemanager.activity.update;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.activity.LandingActivity;
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

public class UpdateExpenseActivity extends UpdateTransactionActivity
{

    /*****************************************************************
     * Private Variables
     *****************************************************************/

    private static final String EXTRA_TRANSACTION_ID = "com.shael.shah.expensemanager.EXTRA_TRANSACTION_ID";

    private ScrollView categoryScrollView;
    private Spinner paymentSpinner;

    private List<Category> categories;
    private List<RadioButton> categoryRadioButtons;
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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Find views to work with during add expense activity
        categoryScrollView = findViewById(R.id.categoryScrollView);
        paymentSpinner = findViewById(R.id.paymentSpinner);

        categories = instance.getCategories();
        categoryRadioButtons = new ArrayList<>();

        //Helper functions
        createCategoryRows();
        createPaymentSpinnerRows();
        populateInfoFields();
    }

    @Override
    protected int getLayoutResourceID()
    {
        return R.layout.activity_update_expense;
    }

    /*****************************************************************
     * Functionality Methods
     *****************************************************************/

    /*
     *  Converts user input to an Expense object.
     *
     *  Returns true on a successful save of the expense, false otherwise.
     */
    private boolean saveTransaction()
    {
        BigDecimal amount;
        Date date;
        Category category = null;
        NumberFormat format = NumberFormat.getCurrencyInstance();

        try
        {
            Double amountEntered = Double.parseDouble(amountEditText.getText().toString().replaceAll("[^\\d.]", ""));
            String formatted = format.format(amountEntered).replaceAll("[^\\d.]", "");
            amount = new BigDecimal(formatted);
            date = dateEditText.getText().toString().equals("Today") ? Calendar.getInstance().getTime() : new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA).parse(dateEditText.getText().toString());
        } catch (NumberFormatException e)
        {
            Toast.makeText(this, "Invalid Amount Format", Toast.LENGTH_LONG).show();
            return false;
        } catch (IllegalArgumentException e)
        {
            Toast.makeText(this, "Invalid Amount Entered", Toast.LENGTH_LONG).show();
            return false;
        } catch (ParseException e)
        {
            Toast.makeText(this, "Invalid Date Entered", Toast.LENGTH_LONG).show();
            return false;
        }

        if (locationEditText.getText().length() == 0)
        {
            Toast.makeText(this, "Please Enter a Location", Toast.LENGTH_LONG).show();
            return false;
        }

        String location = locationEditText.getText().toString();
        String note = noteEditText.getText().toString();

        foundCategory:
        for (RadioButton rb : categoryRadioButtons)
        {
            if (rb.isChecked())
            {
                String categoryName = rb.getText().toString();
                for (Category c : categories)
                {
                    if (c.getType().equals(categoryName))
                    {
                        category = c;
                        break foundCategory;
                    }
                }
            }
        }

        if (category == null)
        {
            Toast.makeText(this, "Please Select a Category", Toast.LENGTH_LONG).show();
            return false;
        }

        Expense.Builder builder = new Expense.Builder(date, amount, category, location).note(note);
        builder.paymentMethod(paymentSpinner.getSelectedItem().toString());
        builder.recurringPeriod(recurringSpinner.getSelectedItem().toString());
        return instance.addExpense(builder.build());
    }

    /*****************************************************************
     * ActionListeners
     *****************************************************************/

    public void delete(View view)
    {
        int expenseID = getIntent().getIntExtra(EXTRA_TRANSACTION_ID, -1);
        Expense expense = instance.getExpense(expenseID);

        if (expense != null)
        {
            if (instance.deleteExpense(expense))
            {

                Toast.makeText(this, "Transaction Deleted", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, LandingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else
            {
                Toast.makeText(this, "Transaction could not be deleted", Toast.LENGTH_LONG).show();
            }
        } else
        {
            Toast.makeText(this, "Transaction could not be deleted", Toast.LENGTH_LONG).show();
        }
    }

    public void cancel(View view)
    {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void update(View view)
    {
        int expenseID = getIntent().getIntExtra(EXTRA_TRANSACTION_ID, -1);
        Expense expense = instance.getExpense(expenseID);
        if (expense != null)
        {
            if (instance.deleteExpense(expense))
            {

                if (saveTransaction())
                {
                    Intent intent = new Intent(UpdateExpenseActivity.this, LandingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            } else
            {
                Toast.makeText(this, "Could not update transaction", Toast.LENGTH_LONG).show();
            }
        }
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
    @Override
    protected void populateInfoFields()
    {
        View.OnClickListener dateListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateExpenseActivity.this,
                        new DatePickerDialog.OnDateSetListener()
                        {

                            public void onDateSet(DatePicker datePicker, int year, int month, int day)
                            {
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

        int expenseID = getIntent().getIntExtra(EXTRA_TRANSACTION_ID, -1);
        Expense expense = instance.getExpense(expenseID);

        if (expense != null)
        {
            amountEditText.setText(getString(R.string.currency, expense.getAmount()));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
            dateEditText.setText(sdf.format(expense.getDate()));
            locationEditText.setText(expense.getLocation());
            noteEditText.setText(expense.getNote());
            dateEditText.setOnClickListener(dateListener);
            paymentSpinner.setSelection(paymentSpinnerAdapter.getPosition(expense.getPaymentMethod()));
            recurringSpinner.setSelection(recurringSpinnerAdapter.getPosition(expense.getRecurringPeriod()));

            if (expense.getCategory() != null)
            {
                String categoryTitle = expense.getCategory().getType();
                for (RadioButton rb : categoryRadioButtons)
                {
                    if (rb.getText().toString().equals(categoryTitle))
                    {
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
    private void createCategoryRows()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        LinearLayout scrollLinearLayout = categoryScrollView.findViewById(R.id.scrollLinearLayout);

        for (Category c : categories)
        {
            View item = layoutInflater.inflate(R.layout.category_select_view, scrollLinearLayout, false);
            item.findViewById(R.id.colorView).setBackgroundColor(c.getColor());

            RadioButton categoryRadioButton = item.findViewById(R.id.categoryRadioButton);
            categoryRadioButtons.add(categoryRadioButton);
            categoryRadioButton.setText(c.getType());
            categoryRadioButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    for (RadioButton rb : categoryRadioButtons)
                    {
                        if (rb != v)
                        {
                            rb.setChecked(false);
                        }
                    }
                }
            });

            scrollLinearLayout.addView(item);
            scrollLinearLayout.addView(createSeparatorView());
        }

        layoutInflater.inflate(R.layout.add_category_view, scrollLinearLayout, true);
    }

    /*
     *  OnClickListener for "Add Category..."
     *
     *  Creates a dialog (Alert Dialog) which allows the user to input a category name.
     *  Inflates a category_select_row_layout and inserts it above "add category..."
     */
    public void createAddCategoryDialog(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.add_category_dialog);
        builder.setTitle(R.string.add_category);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int ID)
            {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int ID)
            {
                AlertDialog categoryDialog = (AlertDialog) dialog;

                EditText categoryNameEditText = categoryDialog.findViewById(R.id.categoryNameEditText);
                int color = ((ColorDrawable) categoryDialog.findViewById(R.id.categoryColorView).getBackground()).getColor();
                Category category = instance.addCategory(categoryNameEditText.getText().toString(), color);

                if (category != null)
                {
                    LinearLayout scrollLinearLayout = categoryScrollView.findViewById(R.id.scrollLinearLayout);

                    View item = LayoutInflater.from(getApplicationContext()).inflate(R.layout.category_select_view, scrollLinearLayout, false);
                    item.findViewById(R.id.colorView).setBackgroundColor(category.getColor());

                    RadioButton categoryRadioButton = item.findViewById(R.id.categoryRadioButton);
                    categoryRadioButtons.add(categoryRadioButton);
                    categoryRadioButton.setText(category.getType());
                    categoryRadioButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            for (RadioButton rb : categoryRadioButtons)
                            {
                                if (rb != v)
                                {
                                    rb.setChecked(false);
                                }
                            }
                        }
                    });

                    scrollLinearLayout.addView(item, scrollLinearLayout.getChildCount() - 1);
                    scrollLinearLayout.addView(createSeparatorView(), scrollLinearLayout.getChildCount() - 1);

                    Toast.makeText(getApplicationContext(), "Category Added", Toast.LENGTH_LONG).show();
                } else
                {
                    Toast.makeText(getApplicationContext(), "Category Already Exists", Toast.LENGTH_LONG).show();
                }

                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        View categoryColorView = dialog.findViewById(R.id.categoryColorView);
        categoryColorView.setBackgroundColor(instance.getCurrentColor());
    }

    /*
     *  Helper function used to populate the recurring period spinner.
     */
    private void createPaymentSpinnerRows()
    {
        String paymentItems[] = new String[]{"Credit", "Debit", "Cash"};
        paymentSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentItems);
        paymentSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentSpinner.setAdapter(paymentSpinnerAdapter);
    }

    /*
    *  Helper function to create separators used in-between categories.
    */
    private View createSeparatorView()
    {
        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        line.setBackgroundColor(Color.LTGRAY);

        return line;
    }
}
