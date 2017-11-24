package ai.elimu.appstore.model.appsynchronization;

import com.google.gson.annotations.SerializedName;

public class AppUploadTimeModel {

    @SerializedName("month")
    private int month;

    @SerializedName("year")
    private int year;

    @SerializedName("dayOfMonth")
    private int dayOfMonth;

    @SerializedName("hourOfDay")
    private int hourOfDay;

    @SerializedName("minute")
    private int minute;

    @SerializedName("second")
    private int second;

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }
}
