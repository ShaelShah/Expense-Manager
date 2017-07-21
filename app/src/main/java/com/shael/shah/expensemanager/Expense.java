package com.shael.shah.expensemanager;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

class Expense implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };

    private Date date;
    private BigDecimal amount;
    private Category category;
    private String location;
    private String note;
    private boolean recurring;
    private boolean income;
    private String recurringPeriod;
    private String paymentMethod;

    private Expense(Builder builder) {
        this.date = builder.date;
        this.amount = builder.amount;
        this.category = builder.category;
        this.location = builder.location;
        this.note = builder.note;
        this.recurring = builder.recurring;
        this.income = builder.income;
        this.recurringPeriod = builder.recurringPeriod;
        this.paymentMethod = builder.paymentMethod;
    }

    //private Expense(Date date, BigDecimal amount, Category category, String location, String note, boolean recurring, boolean income, String recurringPeriod, String paymentMethod) {
    //    this.date = date;
    //    this.amount = amount;
    //    this.category = category;
    //    this.location = location;
    //    this.note = note;
    //    this.recurring = recurring;
    //    this.income = income;
    //    this.recurringPeriod = recurringPeriod;
    //    this.paymentMethod = paymentMethod;
    //}

    private Expense(Parcel parcel) {
        this.date = new Date(parcel.readLong());
        this.amount = new BigDecimal(parcel.readString());
        this.category = parcel.readParcelable(Category.class.getClassLoader());
        this.location = parcel.readString();
        this.note = parcel.readString();
        this.recurring = parcel.readInt() != 0;
        this.income = parcel.readInt() != 0;
        this.recurringPeriod = parcel.readString();
        this.paymentMethod = parcel.readString();
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

    String getPaymentMethod() {
        return paymentMethod;
    }

    String printCSV() {
        return date.toString() + "," + amount.toString() + "," + category.getType() + "," + location + "," + note + "," + recurring + "," + income + "," + recurringPeriod + "," + paymentMethod;
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
                && this.isRecurring() == expense.isRecurring()
                && this.isIncome() == expense.isIncome()
                && this.getLocation().equals(expense.getLocation())
                && this.getNote().equals(expense.getNote())
                && this.getRecurringPeriod().equals(expense.getRecurringPeriod())
                && this.getPaymentMethod().equals(expense.getPaymentMethod()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, amount, category, location, note, recurring, income, recurringPeriod, paymentMethod);
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(date.getTime());
        parcel.writeString(amount.toString());
        parcel.writeParcelable(category, flags);
        parcel.writeString(location);
        parcel.writeString(note);
        parcel.writeInt(recurring ? 1 : 0);
        parcel.writeInt(income ? 1 : 0);
        parcel.writeString(recurringPeriod);
        parcel.writeString(paymentMethod);
    }

    @Override
    public int describeContents() {
        return 0;
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