package com.shael.shah.expensemanager.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Category {

    @PrimaryKey(autoGenerate = true)
    private int categoryID;

    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "colour")
    private int colour;

    public Category(String type) {
        this.type = type;
        this.colour = this.getCategoryID();
    }

    @Ignore
    public Category(String type, int colour) {
        this.type = type;
        this.colour = colour;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    @Override
    public boolean equals(Object cat) {
        if (this == cat)
            return true;

        if (cat == null)
            return false;

        if (this.getClass() != cat.getClass())
            return false;

        Category category = (Category) cat;
        return (this.getType().equals(category.getType()) && this.getColour() == category.getColour());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, colour);
    }

}
