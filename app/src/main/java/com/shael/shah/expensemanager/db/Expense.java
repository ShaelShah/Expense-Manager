package com.shael.shah.expensemanager.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Expense {

    @PrimaryKey(autoGenerate = true)
    private int expenseID;

    @ColumnInfo(name = "date")
    private Date date;
    @ColumnInfo(name = "amount")
    private BigDecimal amount;
    @Embedded
    private Category category;
    @ColumnInfo(name = "location")
    private String location;
    @ColumnInfo(name = "note")
    private String note;
    @ColumnInfo(name = "income")
    private boolean income;
    @ColumnInfo(name = "recurring_period")
    private String recurringPeriod;
    @ColumnInfo(name = "payment_method")
    private String paymentMethod;

    public Expense(Date date, BigDecimal amount, Category category, String location, String note, boolean income, String recurringPeriod, String paymentMethod) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.location = location;
        this.note = note;
        this.income = income;
        this.recurringPeriod = recurringPeriod;
        this.paymentMethod = paymentMethod;
    }

    private Expense(Builder builder) {
        this.date = builder.date;
        this.amount = builder.amount;
        this.category = builder.category;
        this.location = builder.location;
        this.note = builder.note;
        this.income = builder.income;
        this.recurringPeriod = builder.recurringPeriod;
        this.paymentMethod = builder.paymentMethod;
    }

    public int getExpenseID() {
        return expenseID;
    }

    public void setExpenseID(int expenseID) {
        this.expenseID = expenseID;
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

    public boolean isIncome() {
        return income;
    }

    public void setIncome(boolean income) {
        this.income = income;
    }

    public String getRecurringPeriod() {
        return recurringPeriod;
    }

    public void setRecurringPeriod(String recurringPeriod) {
        this.recurringPeriod = recurringPeriod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public static class Builder {

        private final Date date;
        private final BigDecimal amount;
        private final Category category;
        private final String location;

        private String note = "";
        private boolean recurring = false;
        private boolean income = false;
        private String recurringPeriod = "";
        private String paymentMethod = "";

        public Builder(Date date, BigDecimal amount, Category category, String location) {
            this.date = date;
            this.amount = amount;
            this.category = category;
            this.location = location;
        }

        public Builder note(String note) {
            this.note = note;
            return this;
        }

        public Builder recurring(boolean recurring) {
            this.recurring = recurring;
            return this;
        }

        public Builder income(boolean income) {
            this.income = income;
            return this;
        }

        public Builder recurringPeriod(String recurringPeriod) {
            this.recurringPeriod = recurringPeriod;
            return this;
        }

        public Builder paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Expense build() {
            return new Expense(this);
        }
    }

}
