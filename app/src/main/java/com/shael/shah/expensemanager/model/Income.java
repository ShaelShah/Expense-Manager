package com.shael.shah.expensemanager.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Entity
public class Income extends Transaction {

    @PrimaryKey(autoGenerate = true)
    private int incomeID;

    public Income(Date date, BigDecimal amount, String location, String note, String recurringPeriod) {
        super(date, amount, location, note, recurringPeriod);
    }

    private Income(Builder builder) {
        super(builder.date, builder.amount, builder.location, builder.note, builder.recurringPeriod);
        setInsert(true);
    }

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
        if (this == inc)
            return true;

        if (this.getClass() != inc.getClass())
            return false;

        Income income = (Income) inc;
        return (this.getDate().compareTo(income.getDate()) == 0
                && this.getAmount().compareTo(income.getAmount()) == 0
                && this.getLocation().equals(income.getLocation())
                && this.getNote().equals(income.getNote())
                && this.getRecurringPeriod().equals(income.getRecurringPeriod()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getAmount(), getLocation(), getNote(), getRecurringPeriod());
    }

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