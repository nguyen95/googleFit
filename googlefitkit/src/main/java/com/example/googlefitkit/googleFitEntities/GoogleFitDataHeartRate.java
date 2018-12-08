package com.example.googlefitkit.googleFitEntities;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GoogleFitDataHeartRate implements Serializable {
    @SerializedName("date")
    private String date;
    @SerializedName("value")
    private int value;
    @SerializedName("max_value")
    private int maxValue;
    @SerializedName("min_value")
    private int minValue;

    public GoogleFitDataHeartRate() {
    }

    public GoogleFitDataHeartRate(String date, int value, int maxValue, int minValue) {
        this.date = date;
        this.value = value;
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
