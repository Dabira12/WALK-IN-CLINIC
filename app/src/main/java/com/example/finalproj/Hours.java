package com.example.finalproj;

public class Hours {

    private String date, startingTime, finishTime;
    int startHour, startMinute, endHour, endMinute;

    public Hours(String date, int startHour, int startMinute, int endHour, int endMinute){
        this.date = date;
        this.startingTime = String.format("%s:%s", startHour, startMinute);
        this.finishTime = String.format("%s:%s", endHour, endMinute);
        this.startHour = startHour;
        this.endHour = endHour;
        this.startMinute = startMinute;
        this.endMinute = endMinute;
    }

    public String getDate(){
        return date;
    }

    public String getTimeStart(){
        return startingTime;
    }

    public int getStartHour(){
        return startHour;
    }

    public int getStartMinute(){
        return startMinute;
    }

    public int getEndHour(){
        return endHour;
    }

    public int getEndMinute(){
        return endMinute;
    }

    public String getTimeEnd(){
        return finishTime;
    }

    public void setStart(String newTime){
        startingTime = newTime;
    }

    public void  setEnd(String newTime){
        finishTime = newTime;
    }

}
