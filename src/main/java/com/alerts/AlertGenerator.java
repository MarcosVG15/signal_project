package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;

    private static final double SYSTOLIC_PRESSURE_MAX = 180 ;
    private static final double SYSTOLIC_PRESSURE_MIN = 90 ;
    private static final double DIASTOLIC_PRESSURE_MAX = 120 ;
    private static final double DIASTOLIC_PRESSURE_MIN = 60 ;
    private static final double OXYGEN_SATURATION_MIN = 92 ;



    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {


        Long endTime = System.currentTimeMillis();
        Long startTime = endTime -(60*60*1000) ;
        List<PatientRecord> records = patient.getRecords(startTime , endTime) ;

        Alert alert = new Alert(null , null , 1) ;

        bloodPressureDataAlert(records , alert);
        bloodSaturationAlerts(records , alert) ;
        hypotensiveHypoxemiaAlert(records , alert) ;
        ECGAlert(records, alert);
        ButtonEmergency(records ,alert);



    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
        System.out.printf("Patient ID : %s | Condition : %s | TimeStamp: %d%n ",
            alert.getPatientId(),
            alert.getCondition(),
            alert.getTimestamp()
        );
    }

    private List<PatientRecord> getSpecficValueRecord(String label , List<PatientRecord> records){
        List<PatientRecord> specificPatientRecord = new ArrayList<>();

        for (PatientRecord record : records){
            if(record.getRecordType().equalsIgnoreCase(label)){
                specificPatientRecord.add(record) ;
            }
        }

        return specificPatientRecord ;
    }

    private void bloodPressureDataAlert( List<PatientRecord> records, Alert alert){

        List<PatientRecord> systolic = getSpecficValueRecord("SystolicPressure" , records) ;
        List<PatientRecord> diastolic = getSpecficValueRecord("DiastolicPressure" , records)  ;


        double[] systolicArr = new double[4];
        double[] diastolicArr = new double[4];

        int i = 0;
        while (records.size() > i) {
            if (i < 4) {
                systolicArr[i] = systolic.get(i).getMeasurementValue();
                checkBloodPressure("SystolicPressure", systolicArr, alert);

                diastolicArr[i] = diastolic.get(i).getMeasurementValue();
                checkBloodPressure("DiastolicPressure", diastolicArr, alert);

            } else {
                for (int j = 1; j < diastolicArr.length; j++) {
                    diastolicArr[j - 1] = diastolicArr[j];
                    systolicArr[j-1] = systolicArr[j] ;
                }
                systolicArr[systolicArr.length - 1] = systolic.get(i).getMeasurementValue();
                checkBloodPressure("SystolicPressure", systolicArr, alert);

                diastolicArr[diastolicArr.length - 1] = diastolic.get(i).getMeasurementValue();
                checkBloodPressure("DiastolicPressure", diastolicArr, alert);

            }
            i++;
        }

    }

    private void checkBloodPressure(String label , double[] past3Records , Alert alert){

        switch (label){
            case ("SystolicPressure") :
                if(Arrays.stream(past3Records).anyMatch(v -> v < SYSTOLIC_PRESSURE_MIN || v > SYSTOLIC_PRESSURE_MAX)){
                    return ;
                }
                if(IntStream.range(1, past3Records.length)
                        .filter(j -> Math.abs(past3Records[j] - past3Records[j - 1]) >= 10)
                        .count() >=3) {

                    triggerAlert(alert);
                }
            break ;
            case ("DiastolicPressure"):
                if(Arrays.stream(past3Records).anyMatch(v -> v < DIASTOLIC_PRESSURE_MIN || v > DIASTOLIC_PRESSURE_MAX)){
                    return ;
                }
                if(IntStream.range(1, past3Records.length)
                        .filter(j -> Math.abs(past3Records[j] - past3Records[j - 1]) >= 10)
                        .count() >3) {

                    triggerAlert(alert);
                }
            break;
            default:
                return;
        }
    }
    private void bloodSaturationAlerts(List<PatientRecord> originalRecords, Alert alert){
        // I assume that the records contain different types of informatio , which is why I filter the original records.
        List<PatientRecord> records = getSpecficValueRecord("Saturation" , originalRecords) ;

        double previousVal = records.get(0).getMeasurementValue() ;
        long previousTime = records.get(0).getTimestamp() ;

        int i = 1 ;
        while ( i<records.size()){
            double currentVal = records.get(i).getMeasurementValue() ;
            if(currentVal<= OXYGEN_SATURATION_MIN){
                triggerAlert(alert);
                return;
            }
            else{
                long currentTime = records.get(i).getTimestamp() ;

                if(Math.abs(currentVal-previousVal)>5){
                    triggerAlert(alert);
                    return;
                }
                else if(Math.abs(previousTime - currentTime)>=1000*60*10){
                    previousTime = currentTime ;
                    previousVal = currentVal ;

                }
            }
            i++;
        }
    }

    private void hypotensiveHypoxemiaAlert(List<PatientRecord> records, Alert alert){
        ArrayList<Boolean>TriggerSys  = new ArrayList<>();
        ArrayList<Boolean>Trigger02  = new ArrayList<>();

        int i = 0 ;
        while(i <records.size()){

            PatientRecord currentRecord = records.get(i) ;
            switch (currentRecord.getRecordType()){
                case("SystolicPressure") :
                    if(currentRecord.getMeasurementValue()<=SYSTOLIC_PRESSURE_MIN){
                        TriggerSys.add(true);
                    }
                break ;
                case("Saturation"):
                    if(currentRecord.getMeasurementValue()<=OXYGEN_SATURATION_MIN) {
                        Trigger02.add(true);
                    }
                break ;
            }
            if(Trigger02.isEmpty() | TriggerSys.isEmpty()){
                continue;
            }
            else if(Trigger02.getLast() & TriggerSys.getLast()){
                triggerAlert(alert);
            }

            i++;
        }

    }

    private void ECGAlert(List<PatientRecord> records, Alert alert){
        List<PatientRecord> ECG = getSpecficValueRecord("WhiteBloodCells" , records);

        int i = 0 ;
        double rangeOfTriggering = 0.5 ;
        double average = 0 ;

        double [] window = new double[(int)ECG.size()/20];

        while (i< ECG.size()){
            if(i<window.length){
                window[i] = ECG.get(i).getMeasurementValue();
            }
            else{
                average  = getAverage(window) ;
                window = Arrays.copyOfRange(window ,0, window.length-1 );
                window[i] = ECG.get(i).getMeasurementValue() ;
            }

            if(Math.abs(window[i] - average) > rangeOfTriggering){
                triggerAlert(alert);
                return ;
            }
            i++;

        }

    }
    private double getAverage(double[] window){
        double average = 0 ;

        for(double current :window){
            average += current ;
        }
        return average/ window.length ;
    }

    private void ButtonEmergency(List<PatientRecord> records , Alert alert){
        List<PatientRecord> emergencyButton = getSpecficValueRecord("EmergencyButton"  , records);

        int i =0 ;
        while(i<emergencyButton.size()){
            if(emergencyButton.get(i).getMeasurementValue() ==1 ){
                triggerAlert(alert);
                return;
            }
        }


    }

}
