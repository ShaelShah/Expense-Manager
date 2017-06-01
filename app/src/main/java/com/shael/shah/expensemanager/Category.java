package com.shael.shah.expensemanager;

import java.io.Serializable;

public class Category implements Serializable {

    private String type;
    private int color;

    public Category(String type, int color) {
        this.type = type;
        this.color = color;
    }

    //TODO: Is this setter required? Will a category ever be changed?
    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    //TODO: Is this setter required? Will the color ever be changed?
    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

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

        if (this.getColor() != category.getColor()) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Category " + type;
    }
}
