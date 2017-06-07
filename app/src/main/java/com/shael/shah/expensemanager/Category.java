package com.shael.shah.expensemanager;

import java.io.Serializable;
import java.util.Objects;

class Category implements Serializable {

    private String type;
    private int color;

    Category(String type, int color) {
        this.type = type;
        this.color = color;
    }

    String getType() {
        return type;
    }

    int getColor() {
        return color;
    }

    @Override
    public boolean equals(Object cat) {
        if (this == cat)
            return true;

        if (this.getClass() != cat.getClass())
            return false;

        Category category = (Category) cat;
        return (this.getType().equals(category.getType()) && this.getColor() == category.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }
}
