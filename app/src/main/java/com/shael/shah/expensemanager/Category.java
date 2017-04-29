package com.shael.shah.expensemanager;

import java.io.Serializable;

public class Category implements Serializable {

    private String type;

    public Category(String type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Category " + type;
    }
}
