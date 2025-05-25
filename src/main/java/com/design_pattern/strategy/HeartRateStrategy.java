package com.design_pattern.strategy;


import com.alerts.AlertGenerator;
import com.data_management.PatientRecord;
import com.design_pattern.Factory.HeartRateAlertFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This code will analyse data using the check alert method and return a
 */
public class HeartRateStrategy implements AlertInterface{

    private List<PatientRecord> ECGRecord ;
    private static final int WINDOW_SIZE = 5 ;
    private static final double THRESHOLD = 1  ;
    private HeartRateAlertFactory factory ;

    public HeartRateStrategy(HeartRateAlertFactory factory) {
        this.factory = factory;
    }


    @Override
    public void checkAlert(int patientId, List<PatientRecord> record , AlertGenerator generator) {
        List<PatientRecord> ECGList = record.stream().filter(Record ->Record.getRecordType()
                        .equalsIgnoreCase("ECG"))
                        .collect(Collectors.toList());
        this.ECGRecord  = ECGList ;


        if (ECGList.isEmpty()){
            System.out.println("ECG is Empty");
            return ;
        }

        int i = 0 ;
        while ( i<ECGList.size()){

            PatientRecord currentRecord = ECGList.get(i);
            double average = getAverage(WINDOW_SIZE, i);

            if(Math.abs(average -Math.PI)<=1E-10){
                //System.out.println("Not enough data for now .....");
            }
            else{
                if((average-currentRecord.getMeasurementValue()) <=THRESHOLD ){
                    generator.triggerAlert(factory.createAlert(String.valueOf(patientId)
                                                    , currentRecord.getRecordType()
                                                    , currentRecord.getTimestamp()));
                }
            }

            i++;
        }



    }

    // class that gets average using window
    private double getAverage(int windowSize , int currentIndex ){

        double sum = 0 ;
        for ( int i = currentIndex- windowSize; i<currentIndex; i++){
            if(i<0){
                return Math.PI ; // allows me to analyse the output in the checkAlert
            }
            else{
                sum+=ECGRecord.get(i).getMeasurementValue() ;
            }
        }
        return (sum/windowSize);
    }
}
