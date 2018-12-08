package com.example.googlefitkit.googleFitEntities;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GoogleFitDataSleep implements Serializable {
    @SerializedName("start_time ")
    private String startTime;
    @SerializedName("end_time ")
    private String endTime;

    public GoogleFitDataSleep() {
    }

    public GoogleFitDataSleep(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
