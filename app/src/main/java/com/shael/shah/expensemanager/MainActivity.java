package com.shael.shah.expensemanager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    private static final String EXTRA_EXPENSES_DISPLAY = "com.shael.shah.expensemanager.EXTRA_EXPENSES_DISPLAY";
    private static final String EXTRA_EXPENSES_TITLE = "com.shael.shah.expensemanager.EXTRA_EXPENSES_TITLE";
    private static final String EXTRA_EXPENSE_TYPE = "com.shael.shah.expensemanager.EXTRA_EXPENSE_TYPE";
    private static final String EXTRA_EXPENSE_LIST = "com.shael.shah.expensemanager.EXTRA_EXPENSE_LIST";

    //TODO: This should be a sharedPreference
    TimePeriod timePeriod = TimePeriod.MONTHLY;

    private List<Expense> expenses;

    private TextView timePeriodTextView;
    private TextView netTextView;
    private TextView incomeTexView;
    private TextView expensesTextView;
    private RadioGroup dateRangeRadioGroup;

    //TODO: This should be a sharedPreference
    private String displayExpensesOption = "CIRCLE";

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainActivityToolbar);
        setActionBar(toolbar);

        //Find views to work with during this activity
        timePeriodTextView = (TextView) findViewById(R.id.timePeriodTextView);
        netTextView = (TextView) findViewById(R.id.netTextView);
        incomeTexView = (TextView) findViewById(R.id.incomeTextView);
        expensesTextView = (TextView) findViewById(R.id.expensesTextView);

        //Helper functions
        getLists();
        createRecurringExpenses();
        setActionListeners();
        populateMoneyTextViews();
        setDateRangeTextView();

        if (savedInstanceState == null) {
            Fragment fragment = displayExpensesOption.equals("CIRCLE") ? new SegmentsFragment() : new BarsFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(EXTRA_EXPENSE_LIST, (ArrayList<Expense>) getDateRangeExpenses());
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().add(R.id.fragmentFrameLayout, fragment).commit();
        }

        //Workaround to delete all expenses/categories programmatically
        //Singleton.getInstance(this).reset();

        //Workaround to add categories programmatically
        //Singleton.getInstance(this).addCategory("Food");
        //Singleton.getInstance(this).addCategory("Drinks");
        //Singleton.getInstance(this).addCategory("Transportation");
        //Singleton.getInstance(this).addCategory("Entertainment");
        //Singleton.getInstance(this).addCategory("Groceries");
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLists();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
                                    } else if (item.getTitle().equals("Add Recurring Expense")) {
                                        intent.putExtra(EXTRA_EXPENSE_TYPE, "Recurring");
                                        startActivity(intent);
                                    } else {
                                        return false;
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
                return false;

            case R.id.open_settings:
                Intent openSettingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(openSettingsIntent);
                return true;

            case R.id.save_csv:
                backupToCSV();

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
        //TODO: There is probably a better way to implement this function (Joda-Time).
        Calendar calendar = Calendar.getInstance();

        List<Expense> newExpenses = new ArrayList<>();
        for (Expense e : expenses) {
            if (e.isRecurring()) {
                calendar.setTime(e.getDate());

                //TODO: Bi-weekly should also be an option (maybe even custom ranges).
                switch (e.getRecurringPeriod()) {
                    case "Daily":
                        calendar.add(Calendar.DATE, 1);
                        break;
                    case "Weekly":
                        calendar.add(Calendar.DATE, 7);
                        break;
                    case "Bi-Weekly":
                        calendar.add(Calendar.DATE, 14);
                        break;
                    case "Monthly":
                        calendar.add(Calendar.MONTH, 1);
                        break;
                    case "Yearly":
                        calendar.add(Calendar.YEAR, 1);
                        break;
                }

                if (Calendar.getInstance().getTime().compareTo(calendar.getTime()) > 0) {
                    Expense newExpense = new Expense.Builder(calendar.getTime(), e.getAmount(), e.getCategory(), e.getLocation())
                            .note(e.getNote())
                            .recurring(true)
                            .income(e.isIncome())
                            .recurringPeriod(e.getRecurringPeriod())
                            .paymentMethod(e.getPaymentMethod())
                            .build();
                    e.setRecurring(false);
                    newExpenses.add(newExpense);
                }
            }
        }

        for (Expense e : newExpenses) {
            Singleton.getInstance(this).addExpense(e);
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

        //TODO: Bi-weekly should also be an option (maybe even custom ranges).
        //TODO: Can some of this code be refractored?
        //TODO: Check out Joda-Time library.
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

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

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

    private void backupToCSV() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Expense Manager";
        String filename = "Backup - " + Calendar.getInstance().getTime().toString() + ".csv";

        try {
            File fPath = new File(path);
            if (!fPath.exists()) {
                fPath.mkdirs();
            }

            File f = new File(path + File.separator + filename);
            if (!f.exists() || f.isDirectory()) {
                f.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(f, true);

            fileWriter.append("Date,Amount,Category,Location,Note,Recurring,Income,Recurring Period,Payment Method\n");
            fileWriter.write("Incomes\n");
            for (Expense e : expenses) {
                if (e.isIncome())
                    fileWriter.append(expenseToCSV(e) + "\n");
            }

            fileWriter.append("\nExpenses\n");
            for (Expense e : expenses) {
                if (!e.isIncome())
                    fileWriter.append(expenseToCSV(e) + "\n");
            }

            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not backup to CSV", Toast.LENGTH_LONG).show();
        }
    }

    private String expenseToCSV(Expense e) {
        if (e.getCategory() != null)
            return e.getDate().toString() + ","
                    + e.getAmount().toString() + ","
                    + e.getCategory().getType() + ","
                    + e.getLocation() + ","
                    + e.getNote() + ","
                    + e.isRecurring() + ","
                    + e.isIncome() + ","
                    + e.getRecurringPeriod() + ","
                    + e.getPaymentMethod();
        else
            return e.getDate().toString() + ","
                    + e.getAmount().toString() + ","
                    + "" + ","
                    + e.getLocation() + ","
                    + e.getNote() + ","
                    + e.isRecurring() + ","
                    + e.isIncome() + ","
                    + e.getRecurringPeriod() + ","
                    + e.getPaymentMethod();
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
                List<Expense> dateRangeExpenses = getDateRangeExpenses();
                ArrayList<Expense> tempExpenses = new ArrayList<>();
                for (Expense e : dateRangeExpenses) {
                    if (e.isIncome())
                        tempExpenses.add(e);
                }

                Intent intent = new Intent(MainActivity.this, DisplayExpensesActivity.class);
                intent.putExtra(EXTRA_EXPENSES_DISPLAY, tempExpenses);
                intent.putExtra(EXTRA_EXPENSES_TITLE, "Incomes");
                startActivity(intent);
            }
        });

        expensesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Expense> dateRangeExpenses = getDateRangeExpenses();
                ArrayList<Expense> tempExpenses = new ArrayList<>();
                for (Expense e : dateRangeExpenses) {
                    if (!e.isIncome())
                        tempExpenses.add(e);
                }

                Intent intent = new Intent(MainActivity.this, DisplayExpensesActivity.class);
                intent.putExtra(EXTRA_EXPENSES_DISPLAY, tempExpenses);
                intent.putExtra(EXTRA_EXPENSES_TITLE, "Expenses");
                startActivity(intent);
            }
        });

        netTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DisplayExpensesActivity.class);
                intent.putExtra(EXTRA_EXPENSES_DISPLAY, (ArrayList<Expense>) getDateRangeExpenses());
                intent.putExtra(EXTRA_EXPENSES_TITLE, "All Transactions");
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

                        if (displayExpensesOption.equals("CIRCLE")) {
                            SegmentsFragment fragment = (SegmentsFragment) getFragmentManager().findFragmentById(R.id.fragmentFrameLayout);
                            if (fragment != null)
                                fragment.updateExpenses(getDateRangeExpenses());
                        } else {
                            BarsFragment fragment = (BarsFragment) getFragmentManager().findFragmentById(R.id.fragmentFrameLayout);
                            if (fragment != null)
                                fragment.updateExpenses(getDateRangeExpenses());
                        }
                        populateMoneyTextViews();
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
