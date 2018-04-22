package com.shael.shah.expensemanager.fragment.display;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.activity.display.DisplayAllTransactionsActivity;
import com.shael.shah.expensemanager.activity.display.DisplayExpensesActivity;
import com.shael.shah.expensemanager.activity.display.DisplayIncomesActivity;
import com.shael.shah.expensemanager.fragment.ui.SegmentsFragment;
import com.shael.shah.expensemanager.model.Expense;
import com.shael.shah.expensemanager.model.Income;
import com.shael.shah.expensemanager.utils.DataSingleton;
import com.shael.shah.expensemanager.utils.DataSingleton.TimePeriod;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OverviewFragment extends Fragment
{

    /*****************************************************************
     * Private Variables
     ******************************************************************/

    private static final String EXTRA_TRANSACTION_DATE = "com.shael.shah.expensemanager.EXTRA_TRANSACTION_DATE";

    private DataSingleton instance;
    private SegmentsFragment segmentFragment;

    private TimePeriod timePeriod;
    private List<Expense> expenses;
    private List<Income> incomes;

    private TextView timePeriodTextView;
    private TextView netTextView;
    private TextView incomeTexView;
    private TextView expensesTextView;

    /*****************************************************************
     * Lifecycle Methods
     *****************************************************************/

    /*
     *  Initial method called by the system during app startup.
     *  Responsible for getting a copy of all expenses and categories.
     *  Also responsible for setting up of the initial GUI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        //Find views to work with during this activity
        timePeriodTextView = view.findViewById(R.id.timePeriodTextView);
        netTextView = view.findViewById(R.id.netTextView);
        incomeTexView = view.findViewById(R.id.incomeTextView);
        expensesTextView = view.findViewById(R.id.expensesTextView);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        instance = DataSingleton.getInstance();
        expenses = instance.getExpenses();
        incomes = instance.getIncomes();
        timePeriod = instance.getTimePeriod();

        setActionListeners();
        populateMoneyTextViews();

        if (savedInstanceState == null)
        {
            Bundle bundle = new Bundle();
            bundle.putSerializable(EXTRA_TRANSACTION_DATE, getDateRange());
            segmentFragment = new SegmentsFragment();
            segmentFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().add(R.id.displayExpensesAnimationFrameLayout, segmentFragment).commit();
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        instance.setTimePeriod(timePeriod);
    }

    /*****************************************************************
     * Functionality Methods
     *****************************************************************/

    /*
     *  Iterates through all expenses and returns an List<Expense> of all expenses that fall
     *  within the current time period.
     */
    private Date getDateRange()
    {

        Calendar calendar = Calendar.getInstance();
        switch (timePeriod)
        {
            case DAILY:
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                return calendar.getTime();

            case WEEKLY:
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                return calendar.getTime();

            case MONTHLY:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                return calendar.getTime();

            case YEARLY:
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                return calendar.getTime();

            default:
                calendar.set(Calendar.YEAR, 1);
                return calendar.getTime();
        }
    }


    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

    public void updateDisplay()
    {
        populateMoneyTextViews();
        segmentFragment.updateExpenses(getDateRange());
    }

    /*
     *  Sets the timePeriodTextView to the current date range
     *  Iterates through all expenses passed in through the List<Expense> parameter
     *  to calculate how much was spent, how much was earned and the net total.
     *  Sets the views accordingly.
     */
    private void populateMoneyTextViews()
    {
        BigDecimal income = new BigDecimal(0);
        BigDecimal outcome = new BigDecimal(0);
        BigDecimal net;

        switch (timePeriod)
        {
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

        Date afterDate = getDateRange();
        for (Expense e : expenses)
        {
            if (e.getDate().compareTo(afterDate) >= 0)
            {
                outcome = outcome.add(e.getAmount());
            }
        }

        for (Income i : incomes)
        {
            if (i.getDate().compareTo(afterDate) >= 0)
            {
                income = income.add(i.getAmount());
            }
        }

        net = income.subtract(outcome);
        incomeTexView.setText(getString(R.string.currency, income));
        expensesTextView.setText(getString(R.string.currency, outcome));
        netTextView.setText(getString(R.string.currency, net.abs()));

        int color = net.signum() > 0 ? ContextCompat.getColor(getActivity(), R.color.green) : ContextCompat.getColor(getActivity(), R.color.red);
        netTextView.setTextColor(color);
    }

    /*****************************************************************
     * Setup ActionListeners Methods
     *****************************************************************/

    /*
     *  Sets up all action listeners to be used during this activity.
     */
    private void setActionListeners()
    {
        incomeTexView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), DisplayIncomesActivity.class);
                intent.putExtra(EXTRA_TRANSACTION_DATE, getDateRange().getTime());
                startActivity(intent);
            }
        });

        expensesTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), DisplayExpensesActivity.class);
                intent.putExtra(EXTRA_TRANSACTION_DATE, getDateRange().getTime());
                startActivity(intent);
            }
        });

        netTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), DisplayAllTransactionsActivity.class);
                intent.putExtra(EXTRA_TRANSACTION_DATE, getDateRange().getTime());
                startActivity(intent);
            }
        });

        /*
         *  Calculates the date range of the expenses to show.
         *  Currently only works for the current day, week, month or year.
         *  Does not allow for custom date ranges.
         */
        timePeriodTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Date Range");
                builder.setView(R.layout.date_range_dialog);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        RadioGroup radioGroup = ((AlertDialog) dialogInterface).findViewById(R.id.dateRangeRadioGroup);
                        switch (radioGroup.getCheckedRadioButtonId())
                        {
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

                        if (segmentFragment != null)
                            segmentFragment.updateExpenses(getDateRange());

                        populateMoneyTextViews();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
