package com.design_pattern.strategy;

import com.alerts.AlertGenerator;
import com.data_management.PatientRecord;
import com.design_pattern.Factory.EmergencyButtonFactory;

import java.util.List;
import java.util.stream.Collectors;

public class EmergencyButtonStrategy implements AlertInterface{

    private EmergencyButtonFactory factory ;

    public EmergencyButtonStrategy(EmergencyButtonFactory factory){
        this.factory = factory;
    }




    @Override
    public void checkAlert(int patientId, List<PatientRecord> record, AlertGenerator generator) {
        List<PatientRecord> EmergencyButtonlist = record.stream().filter(R -> R.getRecordType()
                                                            .equalsIgnoreCase("EmergencyButton"))
                                                            .collect(Collectors.toList());

        for ( int i = 0 ; i<EmergencyButtonlist.size() ; i++){
            PatientRecord currentRecord = EmergencyButtonlist.get(i) ;
            if(currentRecord.getMeasurementValue() == 1){
                generator.triggerAlert(factory.createAlert(String.valueOf(patientId)
                        , currentRecord.getRecordType()
                        ,currentRecord.getTimestamp()));
            }

        }
    }
}
