package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private ArrayList<Alert> Alerts = new ArrayList<>();
    private  DataStorage dataStorage;

    private static final double SYSTOLIC_PRESSURE_MAX = 180 ;
    private static final double SYSTOLIC_PRESSURE_MIN = 90 ;
    private static final double DIASTOLIC_PRESSURE_MAX = 120 ;
    private static final double DIASTOLIC_PRESSURE_MIN = 60 ;
    private static final double OXYGEN_SATURATION_MIN = 92 ;

    private String patientId ;




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


        long endTime = System.currentTimeMillis();
        long startTime = endTime -(60*60*1000) ;
        List<PatientRecord> recordsFull = patient.getRecords() ;//gets everything for now
        List<PatientRecord> records = patient.getRecords(recordsFull.get(recordsFull.size()-1).getTimestamp()-(60*60*1000), recordsFull.get(recordsFull.size()-1).getTimestamp() ) ;//gets everything for now

        patientId = Integer.toString(recordsFull.get(0).getPatientId());

        bloodPressureDataAlert(records );
        bloodSaturationAlerts(records ) ;
        hypotensiveHypoxemiaAlert(records ) ;
        ECGAlert(records);
        ButtonEmergency(records);



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
        Alerts.add(alert);

    }

    public List<Alert> getAlerts(){
        return Alerts;
    }

    private List<PatientRecord> getSpecificValues(String label , List<PatientRecord> records){
        List<PatientRecord> specificPatientRecord = new ArrayList<>();

        for (PatientRecord record : records){
            if(record.getRecordType().equalsIgnoreCase(label)){
                specificPatientRecord.add(record) ;
            }
        }
        if(records.isEmpty()) {
            System.out.println("Is empty");
        }
        return specificPatientRecord ;
    }

    public void bloodPressureDataAlert(List<PatientRecord> records){

        double[] systolicArr = new double[3];
        double[] diastolicArr = new double[3];

        int s  = 0 ;
        int d =  0 ;
        int i = 0;

        while (records.size() > i) {
            PatientRecord patientRecord = records.get(i);
            if(patientRecord.getRecordType().equalsIgnoreCase("SystolicPressure")){
                if(s<3){
                    systolicArr[s] = patientRecord.getMeasurementValue();
                    System.out.println(Arrays.toString(systolicArr));
                    if(systolicArr[s]<SYSTOLIC_PRESSURE_MIN || systolicArr[s]>SYSTOLIC_PRESSURE_MAX){
                        Alert alert = checkBloodPressure("SystolicPressure" , systolicArr , patientRecord.getTimestamp() , String.valueOf(patientRecord.getPatientId()));
                        triggerAlert(alert) ;
                        return ;

                    }
                }
                else{

                    systolicArr = Arrays.copyOfRange(systolicArr , 1 , systolicArr.length+1);
                    systolicArr[systolicArr.length-1] = patientRecord.getMeasurementValue();
                    System.out.println(Arrays.toString(systolicArr));
                    Alert alert = checkBloodPressure("SystolicPressure" , systolicArr , patientRecord.getTimestamp() , String.valueOf(patientRecord.getPatientId()));
                    if(alert!= null){
                        triggerAlert(alert) ;
                        return ;
                    }
                }

                s++;
            }
            else if(patientRecord.getRecordType().equalsIgnoreCase("DiastolicPressure")){
                if(d<3){
                    diastolicArr[d] = patientRecord.getMeasurementValue();
                    System.out.println(Arrays.toString(diastolicArr));
                    if(diastolicArr[d]<DIASTOLIC_PRESSURE_MIN || diastolicArr[d]>DIASTOLIC_PRESSURE_MAX){
                        Alert alert = checkBloodPressure("DiastolicPressure" , diastolicArr , patientRecord.getTimestamp() , String.valueOf(patientRecord.getPatientId()));
                        triggerAlert(alert) ;
                        return ;

                    }
                }
                else{
                    diastolicArr = Arrays.copyOfRange(diastolicArr , 1 , diastolicArr.length+1);
                    diastolicArr[diastolicArr.length-1] = patientRecord.getMeasurementValue();
                    System.out.println(Arrays.toString(diastolicArr));
                    Alert alert = checkBloodPressure("DiastolicPressure" , diastolicArr , patientRecord.getTimestamp() , String.valueOf(patientRecord.getPatientId()));
                    if(alert!= null){
                        triggerAlert(alert) ;
                        return ;
                    }
                }
                d++;
            }

            i++;
        }

    }

    private Alert checkBloodPressure(String label , double[] past3Records, long timeStamp , String patientId){

        switch (label){
            case ("SystolicPressure") :
                if(Arrays.stream(past3Records).anyMatch(v -> v < SYSTOLIC_PRESSURE_MIN || v > SYSTOLIC_PRESSURE_MAX)){
                    Alert alert = new Alert(patientId , "SystolicPressure" ,timeStamp );
                    return alert;
                }
                else if(IntStream.range(1, past3Records.length)
                        .filter(j -> Math.abs(past3Records[j] - past3Records[j - 1]) >= 10)
                        .count() >=2) {
                    Alert alert = new Alert(patientId , "SystolicPressure" ,timeStamp );
                    return alert;
                }
            break ;
            case ("DiastolicPressure"):
                if(Arrays.stream(past3Records).anyMatch(v -> v < DIASTOLIC_PRESSURE_MIN || v > DIASTOLIC_PRESSURE_MAX)){
                    Alert alert = new Alert(patientId , "DiastolicPressure" ,timeStamp );
                    return alert;
                }
                else if(IntStream.range(1, past3Records.length)
                        .filter(j -> Math.abs(past3Records[j] - past3Records[j - 1]) >= 10)
                        .count() >=2) {
                    Alert alert = new Alert(patientId , "DiastolicPressure" ,timeStamp );
                    return alert;
                }
            break;
            default:
                return null;
        }
        return null ;
    }
    public void bloodSaturationAlerts(List<PatientRecord> originalRecords ){
        // I assume that the records contain different types of informatio , which is why I filter the original records.
        List<PatientRecord> records = getSpecificValues("Saturation" , originalRecords) ;

        if(records.isEmpty()){
            return;
        }

        double previousVal = records.get(0).getMeasurementValue() ; ;
        long previousTime = records.get(0).getTimestamp();;

        double currentVal= records.get(0).getMeasurementValue() ;
        long currentTime = records.get(0).getTimestamp();

        int i = 0 ;
        while ( i<records.size()){
            currentTime = records.get(i).getTimestamp() ;
            currentVal = records.get(i).getMeasurementValue() ;
            if (currentVal> previousVal){ // Such that the previousVal is always the highest value in the interval
                previousVal = currentVal ;
                previousTime = currentTime;
            }
            else{
                if(currentVal< OXYGEN_SATURATION_MIN){
                    Alert alert = new Alert(String.valueOf(records.get(0).getPatientId()) ,"Saturation", records.get(i).getTimestamp() );
                    triggerAlert(alert);
                    return;
                }
                else{
                    if( (currentTime - previousTime) >= 1000*60*10){ // the interval has passed meaning we have to reset the
                        previousVal = currentVal;                    // previous value to something smaller
                        previousTime = currentTime ;
                    }
                    if(Math.abs(currentVal-previousVal)>previousVal*0.05){
                        Alert alert = new Alert(String.valueOf(records.get(0).getPatientId()) ,"Saturation", records.get(i).getTimestamp() );
                        triggerAlert(alert);
                        return;
                    }

                }

            }
            i++;

        }
    }

    public void hypotensiveHypoxemiaAlert(List<PatientRecord> records){
        ArrayList<Boolean>TriggerSys  = new ArrayList<>();
        ArrayList<Boolean>Trigger02  = new ArrayList<>();

        int i = 0 ;
        while(i <records.size()){
            //System.out.println("Running hypotensiveHypoxemiaAlert "+i);

            PatientRecord currentRecord = records.get(i) ;
            switch (currentRecord.getRecordType()){
                case("SystolicPressure") :
                    if(currentRecord.getMeasurementValue()<SYSTOLIC_PRESSURE_MIN){
                        TriggerSys.add(true);
                    }
                break ;
                case("Saturation"):
                    if(currentRecord.getMeasurementValue()<OXYGEN_SATURATION_MIN) {
                        Trigger02.add(true);
                    }
                break ;
                default:
                    break;


            }
            if(Trigger02.isEmpty() | TriggerSys.isEmpty()){
                i++;
                continue;
            }
            else if(Trigger02.get(Trigger02.size()-1) & TriggerSys.get(TriggerSys.size()-1)){
                Alert alert = new Alert(String.valueOf(records.get(1).getPatientId()) , "HypotensiveHypoxemia" , records.get(i).getTimestamp());
                triggerAlert(alert);
                return;
            }

            i++;
        }

    }

    public void ECGAlert(List<PatientRecord> records){
        List<PatientRecord> ECG = getSpecificValues("ECG" , records);


        int i = 0 ;
        double rangeOfTriggering = 1 ;
        double average = 0 ;

        if(ECG.size()<5){
            System.out.println("Not enough data to get Average");
            return;
        }

        double [] window = new double[5];
        //System.out.println(ECG.size());

        while (i< ECG.size()){
            if(i<window.length){

                window[i] = ECG.get(i).getMeasurementValue();
                i++;
                continue;
            }
            else{
                average  = getAverage(window) ;
                window = Arrays.copyOfRange(window ,0, window.length);
                window[window.length-1] = ECG.get(i).getMeasurementValue() ;
            }

            if((window[window.length-1] - average) > rangeOfTriggering){
                Alert alert = new Alert(String.valueOf(records.get(1).getPatientId()) ,"Running ECG",ECG.get(i).getTimestamp() );
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

    public void ButtonEmergency(List<PatientRecord> records ){
        List<PatientRecord> emergencyButton = getSpecificValues("EmergencyButton"  , records);
        int i =0 ;
        while(i<emergencyButton.size()){

            if(emergencyButton.get(i).getMeasurementValue() ==1 ){
                 Alert alert = new Alert(String.valueOf(emergencyButton.get(1).getPatientId()) , "EmergencyButton" , records.get(i).getTimestamp());
                 triggerAlert(alert);
                return;
            }
            i++;
        }


    }

}
