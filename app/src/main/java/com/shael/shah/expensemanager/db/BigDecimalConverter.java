package com.shael.shah.expensemanager.db;

import android.arch.persistence.room.TypeConverter;

import java.math.BigDecimal;

public class BigDecimalConverter {

    @TypeConverter
    public static BigDecimal fromLong(Long value) {
        return value == null ? null : new BigDecimal(value).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    @TypeConverter
    public static Long fromBigDecimal(BigDecimal value) {
        return value == null ? null : value.multiply(new BigDecimal(100)).longValueExact();
    }
}