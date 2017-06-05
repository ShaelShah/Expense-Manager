package com.shael.shah.expensemanager;

import java.io.Serializable;

public class Category implements Serializable {

    private String type;
    private int color;

    public Category(String type, int color) {
        this.type = type;
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public int getColor() {
        return color;
    }

    //TODO: There could be a better way to implement this function
    //TODO: HashCode should also be overridden
    @Override
    public boolean equals(Object cat) {
        if (this == cat) {
            return true;
        }

        if (this.getClass() != cat.getClass()) {
            return false;
        }

        Category category = (Category) cat;

        if (!this.getType().equals(category.getType())) {
            return false;
        }

        return this.getColor() == category.getColor();

    }

    //TODO: toString is ugly and probably should not be used
    @Override
    public String toString() {
        return "Category " + type;
    }
}
