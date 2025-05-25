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
 *
 * Each of these methods will return once they have found a problem, This will later on be
 * updated to simply trigger and alert.
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

    /**
     * This is a getter method such that I can evaluate wether the trigger method was used during testing
     * @return - List or alerts
     */
    public List<Alert> getAlerts(){
        return Alerts;
    }


    /**
     * This method allows me to filter the records of a patient for a specific measurement type
     * @param label - Indicates what Measure Type I want to filter for
     * @param records - The set of all patient records
     * @return - returns a list records that are exclusively of the type we want them to be
     */
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

    /**
     * This method analyses a filtered list of records to find any that are outside a given saftey threshold
     * Not only does it check if the values are outside a given threshold , but also the rate of drop of three
     * consecutive values. If these three values each drop by more than a factor of 10 , it triggers the alert
     * stops the analysis.
     * @param records- this is the entire list of records of a given patient
     */
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

                    }
                }
                else{

                    systolicArr = Arrays.copyOfRange(systolicArr , 1 , systolicArr.length+1);
                    systolicArr[systolicArr.length-1] = patientRecord.getMeasurementValue();
                    System.out.println(Arrays.toString(systolicArr));
                    Alert alert = checkBloodPressure("SystolicPressure" , systolicArr , patientRecord.getTimestamp() , String.valueOf(patientRecord.getPatientId()));
                    if(alert!= null){
                        triggerAlert(alert) ;
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


                    }
                }
                else{
                    diastolicArr = Arrays.copyOfRange(diastolicArr , 1 , diastolicArr.length+1);
                    diastolicArr[diastolicArr.length-1] = patientRecord.getMeasurementValue();
                    System.out.println(Arrays.toString(diastolicArr));
                    Alert alert = checkBloodPressure("DiastolicPressure" , diastolicArr , patientRecord.getTimestamp() , String.valueOf(patientRecord.getPatientId()));
                    if(alert!= null){
                        triggerAlert(alert) ;
                    }
                }
                d++;
            }

            i++;
        }

    }

    /**
     * This is a helper method that exclusively analyses the three consective values to see if each has a drop
     * more than 10 each.
     * @param label - takes in the label that we want to analyse as each type of blood pressure has different requirements
     * @param past3Records - an array of three previous values
     * @param timeStamp - the time of one of the values such that we can trigger an alert
     * @param patientId - the id of the patient we are analysing
     * @return - returns and alert such that the method {@link bloodPressureDataAlert} can trigger the alert
     */
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

    /**
     * This method not only checks whether the blood saturation is between the given threshold but also triggers an alert
     * if there is a change of more than 5 percent in oxygen saturation  in the span of 10 minutes with respect to the highest
     * value in that interval.
     * @param originalRecords - record of all data for a given patient
     */
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
                    }

                }

            }
            i++;

        }
    }

    /**
     * This method triggers and alert if the Systolic blood pressure and the oxygen saturation are both
     * below their required limit.
     * @param records - record of all values for a given patient.
     */

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
            }

            i++;
        }

    }

    /**
     * This method triggers an alert if the value peaks above a given average of the values before it
     * This peak has to be greater than 1 ( will be confirmed after testing)
     * A flaw that can be worked on is that it requires 5 previous data points to create the Average such
     * that is can start analysing  , which means that there is a period of time when the patient could have
     * an emergency, and it wouldn't be detected.
     * @param records - list of records for a given patient
     */

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

                System.out.println(Arrays.toString(window));
            }

            if((window[window.length-1] - average) > rangeOfTriggering){
                Alert alert = new Alert(String.valueOf(records.get(1).getPatientId()) ,"ECG",ECG.get(i).getTimestamp() );
                triggerAlert(alert);
            }
            i++;

        }

    }

    /**
     * This is a helper method of the ECG alert method that calculates the average of N values
     * @param window - the array of values we want to compute the average for
     * @return - return a double value which is the average for that window.
     */
    private double getAverage(double[] window){
        double average = 0 ;

        for(double current :window){
            average += current ;
        }
        return average/ window.length ;
    }

    /**
     * Method triggers an alert if the value is 1 , and doesn't trigger if the value is anything else
     * this value analysis mimics the input of a patient pressing a button.
     * @param records - all records for a given patient
     */
    public void ButtonEmergency(List<PatientRecord> records ){
        List<PatientRecord> emergencyButton = getSpecificValues("EmergencyButton"  , records);
        int i =0 ;
        while(i<emergencyButton.size()){

            if(emergencyButton.get(i).getMeasurementValue() ==1 ){
                 Alert alert = new Alert(String.valueOf(emergencyButton.get(1).getPatientId()) , "EmergencyButton" , records.get(i).getTimestamp());
                 triggerAlert(alert);
            }
            i++;
        }


    }

}
