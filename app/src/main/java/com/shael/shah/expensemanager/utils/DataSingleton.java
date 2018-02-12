package com.shael.shah.expensemanager.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.db.ApplicationDatabase;
import com.shael.shah.expensemanager.model.Category;
import com.shael.shah.expensemanager.model.Expense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DataSingleton {

    /*****************************************************************
     * Private Variables
     ******************************************************************/

    private static final String SHAREDPREF_SETTINGS = "com.shael.shah.expensemanager.SHAREDPREF_SETTINGS";
    private static final String SHAREDPREF_TIMEPERIOD = "com.shael.shah.expensemanager.SHAREDPREF_TIMEPERIOD";
    private static final String SHAREDPREF_DISPLAYOPTION = "com.shael.shah.expensemanager.SHAREDPREF_DISPLAYOPTION";
    private static final String SHAREDPREF_COLOR = "com.shael.shah.expensemanager.SHAREDPREF_COLORS";

    private Context context;

    // Singleton instances
    @SuppressLint("StaticFieldLeak")
    private static DataSingleton instance;
    private static ApplicationDatabase database;

    // Objects
    private List<Expense> expenses;
    private List<Category> categories;

    // Settings
    private TimePeriod timePeriod;
    private String displayOption;
    private int currentColor;
    private int[] colors;

    /*****************************************************************
     * Constructors
     ******************************************************************/

    public static DataSingleton init(Context context) {
        if (instance == null)
            instance = new DataSingleton(context);

        return instance;
    }

    private DataSingleton(Context context) {
        this.context = context;

        // Get database instance
        database = ApplicationDatabase.getInstance(context);

        // Get objects from database
        expenses = getExpensesFromDatabase();
        categories = getCategoriesFromDatabase();

        // Get settings from shared preferences
        colors = context.getResources().getIntArray(R.array.categoryColors);
        getSettingsFromSharedPref();

        // Initialization work
        createRecurringExpenses();
    }

    public static DataSingleton getInstance() {
        if (instance != null)
            return instance;

        throw new IllegalStateException("Database has not been initialized");
    }

    public static void destroyInstance() {
        instance = null;
        database = null;
    }

    /*****************************************************************
     * Database Access Methods
     ******************************************************************/

    private List<Expense> getExpensesFromDatabase() {
        return database.expenseDao().getAllExpenses();
    }

    private List<Category> getCategoriesFromDatabase() {
        return database.categoryDao().getAllCategories();
    }

    /*****************************************************************
     * SharedPreferences Access Methods
     ******************************************************************/

    private void getSettingsFromSharedPref() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPREF_SETTINGS, Context.MODE_PRIVATE);
        timePeriod = TimePeriod.fromInteger(sharedPreferences.getInt(SHAREDPREF_TIMEPERIOD, 2));
        displayOption = sharedPreferences.getString(SHAREDPREF_DISPLAYOPTION, "CIRCLE");
        currentColor = sharedPreferences.getInt(SHAREDPREF_COLOR, 0);
    }

    /*****************************************************************
     * Objects/Settings Access Methods
     ******************************************************************/

    public List<Expense> getExpenses() {
        return expenses;
    }

    public List<Category> getCategories() {
        return categories;
    }

    // Get settings for use in application
    public TimePeriod getTimePeriod() {
        return timePeriod;
    }

    public String getDisplayOption() {
        return displayOption;
    }

    /******************************************************************
     * Object Interaction Methods
     ******************************************************************/

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public void deleteExpense(Expense expense) {
        if (expense.isInsert()) {
            expense.setInsert(false);
            return;
        }

        if (expense.isUpdate()) {
            expense.setUpdate(false);
            return;
        }

        expense.setDelete(true);
    }

    public void updateExpense(Expense toDelete, Expense toAdd) {
        deleteExpense(toDelete);
        addExpense(toAdd);
    }

    public boolean addCategory(String category) {
        if (checkCategory(category)) {
            Category.Builder builder = new Category.Builder(category, colors[currentColor++]);
            categories.add(builder.build());
            return true;
        }

        return false;
    }

    /******************************************************************
     * Shutdown Update Methods
     ******************************************************************/

    public void updateDatabase() {
        for (Expense e : expenses) {
            if (e.isInsert())
                database.expenseDao().insert(e);

            if (e.isUpdate())
                database.expenseDao().update(e);

            if (e.isDelete())
                database.expenseDao().delete(e);
        }

        for (Category c : categories) {
            if (c.isInsert())
                database.categoryDao().insert(c);
        }
    }

    public void updateSettings() {
        SharedPreferences.Editor editor = context.getSharedPreferences(SHAREDPREF_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putInt(SHAREDPREF_TIMEPERIOD, TimePeriod.toInteger(timePeriod));
        editor.putString(SHAREDPREF_DISPLAYOPTION, displayOption);
        editor.putInt(SHAREDPREF_COLOR, currentColor);
        editor.apply();
    }

    /******************************************************************
     * Private Helper Methods
     ******************************************************************/

    private boolean checkCategory(String category) {
        for (Category c : categories) {
            if (c.getType().equals(category)) {
                return false;
            }
        }

        return true;
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

    /******************************************************************
     * TimePeriod Enum
     ******************************************************************/

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