package com.shael.shah.expensemanager;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

class Expense implements Serializable {

    private Date date;
    private BigDecimal amount;
    private Category category;
    private String location;
    private String note;
    private boolean recurring;
    private boolean income;
    private String recurringPeriod;

    //TODO: Use a builder instead of the classical constructor
    Expense(Date date, BigDecimal amount, Category category, String location, String note, boolean recurring, boolean income, String recurringPeriod) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.location = location;
        this.note = note;
        this.recurring = recurring;
        this.income = income;
        this.recurringPeriod = recurringPeriod;
    }

    Date getDate() {
        return date;
    }

    BigDecimal getAmount() {
        return amount;
    }

    boolean isRecurring() {
        return recurring;
    }

    void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    boolean isIncome() {
        return income;
    }

    Category getCategory() {
        return category;
    }

    String getLocation() {
        return location;
    }

    String getNote() {
        return note;
    }

    String getRecurringPeriod() {
        return recurringPeriod;
    }

    @Override
    public boolean equals(Object exp) {
        if (this == exp)
            return true;

        Expense expense = (Expense) exp;
        if (this.getCategory() == null && expense.getCategory() != null)
            return false;

        if (this.getCategory() != null)
            if (!this.getCategory().equals(expense.getCategory()))
                return false;

        return (this.getDate().compareTo(expense.getDate()) == 0
                && this.getAmount().compareTo(expense.getAmount()) == 0
                && this.isRecurring() == expense.isRecurring()
                && this.isIncome() == expense.isIncome()
                && this.getLocation().equals(expense.getLocation())
                && this.getNote().equals(expense.getNote())
                && this.getRecurringPeriod().equals(expense.getRecurringPeriod()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, amount, category, location, note, recurring, income, recurringPeriod);
    }
}