package com.design_pattern.strategy;

import com.alerts.AlertGenerator;
import com.data_management.PatientRecord;
import com.design_pattern.Factory.OxygenSaturationAlertFactory;

import java.util.List;
import java.util.stream.Collectors;

public class OxygenSaturationStrategy implements AlertInterface{
    private OxygenSaturationAlertFactory factory ;
    private static final double OXYGEN_SATURATION_MIN = 92 ;
    private boolean isUpdated ;


    public OxygenSaturationStrategy(OxygenSaturationAlertFactory factory){
        this.factory = factory;
        this.isUpdated = false;
    }

    @Override
    public void checkAlert(int patientId, List<PatientRecord> record, AlertGenerator generator) {
        List<PatientRecord> saturationList = record.stream().filter(r -> r.getRecordType()
                                        .equalsIgnoreCase("Saturation"))
                                        .collect(Collectors.toList());

        int i = 0 ;

        Item LargestItem = new Item(0 , 0) ;
        Item currentItem = new Item(0 , 0 );

        while(i<saturationList.size()){
            PatientRecord currentRecord = saturationList.get(i);

            currentItem.setMeasurementValue(currentRecord.getMeasurementValue());
            currentItem.setTimeStamp(currentRecord.getTimestamp());

            update(LargestItem , currentItem);
            if(currentItem.getMeasurementValue()<OXYGEN_SATURATION_MIN){
                generator.triggerAlert(factory.createAlert(String.valueOf(patientId)
                        , currentRecord.getRecordType()
                        ,currentRecord.getTimestamp()));
            }
            if(!isUpdated && LargestItem.getMeasurementValue()- currentItem.getMeasurementValue()>= LargestItem.getMeasurementValue()*0.05){
                generator.triggerAlert(factory.createAlert(String.valueOf(patientId)
                        , currentRecord.getRecordType()+"RAPID DECREASE IN OXYGEN "
                        ,currentRecord.getTimestamp()));
            }


            isUpdated = true ;
            i++;
        }


    }

    private void update(Item LargestItem , Item currentItem){
        double largestMeasurement = LargestItem.getMeasurementValue();
        double currentMeasurement = currentItem.getMeasurementValue();

        long currentTimeStamp = currentItem.getTimeStamp();

        if(largestMeasurement<currentMeasurement){
            LargestItem.setMeasurementValue(currentMeasurement);
            LargestItem.setTimeStamp(currentTimeStamp);
            isUpdated();
        }
        else if(LargestItem.getTimeStamp()+1000*60*10 <= currentItem.getTimeStamp()){
            LargestItem.setMeasurementValue(currentMeasurement);
            LargestItem.setTimeStamp(currentTimeStamp);
            isUpdated();
        }
        else{
            isUpdated = false;
        }


    }
    private void isUpdated(){
        isUpdated = true ;
    }

}
