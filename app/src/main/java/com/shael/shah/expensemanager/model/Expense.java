package com.shael.shah.expensemanager.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
//import android.os.Parcel;
//import android.os.Parcelable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Entity
//public class Expense implements Parcelable {
public class Expense implements Serializable {

    /*public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };*/

    @PrimaryKey(autoGenerate = true)
    private int expenseID;

    @ColumnInfo
    private Date date;

    @ColumnInfo
    private BigDecimal amount;

    @Embedded
    private Category category;

    @ColumnInfo
    private String location;

    @ColumnInfo
    private String note;

    @ColumnInfo
    private boolean income;

    @ColumnInfo
    private String recurringPeriod;

    @ColumnInfo
    private String paymentMethod;

    @Ignore
    private boolean insert = false;

    @Ignore
    private boolean update = false;

    @Ignore
    private boolean delete = false;

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
        date = builder.date;
        amount = builder.amount;
        category = builder.category;
        location = builder.location;
        note = builder.note;
        income = builder.income;
        recurringPeriod = builder.recurringPeriod;
        paymentMethod = builder.paymentMethod;
        insert = true;
    }

    /*private Expense(Parcel parcel) {
        this.date = new Date(parcel.readLong());
        this.amount = new BigDecimal(parcel.readString());
        this.category = parcel.readParcelable(Category.class.getClassLoader());
        this.location = parcel.readString();
        this.note = parcel.readString();
        this.income = parcel.readInt() != 0;
        this.recurringPeriod = parcel.readString();
        this.paymentMethod = parcel.readString();
    }*/

    public int getExpenseID() {
        return expenseID;
    }

    public void setExpenseID(int expenseID) {
        this.expenseID = expenseID;
    }

    public Date getDate() {
        return date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public boolean isIncome() {
        return income;
    }

    public Category getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public String getNote() {
        return note;
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

    public boolean isInsert() {
        return insert;
    }

    public boolean isUpdate() {
        return update;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setInsert(boolean insert) {
        this.insert = insert;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public String toCSV() {
        if (category == null)
            return date.toString() + "," + amount.toString() + "," + "," + location + "," + note + "," + income + "," + recurringPeriod + "," + paymentMethod;

        return date.toString() + "," + amount.toString() + "," + category.getType() + "," + location + "," + note + "," + income + "," + recurringPeriod + "," + paymentMethod;
    }

    @Override
    public boolean equals(Object exp) {
        if (this == exp)
            return true;

        if (this.getClass() != exp.getClass())
            return false;

        Expense expense = (Expense) exp;
        if (this.getCategory() == null && expense.getCategory() != null)
            return false;

        if (this.getCategory() != null)
            if (!this.getCategory().equals(expense.getCategory()))
                return false;

        return (this.getDate().compareTo(expense.getDate()) == 0
                && this.getAmount().compareTo(expense.getAmount()) == 0
                && this.isIncome() == expense.isIncome()
                && this.getLocation().equals(expense.getLocation())
                && this.getNote().equals(expense.getNote())
                && this.getRecurringPeriod().equals(expense.getRecurringPeriod())
                && this.getPaymentMethod().equals(expense.getPaymentMethod()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, amount, category, location, note, income, recurringPeriod, paymentMethod);
    }

    /*@Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(date.getTime());
        parcel.writeString(amount.toString());
        parcel.writeParcelable(category, flags);
        parcel.writeString(location);
        parcel.writeString(note);
        parcel.writeInt(income ? 1 : 0);
        parcel.writeString(recurringPeriod);
        parcel.writeString(paymentMethod);
    }

    @Override
    public int describeContents() {
        return 0;
    }*/

    public static class Builder {

        private final Date date;
        private final BigDecimal amount;
        private final Category category;
        private final String location;

        private String note = "";
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