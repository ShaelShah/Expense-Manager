package com.shael.shah.expensemanager.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

//import android.os.Parcel;
//import android.os.Parcelable;

@Entity
//public class Income implements Parcelable {
public class Income extends Transaction implements Serializable {

    /*public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };*/

    @PrimaryKey(autoGenerate = true)
    private int incomeID;

    public Income(Date date, BigDecimal amount, String location, String note, String recurringPeriod) {
        super(date, amount, location, note, recurringPeriod);
    }

    private Income(Builder builder) {
        super(builder.date, builder.amount, builder.location, builder.note, builder.recurringPeriod);
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

    public int getIncomeID() {
        return incomeID;
    }

    public void setIncomeID(int incomeID) {
        this.incomeID = incomeID;
    }

    public String toCSV() {
        return getDate().toString() + "," + getAmount().toString() + "," + getLocation() + "," + getNote() + "," + getRecurringPeriod();
    }

    @Override
    public boolean equals(Object inc) {
        return this.getIncomeID() == ((Income) inc).getIncomeID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getAmount(), getLocation(), getNote(), getRecurringPeriod());
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
        private final String location;

        private String note = "";
        private String recurringPeriod = "";

        public Builder(Date date, BigDecimal amount, String location) {
            this.date = date;
            this.amount = amount;
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

        public Income build() {
            return new Income(this);
        }
    }
}