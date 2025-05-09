package com.design_pattern.strategy;

import com.alerts.AlertGenerator;
import com.data_management.PatientRecord;
import com.design_pattern.Factory.BloodPressureAlertFactory;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BloodPressureStrategy implements AlertInterface{

    private BloodPressureAlertFactory factory ;


    private static final double SYSTOLIC_PRESSURE_MAX = 180 ;
    private static final double SYSTOLIC_PRESSURE_MIN = 90 ;
    private static final double DIASTOLIC_PRESSURE_MAX = 120 ;
    private static final double DIASTOLIC_PRESSURE_MIN = 60 ;

    private static final String SYSTOLIC  = "SystolicPressure";
    private static final String DIASTOLIC = "DiastolicPressure";

    private Map<String , ArrayList<PatientRecord>> historyOfPressure ;

    public BloodPressureStrategy(BloodPressureAlertFactory factory) {
        this.factory = factory;
        historyOfPressure = new HashMap<>();
                historyOfPressure.put(SYSTOLIC , new ArrayList<>());
                historyOfPressure.put(DIASTOLIC, new ArrayList<>());



    }

    @Override
    public void checkAlert(int patientId, List<PatientRecord> record, AlertGenerator  generator) {
        List<PatientRecord> pressureList = record.stream().filter(r ->
                        r.getRecordType().equalsIgnoreCase(SYSTOLIC) ||
                        r.getRecordType().equalsIgnoreCase(DIASTOLIC))
                            .collect(Collectors.toList());

        int i = 0 ;
        while( i<pressureList.size()){
            PatientRecord currentRecord = pressureList.get(i);

            historyOfPressure.get(currentRecord.getRecordType()).add(currentRecord);
            checkHistory(patientId, generator);

            switch(currentRecord.getRecordType()){
                case(SYSTOLIC):
                    if(currentRecord.getMeasurementValue()>=SYSTOLIC_PRESSURE_MAX || currentRecord.getMeasurementValue()<=SYSTOLIC_PRESSURE_MIN){
                        generator.triggerAlert(factory.createAlert(String.valueOf(patientId)
                                                  , currentRecord.getRecordType()
                                                  ,currentRecord.getTimestamp()));
                    }
                    break;
                case(DIASTOLIC):
                    if(currentRecord.getMeasurementValue()>=DIASTOLIC_PRESSURE_MAX || currentRecord.getMeasurementValue()<=DIASTOLIC_PRESSURE_MIN){
                        generator.triggerAlert( factory.createAlert(String.valueOf(patientId)
                                , currentRecord.getRecordType()
                                ,currentRecord.getTimestamp()));
                    }
                    break;
            }



            i++;
        }


    }

    private void checkHistory(int patientId, AlertGenerator generator){

        ArrayList<PatientRecord> systolic = historyOfPressure.get(SYSTOLIC) ;
        ArrayList<PatientRecord> diastolic = historyOfPressure.get(DIASTOLIC);

        boolean systolicTriggerState  = getTriggerDifference(systolic) ;
        boolean diastolicTriggerState = getTriggerDifference(diastolic);


        if(systolicTriggerState){
            generator.triggerAlert(factory.createAlert(String.valueOf(patientId), "DECREASING SYSTOLIC PRESSURE" ,systolic.get(systolic.size()-1).getTimestamp() ));
            System.out.println("DECREASING SYSTOLIC PRESSURE");
        }
        else if(diastolicTriggerState) {
            generator.triggerAlert(factory.createAlert(String.valueOf(patientId), "DECREASING DIASTOLIC PRESSURE", diastolic.get(diastolic.size() - 1).getTimestamp()));
            System.out.println("DECREASING DIASTOLIC PRESSURE");
        }


    }
    private boolean getTriggerDifference(ArrayList<PatientRecord> list){

        if(list.size()<4){

            //System.out.println("Not Enough Data For now...");
            return false; // this is zero as it won't trigger any alert.
        }
        else{
            int j = list.size()-3;
            int count = 0 ;
            while(j<list.size()){

                if(list.get(j-1).getMeasurementValue()-list.get(j).getMeasurementValue()>=10) {
                    count++;
                }
                j++;
            }
            if(count>=3){
                return true;
            }
        }
        return false;
    }



}
