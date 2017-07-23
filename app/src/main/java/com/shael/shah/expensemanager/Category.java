package com.shael.shah.expensemanager;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

class Category implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    private String type;
    private int color;

    Category(String type, int color) {
        this.type = type;
        this.color = color;
    }

    private Category(Parcel parcel) {
        this.type = parcel.readString();
        this.color = parcel.readInt();
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

        if (cat == null)
            return false;

        if (this.getClass() != cat.getClass())
            return false;

        Category category = (Category) cat;
        return (this.getType().equals(category.getType())
                && this.getColor() == category.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(type);
        parcel.writeInt(color);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
