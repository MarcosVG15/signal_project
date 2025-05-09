package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.design_pattern.Factory.*;
import com.design_pattern.strategy.*;

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
        if (patient == null){
            System.out.println("patient is null");
        }
        assert patient != null;
        List<PatientRecord> records = patient.getRecords() ;//gets everything for now
        patientId = Integer.toString(records.get(0).getPatientId());

        int patientID = Integer.parseInt(patientId);

        new BloodPressureStrategy(new BloodPressureAlertFactory()).checkAlert(patientID , records , this);
        new OxygenSaturationStrategy(new OxygenSaturationAlertFactory()).checkAlert(patientID, records ,this);
        new HeartRateStrategy(new HeartRateAlertFactory()).checkAlert(patientID , records , this);
        new HypotensiveHypoxemiaStrategy(new HypotensiveHypoxemiaAlertFactory()).checkAlert(patientID , records , this);
        new EmergencyButtonStrategy(new EmergencyButtonFactory()).checkAlert(patientID , records , this);


    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    public void triggerAlert(Alert alert) {
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
    public DataStorage getDataStorage(){
        return dataStorage ;
    }



}
