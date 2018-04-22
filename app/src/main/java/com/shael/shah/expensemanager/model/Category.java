package com.shael.shah.expensemanager.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.shael.shah.expensemanager.utils.DataSingleton;

import java.util.Objects;

@Entity
public class Category
{

    @PrimaryKey
    private int categoryID;

    @ColumnInfo
    private String type;

    @ColumnInfo
    private int color;

    public Category(String type, int color)
    {
        this.type = type;
        this.color = color;
    }

    private Category(Builder builder)
    {
        categoryID = builder.categoryID;
        type = builder.type;
        color = builder.color;
    }

    public int getCategoryID()
    {
        return categoryID;
    }

    public void setCategoryID(int categoryID)
    {
        this.categoryID = categoryID;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public int getColor()
    {
        return color;
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    @Override
    public boolean equals(Object cat)
    {
        if (this == cat)
            return true;

        if (cat == null)
            return false;

        if (this.getClass() != cat.getClass())
            return false;

        Category category = (Category) cat;
        return (this.getType().equals(category.getType())
                && this.getColor() == category.getColor());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(type, color);
    }

    public static class Builder
    {

        private final int categoryID;
        private final String type;
        private final int color;

        public Builder(String type, int color)
        {
            this.categoryID = DataSingleton.getInstance().getCategoryID();
            this.type = type;
            this.color = color;
        }

        public Category build()
        {
            return new Category(this);
        }
    }
}
