package com.design_pattern.strategy;

public class Item {

    private double MeasurementValue ;
    private long timeStamp ;

    public Item(double MeasurementValue , long timeStamp){
        this.MeasurementValue = MeasurementValue ;
        this.timeStamp = timeStamp ;
    }
    public double getMeasurementValue(){
        return MeasurementValue ;
    }
    public long getTimeStamp(){
        return timeStamp ;
    }

    public void setMeasurementValue(double newMeasurementValue){
        this.MeasurementValue = newMeasurementValue ;
    }
    public void setTimeStamp(long newTimeStamp){
        this.timeStamp = newTimeStamp ;
    }


}
