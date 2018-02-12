package com.shael.shah.expensemanager.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.shael.shah.expensemanager.db.ApplicationDatabase;
import com.shael.shah.expensemanager.model.Category;
import com.shael.shah.expensemanager.model.Expense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DataSingleton {

    private static final String SHAREDPREF_SETTINGS = "com.shael.shah.expensemanager.SHAREDPREF_SETTINGS";
    private static final String SHAREDPREF_TIMEPERIOD = "com.shael.shah.expensemanager.SHAREDPREF_TIMEPERIOD";
    private static final String SHAREDPREF_DISPLAYOPTION = "com.shael.shah.expensemanager.SHAREDPREF_DISPLAYOPTION";
    private static final String SHAREDPREF_COLOR = "com.shael.shah.expensemanager.SHAREDPREF_COLORS";

    private Context context;

    @SuppressLint("StaticFieldLeak")
    private static DataSingleton instance;
    private ApplicationDatabase database;

    private List<Expense> expenses;
    private List<Category> categories;
    private TimePeriod timePeriod;
    private String displayOption;
    private int currentColour;

    private DataSingleton(Context context) {
        this.context = context;
        database = ApplicationDatabase.getInstance(context);
        getExpensesFromDatabase();
        getCategoriesFromDatabase();
        getSettingsFromSharedPref();
        createRecurringExpenses();
    }

    public static DataSingleton init(Context context) {
        if (instance == null)
            instance = new DataSingleton(context);

        return instance;
    }

    public static DataSingleton getInstance() {
        if (instance != null)
            return instance;

        throw new IllegalStateException("Database has not been initialized");
    }

    public static void destroyInstance() {
        instance = null;
    }

    private void getExpensesFromDatabase() {
        expenses = database.expenseDao().getAllExpenses();
    }

    private void getCategoriesFromDatabase() {
        categories = database.categoryDao().getAllCategories();
    }

    private void getSettingsFromSharedPref() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPREF_SETTINGS, Context.MODE_PRIVATE);
        timePeriod = TimePeriod.fromInteger(sharedPreferences.getInt(SHAREDPREF_TIMEPERIOD, 2));
        displayOption = sharedPreferences.getString(SHAREDPREF_DISPLAYOPTION, "CIRCLE");
        currentColour = sharedPreferences.getInt(SHAREDPREF_COLOR, 0);
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public int getCurrentColor() {
        return currentColour;
    }

    public TimePeriod getTimePeriod() {
        return timePeriod;
    }

    public String getDisplayOption() {
        return displayOption;
    }

    public void updateDatabase() {
        database.expenseDao().update(expenses);
        database.categoryDao().update(categories);
        updateSettings();
    }

    public void addCategory(Category category) {
        database.categoryDao().insert(category);
    }

    public void addExpense(Expense expense) {
        database.expenseDao().insert(expense);
    }

    public void deleteExpense(Expense expense) {
        database.expenseDao().delete(expense);
    }

    private void updateSettings() {
        SharedPreferences.Editor editor = context.getSharedPreferences(SHAREDPREF_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putInt(SHAREDPREF_TIMEPERIOD, TimePeriod.toInteger(timePeriod));
        editor.putString(SHAREDPREF_DISPLAYOPTION, displayOption);
        editor.putInt(SHAREDPREF_COLOR, currentColour);
        editor.apply();
    }

    private void createRecurringExpenses() {
        Calendar calendar = Calendar.getInstance();

        List<Expense> newExpenses = new ArrayList<>();
        for (Expense e : expenses) {
            if (!e.getRecurringPeriod().equals("None")) {
                calendar.setTime(e.getDate());

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
                            .income(e.isIncome())
                            .recurringPeriod(e.getRecurringPeriod())
                            .paymentMethod(e.getPaymentMethod())
                            .build();
                    e.setRecurringPeriod("None");
                    newExpenses.add(newExpense);
                }
            }
        }

        expenses.addAll(newExpenses);
    }

    public boolean checkCategory(String category) {
        for (Category c : categories) {
            if (c.getType().equals(category)) {
                return false;
            }
        }

        return true;
    }

    public enum TimePeriod {
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY,
        ALL;

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