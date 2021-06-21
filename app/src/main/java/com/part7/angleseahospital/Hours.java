package com.part7.angleseahospital;

public class Hours {
    private String day;
    private String date;
    private String hours;

    public Hours(String day, String date, String hours) {
        this.day = day;
        this.date = date;
        this.hours = hours;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }
}