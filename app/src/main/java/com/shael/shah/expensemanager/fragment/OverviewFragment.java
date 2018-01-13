package com.shael.shah.expensemanager.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.activity.DisplayExpensesActivity;
import com.shael.shah.expensemanager.db.AppDatabase;
import com.shael.shah.expensemanager.db.Expense;
//import com.shael.shah.expensemanager.activity.DisplayExpensesActivity;
//import com.shael.shah.expensemanager.model.Expense;
//import com.shael.shah.expensemanager.utils.DataSingleton;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OverviewFragment extends Fragment {

    /*****************************************************************
     * Private Variables
     ******************************************************************/

    private static final String EXTRA_EXPENSES_DISPLAY = "com.shael.shah.expensemanager.EXTRA_EXPENSES_DISPLAY";
    private static final String EXTRA_EXPENSES_TITLE = "com.shael.shah.expensemanager.EXTRA_EXPENSES_TITLE";
    private static final String EXTRA_EXPENSE_LIST = "com.shael.shah.expensemanager.EXTRA_EXPENSE_LIST";
    private static final String SHAREDPREF_DISPLAY_OPTION = "com.shael.shah.expensemanager.SHAREDPREF_DISPLAY_OPTION";
    private static final String SHAREDPREF_TIME_PERIOD = "com.shael.shah.expensemanager.SHAREDPREF_TIME_PERIOD";

    private AppDatabase appDatabase;

    private TimePeriod timePeriod;
//    private String displayExpensesOption;
    private List<Expense> expenses;

    private TextView timePeriodTextView;
    private TextView netTextView;
    private TextView incomeTexView;
    private TextView expensesTextView;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appDatabase = AppDatabase.getAppDatabase(getActivity().getApplicationContext());

        //Helper functions
        getLists();
        createRecurringExpenses();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        //Find views to work with during this activity
        timePeriodTextView = (TextView) view.findViewById(R.id.timePeriodTextView);
        netTextView = (TextView) view.findViewById(R.id.netTextView);
        incomeTexView = (TextView) view.findViewById(R.id.incomeTextView);
        expensesTextView = (TextView) view.findViewById(R.id.expensesTextView);

        timePeriod = getTimePeriodFromSharedPreferences();
//        displayExpensesOption = getDisplayOptionFromSharedPreferences();

        //setActionListeners();
        populateMoneyTextViews();
        setDateRangeTextView();

        if (savedInstanceState == null) {
            //Fragment fragment = displayExpensesOption.equalsIgnoreCase("CIRCLE") ? new SegmentsFragment() : new BarsFragment();
            Fragment fragment = new SegmentsFragment();
            //Bundle bundle = new Bundle();
            //bundle.putParcelableArrayList(EXTRA_EXPENSE_LIST, (ArrayList<Expense>) getDateRangeExpenses());
            //fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().add(R.id.displayExpensesAnimationFrameLayout, fragment).commit();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getLists();
    }

    @Override
    public void onStop() {
        super.onStop();
        setLists();
        setTimePeriodSharedPreference(timePeriod);
    }

    /*****************************************************************
     * Functionality Methods
     *****************************************************************/

    /*
     *  Retrieves all expenses and categories and assigns them to the
     *  corresponding global variables.
     */
    private void getLists() {
        //expenses = DataSingleton.getInstance(getActivity()).getExpenses();
        expenses = appDatabase.expenseDao().getAllExpenses();
    }

    /*
     *  Saves all expenses and categories to sharedPreferences.
     */
    private void setLists() {
        //DataSingleton.getInstance(getActivity()).saveLists();
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
        //TODO: There is probably a better way to implement this function (Joda-Time).
        Calendar calendar = Calendar.getInstance();

        List<Expense> newExpenses = new ArrayList<>();
        for (Expense e : expenses) {
            if (!e.getRecurringPeriod().equals("None")) {
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
                    e.setRecurringPeriod("None");
                    newExpenses.add(newExpense);
                }
            }
        }

        for (Expense e : newExpenses) {
            appDatabase.expenseDao().insert(e);
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

                Intent intent = new Intent(getActivity(), DisplayExpensesActivity.class);
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

                Intent intent = new Intent(getActivity(), DisplayExpensesActivity.class);
                intent.putExtra(EXTRA_EXPENSES_DISPLAY, tempExpenses);
                intent.putExtra(EXTRA_EXPENSES_TITLE, "Expenses");
                startActivity(intent);
            }
        });

        netTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DisplayExpensesActivity.class);
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
        /*timePeriodTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                        if (displayExpensesOption.equalsIgnoreCase("CIRCLE")) {
                            SegmentsFragment fragment = (SegmentsFragment) getFragmentManager().findFragmentById(R.id.displayExpensesAnimationFrameLayout);
                            if (fragment != null)
                                fragment.updateExpenses(getDateRangeExpenses());
                        } else {
                            BarsFragment fragment = (BarsFragment) getFragmentManager().findFragmentById(R.id.displayExpensesAnimationFrameLayout);
                            if (fragment != null)
                                fragment.updateExpenses(getDateRangeExpenses());
                        }
                        populateMoneyTextViews();
                        dialog.dismiss();
                    }
                });
            }
        });*/
    }

    /*****************************************************************
     * Get/Set Shared Preferences
     *****************************************************************/

    private TimePeriod getTimePeriodFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return TimePeriod.fromInteger(sharedPreferences.getInt(SHAREDPREF_TIME_PERIOD, 2));
    }

    private void setTimePeriodSharedPreference(TimePeriod timePeriod) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        prefEditor.putInt(SHAREDPREF_TIME_PERIOD, TimePeriod.toInteger(timePeriod));
        prefEditor.apply();
    }

    private String getDisplayOptionFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPreferences.getString(SHAREDPREF_DISPLAY_OPTION, "CIRCLE");
    }

    private enum TimePeriod {
        DAILY, WEEKLY, MONTHLY, YEARLY, ALL;

        public static TimePeriod fromInteger(int x) {
            switch (x) {
                case 0:
                    return DAILY;
                case 1:
                    return WEEKLY;
                case 2:
                    return MONTHLY;
                case 3:
                    return YEARLY;
                case 4:
                    return ALL;
            }
            return MONTHLY;
        }

        public static int toInteger(TimePeriod timePeriod) {
            switch (timePeriod) {
                case DAILY:
                    return 0;
                case WEEKLY:
                    return 1;
                case MONTHLY:
                    return 2;
                case YEARLY:
                    return 3;
                case ALL:
                    return 4;
            }
            return 2;
        }
    }
}
