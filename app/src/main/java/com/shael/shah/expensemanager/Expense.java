package com.shael.shah.expensemanager;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Expense implements Serializable {

    private Date date;
    private BigDecimal amount;
    private Category category;
    private String location;
    private String note;
    private boolean recurring;
    private boolean income;
    private String recurringPeriod;

    //TODO: Use a builder instead of the classical constructor.
    public Expense(Date date, BigDecimal amount, Category category, String location, String note, boolean recurring, boolean income, String recurringPeriod) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.location = location;
        this.note = note;
        this.recurring = recurring;
        this.income = income;
        this.recurringPeriod = recurringPeriod;
    }

    public Expense(Date date, BigDecimal amount, String category, String location, String note, boolean recurring, boolean income, String recurringPeriod) {
        this(date, amount, new Category(category), location, note, recurring, income, recurringPeriod);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public boolean isIncome() {
        return income;
    }

    public void setIncome(boolean income) {
        this.income = income;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getRecurringPeriod() {
        return recurringPeriod;
    }

    public void setRecurringPeriod(String recurringPeriod) {
        this.recurringPeriod = recurringPeriod;
    }

    @Override
    public String toString() {
        //TODO: toString is ugly, probably shouldn't be using it.
        if (income)
            return "Earned " + amount + " at " + location + " on " + date.toString();
        else
            return "Spent " + amount + " at " + location + " on " + date.toString();
    }
}
