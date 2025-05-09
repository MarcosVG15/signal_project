package com.design_pattern.Decorators;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;

import java.util.List;

public class RepeatedAlertDecoratorDec extends AlertDecoratorDec {

    private AlertGenerator generator ;
    private long[] interval ;


    public RepeatedAlertDecoratorDec(AlertInterfaceDec alertDecorator , AlertGenerator generator , long[] interval) {
        super(alertDecorator);
        this.generator = generator ;
        this.interval = interval ;




    }
    public void evaluate(AlertInterfaceDec alertDecorator){
        DataStorage dataStorage = null;
        AlertGenerator generator2 = new AlertGenerator(dataStorage);

        Patient patient = generator.getDataStorage().getAllPatients().get(0);
        if(interval[1] > patient.getRecords().get(patient.getRecords().size()-1).getTimestamp()||interval[0]<=patient.getRecords().get(0).getTimestamp()){
            System.out.println("WINDOW TOO BIG");
        }
        else{

            Patient tempPatient = new Patient(2);
            patient.getRecords().stream().filter(r ->
                    r.getTimestamp()>interval[0] &&
                    r.getTimestamp()<=interval[1])
                    .forEach(r->tempPatient.addRecord(r.getMeasurementValue(), r.getRecordType(),r.getTimestamp()));

            generator2.evaluateData(tempPatient);
            List<com.alerts.Alert> reviewedAlerts = generator2.getAlerts();
            List<com.alerts.Alert> currentAlerts = generator.getAlerts();


            for ( int i = 0 ;i<reviewedAlerts.size() ; i++){
                if(!currentAlerts.get(i).equals(reviewedAlerts.get(i))){
                    Alert alert = new Alert(currentAlerts.get(i).getPatientId()  ,"ALERT MALFUNCTION" , System.currentTimeMillis());
                    generator.triggerAlert(alert);
                    return;
                }

            }

        }

    }

}
