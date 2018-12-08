package com.example.googlefitkit.googleFitEntities;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GoogleFitData2 implements Serializable {
    @SerializedName("date")
    private String date;
    @SerializedName("value")
    private int value;

    public GoogleFitData2() {
    }

    public GoogleFitData2(String date, int value) {
        this.date = date;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
