package com.shael.shah.expensemanager.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.db.ApplicationDatabase;
import com.shael.shah.expensemanager.model.Category;
import com.shael.shah.expensemanager.model.Expense;
import com.shael.shah.expensemanager.model.Income;

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

    // Singleton instances
    @SuppressLint("StaticFieldLeak")
    private static DataSingleton instance;
    private static ApplicationDatabase database;
    private Context context;

    // Objects
    private List<Expense> expenses;
    private List<Income> incomes;
    private List<Category> categories;

    // Settings
    private TimePeriod timePeriod;
    private String displayOption;
    private int currentColor;
    private int[] colors;

    private DataSingleton(Context context) {
        this.context = context;

        // Get database instance
        database = ApplicationDatabase.getInstance(context);

        // Get objects from database
        expenses = getExpensesFromDatabase();
        incomes = getIncomesFromDatabase();
        categories = getCategoriesFromDatabase();

        // Get settings from shared preferences
        colors = context.getResources().getIntArray(R.array.categoryColors);
        getSettingsFromSharedPref();

        // Initialization work
        createRecurringExpenses();
        createRecurringIncomes();
    }

    /*****************************************************************
     * Constructors
     ******************************************************************/

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
        database.destroyInstance();
    }

    public void reset() {
        database.incomeDao().deleteAll(incomes);
        database.expenseDao().deleteAll(expenses);
        database.categoryDao().deleteAll(categories);

        incomes.clear();
        expenses.clear();
        categories.clear();
        currentColor = 0;

        updateSettings();
    }

    /*****************************************************************
     * Database Access Methods
     ******************************************************************/

    private List<Expense> getExpensesFromDatabase() {
        return database.expenseDao().getAllExpenses();
    }

    private List<Income> getIncomesFromDatabase() {
        return database.incomeDao().getAllIncomes();
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

    public Expense getExpense(int expenseID) {
        for (Expense e : expenses) {
            if (e.getExpenseID() == expenseID) {
                return e;
            }
        }

        return null;
    }

    public List<Income> getIncomes() {
        return incomes;
    }

    public Income getIncome(int incomeID) {
        for (Income i : incomes) {
            if (i.getIncomeID() == incomeID) {
                return i;
            }
        }

        return null;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Category getCategory(int categoryID) {
        for (Category c : categories) {
            if (c.getCategoryID() == categoryID) {
                return c;
            }
        }

        return null;
    }

    // Get settings for use in application
    public TimePeriod getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getDisplayOption() {
        return displayOption;
    }

    public int getCurrentColor() {
        return colors[currentColor++];
    }

    /******************************************************************
     * Object Interaction Methods
     ******************************************************************/

    public void addExpense(Expense expense) {
        expenses.add(expense);
        database.expenseDao().insert(expense);
    }

    public void deleteExpense(Expense expense) {
        expenses.remove(expense);
        database.expenseDao().delete(expense);
    }

    public void deleteAllExpensesFromCategory(Category category) {
        List<Expense> exDelete = new ArrayList<>();
        for (Expense e : expenses) {
            if (e.getCategory().equals(category)) {
                exDelete.add(e);
            }
        }

        for (Expense e : exDelete) {
            deleteExpense(e);
        }
    }

    public void addIncome(Income income) {
        incomes.add(income);
        database.incomeDao().insert(income);
    }

    public void deleteIncome(Income income) {
        incomes.remove(income);
        database.incomeDao().delete(income);
    }

    public Category addCategory(String category) {
        return addCategory(category, colors[currentColor++]);
    }

    public Category addCategory(String category, int color) {
        if (checkCategory(category)) {
            Category.Builder builder = new Category.Builder(category, color);
            Category cat = builder.build();
            categories.add(cat);
            database.categoryDao().insert(cat);
            return cat;
        }

        return null;
    }

    public boolean updateCategory(Category category, String type, int color) {
        if (!type.isEmpty() && checkCategory(type)) {
            category.setType(type);
            category.setColor(color);
            database.categoryDao().update(category);
            return true;
        }

        return false;
    }

    public void deleteCategory(Category category) {
        categories.remove(category);
        database.categoryDao().delete(category);
    }

    /******************************************************************
     * Shutdown Update Methods
     ******************************************************************/

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
        if (category.isEmpty())
            return false;

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
                            .recurringPeriod(e.getRecurringPeriod())
                            .paymentMethod(e.getPaymentMethod())
                            .build();
                    e.setRecurringPeriod("None");
                    newExpenses.add(newExpense);
                }
            }
        }

        expenses.addAll(newExpenses);
        database.expenseDao().insertAll(newExpenses);
    }

    private void createRecurringIncomes() {
        Calendar calendar = Calendar.getInstance();

        List<Income> newIncomes = new ArrayList<>();
        for (Income i : incomes) {
            if (!i.getRecurringPeriod().equals("None")) {
                calendar.setTime(i.getDate());

                switch (i.getRecurringPeriod()) {
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
                    Income newIncome = new Income.Builder(calendar.getTime(), i.getAmount(), i.getLocation())
                            .note(i.getNote())
                            .recurringPeriod(i.getRecurringPeriod())
                            .build();
                    i.setRecurringPeriod("None");
                    newIncomes.add(newIncome);
                }
            }
        }

        incomes.addAll(newIncomes);
        database.incomeDao().insertAll(newIncomes);
    }

    /******************************************************************
     * Fragment Enum
     ******************************************************************/

    public enum LandingFragment {
        OVERVIEW, SETTINGS
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