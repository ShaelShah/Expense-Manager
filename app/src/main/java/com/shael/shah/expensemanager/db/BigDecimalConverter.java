package com.shael.shah.expensemanager.db;

import android.arch.persistence.room.TypeConverter;

import java.math.BigDecimal;

public class BigDecimalConverter {

    @TypeConverter
    public static BigDecimal fromDouble(Double value) {
        return value == null ? null : new BigDecimal(value);
    }

    @TypeConverter
    public static Double fromBigDecimal(BigDecimal value) {
        return value == null ? null : value.doubleValue();
    }
}
