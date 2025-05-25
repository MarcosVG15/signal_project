package com.data_management;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alerts.AlertGenerator;

/**
 * Manages storage and retrieval of patient data within a healthcare monitoring
 * system.
 * This class serves as a repository for all patient records, organized by
 * patient IDs.
 */
public class DataStorage {
    private Map<Integer, Patient> patientMap;// Stores patient objects indexed by their unique patient ID.
    private long currentTime ;
    private ArrayList<Integer> patientIDs ;

    /**
     * Constructs a new instance of DataStorage, initializing the underlying storage
     * structure.
     */

    private static volatile DataStorage instance;


    private DataStorage() {
        this.patientMap = new HashMap<>();
        this.patientIDs = new ArrayList<>();
        currentTime = 0;


    }
    public static DataStorage getInstance() throws IOException {
        if (instance == null) {
            synchronized (DataStorage.class) {
                if (instance == null) {
                    instance = new DataStorage();
                }
            }
        }
        return instance;
    }


    /**
     * Adds or updates patient data in the storage.
     * If the patient does not exist, a new Patient object is created and added to
     * the storage.
     * Otherwise, the new data is added to the existing patient's records.
     *
     * @param patientId        the unique identifier of the patient
     * @param measurementValue the value of the health metric being recorded
     * @param recordType       the type of record, e.g., "HeartRate",
     *                         "BloodPressure"
     * @param timestamp        the time at which the measurement was taken, in
     *                         milliseconds since the Unix epoch
     */
    public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {


        if(patientMap.get(patientId) == null){
            patientIDs.add(patientId);
            Patient patient = new Patient(patientId);
            patient.addRecord(measurementValue , recordType , timestamp);
            patientMap.put(patientId ,patient);
        }
        else{
            patientMap.get(patientId).addRecord(measurementValue , recordType , timestamp);
        }


        // such that  we can accumulate data
        if (System.currentTimeMillis() - currentTime > 60_000*15) { // you can change this to the size that you want depends on the size of the batch you want to analyse
            buffer_Analyse();

            currentTime = System.currentTimeMillis();
        }


    }

    /**
     * Accumulates the data based on time intervals after which it uses the alert analysis to  analyse each patient at a time
     * using the Evaluate Data class .
     */
    public  void buffer_Analyse(){
            System.out.println("Running buffer....................................................................................................");
            long now = System.currentTimeMillis();
            Map<Integer, Patient> bufferPatientMap = new ConcurrentHashMap<>();

            for (int patientId : patientIDs) {
                List<PatientRecord> records = patientMap.get(patientId).getRecords();
                for (PatientRecord r : records) {
                    long ts = r.getTimestamp();
                    if (ts <= now && ts >= (now -  10 * 1000)) {
                        bufferPatientMap.computeIfAbsent(patientId, id -> new Patient(id))
                                .addRecord(r.getMeasurementValue(), r.getRecordType(), ts);
                    }
                }
            }

            getAlerts(bufferPatientMap);
    }

    private  void getAlerts(Map<Integer , Patient> patientMap){
        AlertGenerator alertGenerator = new AlertGenerator(this);
        for(int i = 0 ; i<patientIDs.size(); i++){
            alertGenerator.evaluateData(patientMap.get(patientIDs.get(i)));
        }

    }

    /**
     * Retrieves a list of PatientRecord objects for a specific patient, filtered by
     * a time range.
     *
     * @param patientId the unique identifier of the patient whose records are to be
     *                  retrieved
     * @param startTime the start of the time range, in milliseconds since the Unix
     *                  epoch
     * @param endTime   the end of the time range, in milliseconds since the Unix
     *                  epoch
     * @return a list of PatientRecord objects that fall within the specified time
     *         range
     */
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        if (patient != null) {
            return patient.getRecords(startTime, endTime);
        }
        else {
            System.out.println("patient is Null");
            return new ArrayList<>(); // return an empty list if no patient is found
        }

    }

    /**
     * Retrieves a collection of all patients stored in the data storage.
     *
     * @return a list of all patients
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    /**
     * The main method for the DataStorage class.
     * Initializes the system, reads data into storage, and continuously monitors
     * and evaluates patient data.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) throws IOException, URISyntaxException {

        DataStorage storage = DataStorage.getInstance();


        WebSocketClientAdapter webSocketClientAdapter = new WebSocketClientAdapter( new URI("ws://localhost:8080") , storage);
        webSocketClientAdapter.startStreaming();
    }
}
