package com.shael.shah.expensemanager.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Transaction implements Serializable {

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

    @Ignore
    private boolean insert = false;

    @Ignore
    private boolean update = false;

    @Ignore
    private boolean delete = false;

    Transaction(Date date, BigDecimal amount, String location, String note, String recurringPeriod) {
        this.date = date;
        this.amount = amount;
        this.location = location;
        this.note = note;
        this.recurringPeriod = recurringPeriod;
    }

    public Date getDate() {
        return date;
    }

    public BigDecimal getAmount() {
        return amount;
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

    public boolean isInsert() {
        return insert;
    }

    public void setInsert(boolean insert) {
        this.insert = insert;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
