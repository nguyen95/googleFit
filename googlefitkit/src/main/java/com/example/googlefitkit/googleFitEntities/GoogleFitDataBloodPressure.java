package com.example.googlefitkit.googleFitEntities;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GoogleFitDataBloodPressure implements Serializable {
    @SerializedName("date")
    private String date;
    @SerializedName("sbp")
    private float sbp;
    @SerializedName("dbp")
    private float dbp;

    public GoogleFitDataBloodPressure() {
    }

    public GoogleFitDataBloodPressure(String date, float sbp, float dbp) {
        this.date = date;
        this.sbp = sbp;
        this.dbp = dbp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getSbp() {
        return sbp;
    }

    public void setSbp(float sbp) {
        this.sbp = sbp;
    }

    public float getDbp() {
        return dbp;
    }

    public void setDbp(float dbp) {
        this.dbp = dbp;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
