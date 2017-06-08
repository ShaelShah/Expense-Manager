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
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    /*****************************************************************
    * Private Variables
    ******************************************************************/

    private static final String EXTRA_CATEGORY_TITLE = "com.shael.shah.expensemanager.EXTRA_CATEGORY_TITLE";
    private static final String EXTRA_EXPENSE_TYPE = "com.shael.shah.expensemanager.EXTRA_EXPENSE_TYPE";
    TimePeriod timePeriod = TimePeriod.MONTHLY;
    //TODO: Temporary solution
    List<View> colorBoxViews = new ArrayList<>();
    private List<Expense> expenses;
    private List<Category> categories;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        populateMoneyTextViews();
        populateMainCategoryRows();

        //Workaround to delete all expenses/categories programmatically
        //Singleton.getInstance(this).reset();

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {

            startAnimations();

//            if (!colorBoxViews.isEmpty()) {
//                for (View v : colorBoxViews) {
//                    scaleView(v, 0f, 1f);
//                }
//            }
        }
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

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View view = findViewById(R.id.add_item);

                if (view != null) {

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
                                        intent.putExtra(EXTRA_EXPENSE_TYPE, "Income");
                                        startActivity(intent);
                                    } else {
                                        intent.putExtra(EXTRA_EXPENSE_TYPE, "Recurring");
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
                Intent addExpenseIntent = new Intent(this, AddExpenseActivity.class);
                addExpenseIntent.putExtra(EXTRA_EXPENSE_TYPE, "Normal");
                startActivity(addExpenseIntent);
                return true;

            case R.id.open_menu:
                return true;

            case R.id.open_settings:
                Intent openSettingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(openSettingsIntent);

            /*
            case R.id.add_normal:
                intent.putExtra(EXTRA_EXPENSE_TYPE, "Normal");
                //startActivity(intent);
                return true;

            case R.id.add_income:
                intent.putExtra(EXTRA_EXPENSE_TYPE, "Income");
                //startActivity(intent);
                return true;

            case R.id.add_recurring:
                intent.putExtra(EXTRA_EXPENSE_TYPE, "Recurring");
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

    /*
     *  Iterates through all expenses and returns an List<Expense> of all expenses that fall
     *  within the current time period.
     */
    private List<Expense> getDateRangeExpenses() {

        Calendar currentCal = Calendar.getInstance();
        Calendar oldCal = Calendar.getInstance();

        List<Expense> tempExpenses = new ArrayList<>();

        switch (timePeriod) {
            case DAILY:
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
                return tempExpenses;

            case WEEKLY:
                for (Expense e : expenses) {
                    oldCal.setTime(e.getDate());
                    if (currentCal.get(Calendar.YEAR) == oldCal.get(Calendar.YEAR)) {
                        if (currentCal.get(Calendar.WEEK_OF_YEAR) == oldCal.get(Calendar.WEEK_OF_YEAR)) {
                            tempExpenses.add(e);
                        }
                    }
                }
                return tempExpenses;

            case MONTHLY:
                for (Expense e : expenses) {
                    oldCal.setTime(e.getDate());
                    if (currentCal.get(Calendar.YEAR) == oldCal.get(Calendar.YEAR)) {
                        if (currentCal.get(Calendar.MONTH) == oldCal.get(Calendar.MONTH)) {
                            tempExpenses.add(e);
                        }
                    }
                }
                return tempExpenses;

            case YEARLY:
                for (Expense e : expenses) {
                    oldCal.setTime(e.getDate());
                    if (currentCal.get(Calendar.YEAR) == oldCal.get(Calendar.YEAR)) {
                        tempExpenses.add(e);
                    }
                }
                return tempExpenses;

            default:
                return expenses;
        }
    }

    /*
     *  Sets the timePeriodTextView to the current date range
     */
    private void setDateRangeTextView() {
        switch (timePeriod) {
            case DAILY:
                timePeriodTextView.setText(R.string.today);
                break;

            case WEEKLY:
                timePeriodTextView.setText(R.string.weekly);
                break;

            case MONTHLY:
                timePeriodTextView.setText(new SimpleDateFormat("MMMM", Locale.CANADA).format(Calendar.getInstance().getTime()));
                break;

            case YEARLY:
                timePeriodTextView.setText(new SimpleDateFormat("YYYY", Locale.CANADA).format(Calendar.getInstance().getTime()));
                break;

            default:
                timePeriodTextView.setText(R.string.all);
                break;
        }
    }

    /*
     *  Finds the color bar for each category and performs an animation starting from 0
     *  horizontally and ending at a percentage of how much that category makes up the
     *  net total spent, all scaled to the highest contributor set to 100%.
     */
    private void startAnimations() {
        LinearLayout scrollLinearLayout = (LinearLayout) mainCategoryScrollView.findViewById(R.id.mainScrollLinearLayout);

        List<View> colorBoxViews = new ArrayList<>();
        List<Float> ratioFloats = new ArrayList<>();

        if (scrollLinearLayout != null) {
            LinearLayout displayCategoryLinearLayout;

            for (int i = 0; i < scrollLinearLayout.getChildCount(); i++) {
                displayCategoryLinearLayout = (LinearLayout) scrollLinearLayout.getChildAt(i);
                View colorBox = displayCategoryLinearLayout.getChildAt(0);
                LinearLayout displayCategoryInformationLinearLayout = (LinearLayout) displayCategoryLinearLayout.getChildAt(1);
                TextView categoryRowAmount = (TextView) displayCategoryInformationLinearLayout.getChildAt(1);

                float amount = Float.parseFloat(categoryRowAmount.getText().toString().replaceAll("[^\\d.]", ""));
                float net = Float.parseFloat(netTextView.getText().toString().replaceAll("[^\\d.]", ""));

                colorBoxViews.add(colorBox);
                ratioFloats.add(amount / net);
            }

            if (!colorBoxViews.isEmpty() && !ratioFloats.isEmpty()) {
                if (colorBoxViews.size() == ratioFloats.size()) {
                    float max = Collections.max(ratioFloats);

                    for (int i = 0; i < colorBoxViews.size(); i++) {
                        scaleView(colorBoxViews.get(i), 0f, ratioFloats.get(i) / max);
                    }
                }
            }
        }
    }

    /*
     *  Horizontal expansion of a view.
     */
    private void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(startScale, endScale, 1f, 1f);
        anim.setFillAfter(true);
        anim.setDuration(2000);
        v.startAnimation(anim);
    }

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

    /*
     *  Iterates through all expenses passed in through the List<Expense> parameter
     *  to calculate how much was spent, how much was earned and the net total.
     *  Sets the views accordingly.
     */
    private void populateMoneyTextViews() {
        BigDecimal income = new BigDecimal(0);
        BigDecimal outcome = new BigDecimal(0);
        BigDecimal net;

        setDateRangeTextView();
        for (Expense e : getDateRangeExpenses()) {
            if (e.isIncome())
                income = income.add(e.getAmount());
            else
                outcome = outcome.add(e.getAmount());
        }

        net = income.subtract(outcome);

        incomeTexView.setText(getString(R.string.currency, income));
        expensesTextView.setText(getString(R.string.currency, outcome));
        netTextView.setText(getString(R.string.currency, net.abs()));

        //noinspection deprecation
        int color = net.signum() > 0 ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red);
        netTextView.setTextColor(color);
    }

    /*
     *  Iterates through all of the expenses passed in through the List<Expense> parameter
     *  to provide a list of all expenses sorted by category.
     *
     *  Inflates a category_select_row_layout for each category.
     *  Initially removes all child views from the parent.
     */
    private void populateMainCategoryRows() {
        //TODO: This function can be better optimized, instead of looping through all expenses for each category, loop through the expenses once and assign temporary lists for each category
        LinearLayout scrollLinearLayout = (LinearLayout) mainCategoryScrollView.findViewById(R.id.mainScrollLinearLayout);

        if (scrollLinearLayout.getChildCount() > 0) {
            scrollLinearLayout.removeAllViews();
        }

        setDateRangeTextView();
        List<Expense> tempExpenses = getDateRangeExpenses();

        for (Category c : categories) {

            String title = c.getType();
            BigDecimal amount = new BigDecimal(0);

            for (Expense e : tempExpenses) {
                if (!e.isIncome() && e.getCategory().getType().equals(title)) {
                    amount = amount.add(e.getAmount());
                }
            }

            if (amount.signum() > 0) {
                //TODO: Look into View.inflate method (specifically the 3rd parameter)
                View item = View.inflate(this, R.layout.category_display_row_layout, null);

                View colorBox = item.findViewById(R.id.mainColorView);
                colorBox.setBackgroundColor(c.getColor());
                colorBoxViews.add(colorBox);

                TextView categoryRowTitle = (TextView) item.findViewById(R.id.categoryRowTitle);
                categoryRowTitle.setText(title);
                categoryRowTitle.setTextColor(c.getColor());

                TextView categoryRowAmount = (TextView) item.findViewById(R.id.categoryRowAmount);
                categoryRowAmount.setTextColor(c.getColor());

                categoryRowAmount.setText(getString(R.string.currency, amount));
                scrollLinearLayout.addView(item);

                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String categoryTitle = ((TextView) v.findViewById(R.id.categoryRowTitle)).getText().toString();
                        Intent intent = new Intent(MainActivity.this, CategoryExpenses.class);
                        intent.putExtra(EXTRA_CATEGORY_TITLE, categoryTitle);
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
                intent.putExtra(EXTRA_CATEGORY_TITLE, "Income");
                startActivity(intent);
            }
        });

        expensesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryExpenses.class);
                intent.putExtra(EXTRA_CATEGORY_TITLE, "Expenses");
                startActivity(intent);
            }
        });

        netTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryExpenses.class);
                intent.putExtra(EXTRA_CATEGORY_TITLE, "Net Total");
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

                        switch (checkedId) {
                            case R.id.dailyRadioButton:
                                timePeriod = TimePeriod.DAILY;
                                timePeriodTextView.setText(R.string.today);
                                break;

                            case R.id.weeklyRadioButton:
                                timePeriod = TimePeriod.WEEKLY;
                                timePeriodTextView.setText(R.string.weekly);
                                break;

                            case R.id.monthlyRadioButton:
                                timePeriod = TimePeriod.MONTHLY;
                                timePeriodTextView.setText(new SimpleDateFormat("MMMM", Locale.CANADA).format(Calendar.getInstance().getTime()));
                                break;

                            case R.id.yearlyRadioButton:
                                timePeriod = TimePeriod.YEARLY;
                                timePeriodTextView.setText(new SimpleDateFormat("YYYY", Locale.CANADA).format(Calendar.getInstance().getTime()));
                                break;

                            default:
                                timePeriod = TimePeriod.ALL;
                                timePeriodTextView.setText(R.string.all);
                                break;
                        }

                        populateMainCategoryRows();
                        populateMoneyTextViews();
                        //TODO: Is a final okay here?
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private enum TimePeriod {
        DAILY, WEEKLY, MONTHLY, YEARLY, ALL
    }
}
