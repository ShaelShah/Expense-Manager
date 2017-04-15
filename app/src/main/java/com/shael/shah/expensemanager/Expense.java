package com.shael.shah.expensemanager;

import java.util.Date;

/**
 * Created by root on 15/04/17.
 */

public class Expense {

    private Date date;
    private double amount;
    private Category category;
    private String location;
    private String note;

    public Expense(Date date, double amount, Category category, String location, String note) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.location = location;
        this.note = note;
    }

    public Expense(double amount, String location) {
        this.amount = amount;
        this.location = location;
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
}
