package com.shael.shah.expensemanager;

import java.io.Serializable;
import java.util.Date;

public class Expense implements Serializable {

    private Date date;
    private double amount;
    private Category category;
    private String location;
    private String note;
    private boolean recurring;
    private boolean income;

    public Expense(Date date, double amount, Category category, String location, String note, boolean recurring, boolean income) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.location = location;
        this.note = note;
        this.recurring = recurring;
        this.income = income;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
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

    public void setAmount(double amount) {
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

    @Override
    public String toString() {
        return "Spent " + amount + " at " + location + " on " + date.toString();
    }
}
