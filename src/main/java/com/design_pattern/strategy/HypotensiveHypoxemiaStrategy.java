package com.design_pattern.strategy;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.PatientRecord;
import com.design_pattern.Factory.EmergencyButtonFactory;
import com.design_pattern.Factory.HypotensiveHypoxemiaAlertFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HypotensiveHypoxemiaStrategy implements AlertInterface{

    private HypotensiveHypoxemiaAlertFactory factory ;
    private static final double SYSTOLIC_PRESSURE_MIN = 90 ;
    private static final double OXYGEN_SATURATION_MIN = 92 ;

    public HypotensiveHypoxemiaStrategy(HypotensiveHypoxemiaAlertFactory factory){
        this.factory = factory;
    }


    @Override
    public void checkAlert(int patientId, List<PatientRecord> records, AlertGenerator generator) {

        ArrayList<Item> TriggerSys  = new ArrayList<>();
        ArrayList<Item>Trigger02  = new ArrayList<>();

        int i = 0 ;
        while(i <records.size()){
            //System.out.println("Running hypotensiveHypoxemiaAlert "+i);

            PatientRecord currentRecord = records.get(i) ;
            switch (currentRecord.getRecordType()){
                case("SystolicPressure") :
                    if(currentRecord.getMeasurementValue()<SYSTOLIC_PRESSURE_MIN){
                        Item newItem = new Item(1 , currentRecord.getTimestamp());
                        TriggerSys.add(newItem);
                    }
                    break ;
                case("Saturation"):
                    if(currentRecord.getMeasurementValue()<OXYGEN_SATURATION_MIN) {
                        Item newItem = new Item(1 , currentRecord.getTimestamp());
                        Trigger02.add(newItem);
                    }
                    break ;
                default:
                    break;


            }
            if(Trigger02.isEmpty() | TriggerSys.isEmpty()){
                i++;
                continue;
            }
            else if(Trigger02.get(Trigger02.size()-1).getMeasurementValue() == TriggerSys.get(TriggerSys.size()-1).getMeasurementValue() && Math.abs(Trigger02.get(Trigger02.size()-1).getTimeStamp()-TriggerSys.get(TriggerSys.size()-1).getTimeStamp())<1000){
                generator.triggerAlert(factory.createAlert(String.valueOf(patientId)
                                            , "HYPOTENSIVE HYPOXEMIA ALERT"
                                            ,currentRecord.getTimestamp()));

            }

            i++;
        }

    }
}
