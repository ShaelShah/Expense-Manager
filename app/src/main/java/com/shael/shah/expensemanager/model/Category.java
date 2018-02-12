package com.shael.shah.expensemanager.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
//import android.os.Parcel;
//import android.os.Parcelable;

import java.io.Serializable;
import java.util.Objects;

@Entity
//public class Category implements Parcelable {
public class Category implements Serializable {

    /*public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };*/

    @PrimaryKey(autoGenerate = true)
    private int categoryID;

    @ColumnInfo
    private String type;

    @ColumnInfo
    private int color;

    public Category(String type, int color) {
        this.type = type;
        this.color = color;
    }

    /*private Category(Parcel parcel) {
        this.type = parcel.readString();
        this.color = parcel.readInt();
    }*/

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getType() {
        return type;
    }

    public int getColor() {
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

    /*@Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(type);
        parcel.writeInt(color);
    }

    @Override
    public int describeContents() {
        return 0;
    }*/
}
