package com.shael.shah.expensemanager.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

//import android.os.Parcel;
//import android.os.Parcelable;

@Entity
//public class Expense implements Parcelable {
public class Expense extends Transaction implements Serializable {

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

    @Embedded
    private Category category;

    @ColumnInfo
    private String paymentMethod;

    public Expense(Date date, BigDecimal amount, Category category, String location, String note, String recurringPeriod, String paymentMethod) {
        super(date, amount, location, note, recurringPeriod);

        this.category = category;
        this.paymentMethod = paymentMethod;
    }

    private Expense(Builder builder) {
        super(builder.date, builder.amount, builder.location, builder.note, builder.recurringPeriod);

        category = builder.category;
        paymentMethod = builder.paymentMethod;
        setInsert(true);
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

    public Category getCategory() {
        return category;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String toCSV() {
        return getDate().toString() + "," + getAmount().toString() + "," + category.getType() + "," + getLocation() + "," + getNote() + "," + getRecurringPeriod() + "," + paymentMethod;
    }

    @Override
    public boolean equals(Object exp) {
        return this.getExpenseID() == ((Expense) exp).getExpenseID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getAmount(), category, getLocation(), getNote(), getRecurringPeriod(), paymentMethod);
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