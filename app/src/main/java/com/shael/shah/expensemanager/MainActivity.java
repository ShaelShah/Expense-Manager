package com.shael.shah.expensemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    /*****************************************************************
    * Private Variables
    ******************************************************************/

    private List<Expense> expenses;
    private List<Category> categories;

    //TODO: Maybe this should be a local variable
    private Toolbar toolbar;
    private TextView timePeriodTextView;
    private TextView netTextView;
    private TextView incomeTexView;
    private TextView expensesTextView;
    private ScrollView mainCategoryScrollView;
    private RadioGroup dateRangeRadioGroup;

    /*****************************************************************
     * Lifecycle Methods
     *****************************************************************/

    /*
     *  Initial method called by the system during app startup.
     *  Responsible for getting a copy of all expenses and categories.
     *  Also responsible for setting up of the initial GUI.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        //Find views to work with during this activity
        timePeriodTextView = (TextView) findViewById(R.id.timePeriodTextView);
        netTextView = (TextView) findViewById(R.id.netTextView);
        incomeTexView = (TextView) findViewById(R.id.incomeTextView);
        expensesTextView = (TextView) findViewById(R.id.expensesTextView);
        mainCategoryScrollView = (ScrollView) findViewById(R.id.mainCategoryScrollView);

        //Helper functions
        getLists();
        setActionListeners();
        createRecurringExpenses();
        populateMoneyTextViews(expenses);
        populateMainCategoryRows(expenses);

        //Workaround to delete all expenses/categories programmatically
        //Singleton.getInstance(this).reset()

        //Workaround to add categories programmatically
        //Singleton.getInstance(this).addCategory("Food");
        //Singleton.getInstance(this).addCategory("Transportation");
        //Singleton.getInstance(this).addCategory("Entertainment");
    }

    @Override
    protected void onPause() {
        super.onPause();
        setLists();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLists();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLists();
    }

    @Override
    protected void onStop() {
        super.onStop();
        setLists();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setLists();
    }

    /*****************************************************************
     * Menu Methods
     *****************************************************************/

    /*
     *  Method called by Android to set up layout for the toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        /*
        View v = new View(this);
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Your long click listener callback logic goes here
                Toast.makeText(getApplicationContext(), "Long pressed", Toast.LENGTH_LONG).show();
                return false;
            }

        });
        menu.getItem(0).setActionView(v); // add long press to first item
        */

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View view = findViewById(R.id.add_item);

                if (view != null) {
                    /*
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                        }
                    });
                    */
                    view.setOnLongClickListener(new View.OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {

                            PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
                            popupMenu.inflate(R.menu.add_expense_popup_menu);

                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                                    if (item.getTitle().equals("Add Income")) {
                                        intent.putExtra("ExpenseType", "Income");
                                        startActivity(intent);
                                    } else {
                                        intent.putExtra("ExpenseType", "Recurring");
                                        startActivity(intent);
                                    }
                                    return true;
                                }
                            });

                            popupMenu.show();
                            return true;
                        }
                    });
                }
            }
        });

        return true;
    }

    /*
     *  Method called by Android to handle all menu operations.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_item:
                Intent intent = new Intent(this, AddExpenseActivity.class);
                intent.putExtra("ExpenseType", "Normal");
                startActivity(intent);
                return true;

            case R.id.open_menu:
                return true;

            /*
            case R.id.add_normal:
                intent.putExtra("ExpenseType", "Normal");
                //startActivity(intent);
                return true;

            case R.id.add_income:
                intent.putExtra("ExpenseType", "Income");
                //startActivity(intent);
                return true;

            case R.id.add_recurring:
                intent.putExtra("ExpenseType", "Recurring");
                //startActivity(intent);
                return true;
            */

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*****************************************************************
     * Functionality Methods
     *****************************************************************/

    /*
     *  Retrieves all expenses and categories and assigns them to the
     *  corresponding global variables.
     */
    private void getLists() {
        expenses = Singleton.getInstance(this).getExpenses();
        categories = Singleton.getInstance(this).getCategories();
    }

    /*
     *  Saves all expenses and categories to sharedPreferences.
     */
    private void setLists() {
        Singleton.getInstance(this).saveLists();
    }

    /*
     *  Iterates through all expenses to create copies of all expenses that are
     *  recurring depending on the current date.
     *
     *  Achieves this by checking if the recurring period for the expense is up and
     *  and creates a copy of the expense with the current date. Also, sets the original
     *  expense member field recurring to false to avoid duplicates.
     */
    private void createRecurringExpenses() {
        //TODO: This function needs to be tested.
        //TODO: There is probably a better way to implement this function.
        //TODO: Check out the Joda-Time library.
        Calendar calendar = Calendar.getInstance();

        for (Expense e : expenses) {
            if (e.isRecurring()) {
                calendar.setTime(e.getDate());

                switch (e.getRecurringPeriod()) {
                    case "Daily":
                        calendar.add(Calendar.DATE, 1);
                        break;
                    case "Weekly":
                        calendar.add(Calendar.DATE, 7);
                        break;
                    case "Monthly":
                        calendar.add(Calendar.MONTH, 1);
                        break;
                    case "Yearly":
                        calendar.add(Calendar.YEAR, 1);
                        break;
                }

                if (calendar.getTime().compareTo(e.getDate()) < 0) {
                    Expense newExpense = new Expense(calendar.getTime(), e.getAmount(), e.getCategory(), e.getLocation(), e.getNote(), true, e.isIncome(), e.getRecurringPeriod());
                    Singleton.getInstance(this).addExpense(newExpense);
                    e.setRecurring(false);
                }
            }
        }
    }

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

    /*
     *  Iterates through all expenses passed in through the List<Expense> parameter
     *  to calculate how much was spent, how much was earned and the net total.
     *  Sets the views accordingly.
     */
    private void populateMoneyTextViews(List<Expense> expenses) {
        BigDecimal income = new BigDecimal(0);
        BigDecimal outcome = new BigDecimal(0);
        BigDecimal net;

        for (Expense e : expenses) {
            if (e.isIncome()) {
                income = income.add(e.getAmount());
            } else {
                outcome = outcome.add(e.getAmount());
            }
        }

        //TODO: There should be a better way to do this
        net = income.subtract(outcome);

        //TODO: Concatenations should not be used with setText
        incomeTexView.setText("$" + income);
        expensesTextView.setText("$" + outcome);
        netTextView.setText("$" + net.abs());

        if (net.signum() > 0) {
            //TODO: use new non-deprecated getColor methods
            netTextView.setTextColor(getResources().getColor(R.color.green));
        } else {
            netTextView.setTextColor(getResources().getColor(R.color.red));
        }
    }

    /*
     *  Iterates through all of the expenses passed in through the List<Expense> parameter
     *  to provide a list of all expenses sorted by category.
     *
     *  Inflates a category_select_row_layout for each category.
     *  Initially removes all child views from the parent.
     */
    private void populateMainCategoryRows(List<Expense> expenses) {
        LinearLayout scrollLinearLayout = (LinearLayout) mainCategoryScrollView.findViewById(R.id.mainScrollLinearLayout);

        if (scrollLinearLayout.getChildCount() > 0) {
            scrollLinearLayout.removeAllViews();
        }

        for (Category c : categories) {

            String title = c.getType();
            BigDecimal amount = new BigDecimal(0);
            for (Expense e : expenses) {
                if (!e.isIncome() && e.getCategory().getType().equals(title)) {
                    amount = amount.add(e.getAmount());
                }
            }

            if (amount.signum() > 0) {
                //TODO: Look into View.inflate method (specifically the 3rd parameter)
                View item = View.inflate(this, R.layout.category_display_row_layout, null);

                //TODO: Color should be set dynamically and uniquely for each category
                View colorBox = item.findViewById(R.id.mainColorView);
                colorBox.setBackgroundColor(c.getColor());

                TextView categoryRowTitle = (TextView) item.findViewById(R.id.categoryRowTitle);
                categoryRowTitle.setText(title);
                categoryRowTitle.setTextColor(c.getColor());

                TextView categoryRowAmount = (TextView) item.findViewById(R.id.categoryRowAmount);
                categoryRowAmount.setTextColor(c.getColor());

                //TODO: Concatenations should not be used with setText
                categoryRowAmount.setText("$" + amount);
                scrollLinearLayout.addView(item);

                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String categoryTitle = ((TextView) v.findViewById(R.id.categoryRowTitle)).getText().toString();
                        Intent intent = new Intent(MainActivity.this, CategoryExpenses.class);
                        intent.putExtra("CategoryTitle", categoryTitle);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    /*****************************************************************
     * Setup ActionListeners Methods
     *****************************************************************/

    /*
     *  Sets up all action listeners to be used during this activity.
     */
    private void setActionListeners() {
        incomeTexView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryExpenses.class);
                //TODO: putExtra key is not up to Android Coding standard
                intent.putExtra("CategoryTitle", "Income");
                startActivity(intent);
            }
        });

        expensesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryExpenses.class);
                //TODO: putExtra key is not up to Android Coding standard
                intent.putExtra("CategoryTitle", "Expenses");
                startActivity(intent);
            }
        });

        netTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryExpenses.class);
                //TODO: putExtra key is not up to Android Coding standard
                intent.putExtra("CategoryTitle", "Net Total");
                startActivity(intent);
            }
        });

        /*
         *  Calculates the date range of the expenses to show.
         *  Currently only works for the current day, week, month or year.
         *  Does not allow for custom date ranges.
         */
        timePeriodTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(inflater.inflate(R.layout.date_range_dialog_layout, null));
                final AlertDialog dialog = builder.create();
                dialog.show();

                dateRangeRadioGroup = (RadioGroup) dialog.findViewById(R.id.dateRangeRadioGroup);
                dateRangeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        Calendar currentCal = Calendar.getInstance();
                        Calendar oldCal = Calendar.getInstance();

                        List<Expense> tempExpenses = new ArrayList<>();

                        switch (checkedId) {
                            case R.id.dailyRadioButton:
                                for (Expense e : expenses) {
                                    oldCal.setTime(e.getDate());
                                    if (currentCal.get(Calendar.YEAR) == oldCal.get(Calendar.YEAR)) {
                                        if (currentCal.get(Calendar.MONTH) == oldCal.get(Calendar.MONTH)) {
                                            if (currentCal.get(Calendar.DAY_OF_MONTH) == oldCal.get(Calendar.DAY_OF_MONTH)) {
                                                tempExpenses.add(e);
                                            }
                                        }
                                    }
                                }
                                timePeriodTextView.setText("Today");
                                break;

                            case R.id.weeklyRadioButton:
                                for (Expense e : expenses) {
                                    oldCal.setTime(e.getDate());
                                    if (currentCal.get(Calendar.YEAR) == oldCal.get(Calendar.YEAR)) {
                                        if (currentCal.get(Calendar.WEEK_OF_YEAR) == oldCal.get(Calendar.WEEK_OF_YEAR)) {
                                            tempExpenses.add(e);
                                        }
                                    }
                                }
                                timePeriodTextView.setText("Weekly");
                                break;

                            case R.id.monthlyRadioButton:
                                for (Expense e : expenses) {
                                    oldCal.setTime(e.getDate());
                                    if (currentCal.get(Calendar.YEAR) == oldCal.get(Calendar.YEAR)) {
                                        if (currentCal.get(Calendar.MONTH) == oldCal.get(Calendar.MONTH)) {
                                            tempExpenses.add(e);
                                        }
                                    }
                                }
                                timePeriodTextView.setText(new SimpleDateFormat("MMMM", Locale.CANADA).format(currentCal.getTime()));
                                break;

                            case R.id.yearlyRadioButton:
                                for (Expense e : expenses) {
                                    oldCal.setTime(e.getDate());
                                    if (currentCal.get(Calendar.YEAR) == oldCal.get(Calendar.YEAR)) {
                                        tempExpenses.add(e);
                                    }
                                }
                                timePeriodTextView.setText(new SimpleDateFormat("YYYY", Locale.CANADA).format(currentCal.getTime()));
                                break;

                            default:
                                tempExpenses = expenses;
                                timePeriodTextView.setText("All");
                                break;
                        }

                        populateMainCategoryRows(tempExpenses);
                        populateMoneyTextViews(tempExpenses);
                        dialog.dismiss();
                    }
                });
            }
        });
    }
}
