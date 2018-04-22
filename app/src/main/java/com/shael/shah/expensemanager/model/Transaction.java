package com.shael.shah.expensemanager.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Transaction
{

    @ColumnInfo
    private Date date;

    @ColumnInfo
    private BigDecimal amount;

    @ColumnInfo
    private String location;

    @ColumnInfo
    private String note;

    @ColumnInfo
    private String recurringPeriod;

    Transaction(Date date, BigDecimal amount, String location, String note, String recurringPeriod)
    {
        this.date = date;
        this.amount = amount;
        this.location = location;
        this.note = note;
        this.recurringPeriod = recurringPeriod;
    }

    public Date getDate()
    {
        return date;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public String getLocation()
    {
        return location;
    }

    public String getNote()
    {
        return note;
    }

    public String getRecurringPeriod()
    {
        return recurringPeriod;
    }

    public void setRecurringPeriod(String recurringPeriod)
    {
        this.recurringPeriod = recurringPeriod;
    }
}
