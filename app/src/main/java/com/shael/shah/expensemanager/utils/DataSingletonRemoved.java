package com.shael.shah.expensemanager.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.model.Category;
import com.shael.shah.expensemanager.model.Expense;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DataSingletonRemoved {

    private static final String SHAREDPREF_EXPENSES = "com.shael.shah.expensemanager.SHAREDPREF_EXPENSES";
    private static final String SHAREDPREF_CATEGORIES = "com.shael.shah.expensemanager.SHAREDPREF_CATEGORIES";
    private static final String SHAREDPREF_COLORS = "com.shael.shah.expensemanager.SHAREDPREF_COLORS";
    private static final String SHAREDPREF_TIME_PERIOD = "com.shael.shah.expensemanager.SHAREDPREF_TIME_PERIOD";
    private static final String SHAREDPREF_DISPLAY_OPTION = "com.shael.shah.expensemanager.SHAREDPREF_DISPLAY_OPTION";

//    @SuppressLint("StaticFieldLeak")
//    private static DataSingleton instance;
    private static int currentColor = 0;
    private Context context;
    private List<Expense> expenses;
    private List<Category> categories;
    private int[] colours;

    /*private DataSingleton(Context context) {
        this.context = context;

        expenses = getExpensesListFromSharedPreferences();
        categories = getCategoriesListFromSharedPreferences();
        colours = context.getResources().getIntArray(R.array.categoryColours);
        currentColour = getColorFromSharedPreferences();

        createRecurringExpenses();
    }*/

    /*public static DataSingleton initialize(Context context) {
        if (instance == null)
            instance = new DataSingleton(context.getApplicationContext());

        return instance;
    }*/

    /*public static DataSingleton getInstance() {
        if (instance != null)
            return instance;

        throw new IllegalArgumentException("DataSingleton has not been initialized");
    }*/

    public List<Expense> getExpenses() {
        return expenses;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public boolean checkCategory(String category) {
        for (Category c : categories) {
            if (c.getType().equals(category)) {
                return false;
            }
        }

        return true;
    }

    public void addExpense(Expense expense) {
        boolean added = false;

        if (!expenses.isEmpty()) {
            for (int i = 0; i < expenses.size(); i++) {
                if (expense.getDate().compareTo(expenses.get(i).getDate()) >= 0) {
                    expenses.add(i, expense);
                    added = true;
                    break;
                }
            }
        }

        if (!added)
            expenses.add(expense);
    }

    /*public boolean removeExpense(Expense expense) {
        for (Expense e : expenses) {
            if (e.equals(expense)) {
                expenses.remove(expense);
                return true;
            }
        }

        return false;
    }*/

    public Boolean addCategory(String category) {
        for (Category c : categories) {
            if (c.getType().equals(category)) {
                return false;
            }
        }

        categories.add(new Category(category, colours[currentColor++]));
        return true;
    }

    public void saveLists() {
        setSharedPreferences(expenses, SHAREDPREF_EXPENSES);
        setSharedPreferences(categories, SHAREDPREF_CATEGORIES);
        setSharedPreferenceColor(currentColor);
    }

    /*public List<Expense> getExpensesAfterDate(Date dateRange) {
        List<Expense> rangeExpenses = new ArrayList<>();
        for (Expense e : expenses) {
            if (e.getDate().compareTo(dateRange) > 0)
                rangeExpenses.add(e);
        }

        return rangeExpenses;
    }*/

    /*
     *  Iterates through all expenses to create copies of all expenses that are
     *  recurring depending on the current date.
     *
     *  Achieves this by checking if the recurring period for the expense is up and
     *  and creates a copy of the expense with the current date. Also, sets the original
     *  expense member field recurring to false to avoid duplicates.
     */
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

    private List<Expense> getExpensesListFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String json = sharedPreferences.getString(SHAREDPREF_EXPENSES, "");

        Type type = new TypeToken<List<Expense>>() {
        }.getType();
        List<Expense> expenses = gson.fromJson(json, type);

        if (expenses == null || expenses.isEmpty()) {
            return new ArrayList<>();
        }

        return expenses;
    }

    private List<Category> getCategoriesListFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String json = sharedPreferences.getString(SHAREDPREF_CATEGORIES, "");

        Type type = new TypeToken<List<Category>>() {
        }.getType();
        List<Category> categories = gson.fromJson(json, type);

        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }

        return categories;
    }

    private int getColorFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(SHAREDPREF_COLOURS, 0);
    }

    /*public TimePeriod getTimePeriodFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return TimePeriod.fromInteger(sharedPreferences.getInt(SHAREDPREF_TIME_PERIOD, 2));
    }

    public void setTimePeriodSharedPreference(TimePeriod timePeriod) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        prefEditor.putInt(SHAREDPREF_TIME_PERIOD, TimePeriod.toInteger(timePeriod));
        prefEditor.apply();
    }*/

    private void setSharedPreferences(List<?> list, String tag) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(list);

        prefEditor.putString(tag, json);
        prefEditor.apply();
    }

    private void setSharedPreferenceColor(int stopColor) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        prefEditor.putInt(SHAREDPREF_COLOURS, stopColor);
        prefEditor.apply();
    }

    public String getDisplayOptionFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(SHAREDPREF_DISPLAY_OPTION, "CIRCLE");
    }

    private void removeAllCategories() {
        categories.clear();
    }

    private void removeAllExpenses() {
        expenses.clear();
    }

    private void resetColor() {
        currentColour = 0;
    }

    public void reset() {
        removeAllExpenses();
        removeAllCategories();
        resetColor();
    }

    /*public enum TimePeriod {
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
    }*/
}
