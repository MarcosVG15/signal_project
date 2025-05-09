package com.cardio_generator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cardio_generator.generators.*;

import com.cardio_generator.outputs.*;
import com.cardio_generator.outputs.FileOutputStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * This class is reponsible for creating the entire data set of values using different generators such as
 *  - {@link ECGDataGenerator}
 *  - {@link BloodLevelsDataGenerator}
 *  - {@link BloodSaturationDataGenerator}
 *  - {@link BloodPressureDataGenerator}
 */
public class HealthDataSimulator {

    private static int patientCount = 50; // Default number of patients
    private static ScheduledExecutorService scheduler;
    private static OutputStrategy outputStrategy = new ConsoleOutputStrategy(); // Default output strategy
    private static final Random random = new Random();
    private static final Map<Integer, Patient> patientRegistry = new ConcurrentHashMap<>();


    private static volatile HealthDataSimulator instance;

    public static HealthDataSimulator getInstance(String[] args) throws IOException {
        if (instance == null) {
            synchronized (HealthDataSimulator.class) {
                if (instance == null) {
                    instance = new HealthDataSimulator(args);
                }
            }
        }
        return instance;
    }


    public HealthDataSimulator(String[] args) throws IOException {
        parseArguments(args);

        scheduler = Executors.newScheduledThreadPool(patientCount*9 );

        List<Integer> patientIds = initializePatientIds(patientCount);
        Collections.shuffle(patientIds); // Randomize the order of patient IDs

        scheduleTasksForPatients(patientIds);
    }


    /**
     * main method is where the class is initialised, it sets a pool size based on patientCount.
     * After which the patientId's are generated and shuffled. Once this is done the main calls
     * the method that generates the schedules tasks for each individual patient.
     *
     * @param args - commands given to the program
     * @throws IOException - throws error is Input output exception occurs
     */


    public static void main(String[] args) throws IOException, InterruptedException {

        parseArguments(args);

        scheduler = Executors.newScheduledThreadPool(patientCount*9 );

        List<Integer> patientIds = initializePatientIds(patientCount);
        Collections.shuffle(patientIds); // Randomize the order of patient IDs

        scheduleTasksForPatients(patientIds);



    }


    // its task is to create a collection on n datapoints such that they can be processed by the alert generator
    public static void buffer_Analyse(){
        System.out.println("run buffer");
        Map<Integer , Patient> bufferPatientMap = new ConcurrentHashMap<>();
        long timestamp = System.currentTimeMillis();


        for(int i = 1 ; i<patientCount+1 ; i++){
            List<PatientRecord> record = patientRegistry.get(i).getRecords();
            for ( int j = 0; j<record.size(); j++){
                if(record.get(j).getTimestamp()<= timestamp && record.get(j).getTimestamp()>=(timestamp-1000*60*30)){
                    if(bufferPatientMap.get(i) == null){
                        Patient patient = new Patient(i);
                        patient.addRecord(record.get(j).getMeasurementValue() , record.get(j).getRecordType() , record.get(j).getTimestamp());
                        bufferPatientMap.put(i,patient);
                    }
                    else{
                        bufferPatientMap.get(i).addRecord(record.get(j).getMeasurementValue() , record.get(j).getRecordType() , record.get(j).getTimestamp());
                    }
                }
            }

        }
        getAlerts(bufferPatientMap);

    }

    private static void getAlerts(Map<Integer , Patient> patientMap){
        DataStorage dataStorage = null;
        com.alerts.AlertGenerator alertGenerator = new com.alerts.AlertGenerator(dataStorage);
        for(int i = 1 ; i<patientCount+1; i++){
            alertGenerator.evaluateData(patientMap.get(i));
        }

    }

    /**
     * It is a method that takes in specific commands with which decides which type of output it should display
     * furthermore it allows to output the printHelp() method as well as displaying when an issue occures such
     * as the user calling a port of the TCPOutputStrategy that doesn't exist or enters and invalid command
     * that the code doesn't use.
     * @param args - arguments given through terminal such that the code can execute specific commands
     * @throws IOException - allows us to throw input output exceptions
     */

    private static void parseArguments(String[] args) throws IOException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;
                case "--patient-count":
                    if (i + 1 < args.length) {
                        try {
                            patientCount = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err
                                    .println("Error: Invalid number of patients. Using default value: " + patientCount);
                        }
                    }
                    break;
                case "--output":
                    if (i + 1 < args.length) {
                        String outputArg = args[++i];
                        if (outputArg.equals("console")) {
                            outputStrategy = new ConsoleOutputStrategy();
                        } else if (outputArg.startsWith("file:")) {
                            String baseDirectory = outputArg.substring(5);
                            Path outputPath = Paths.get(baseDirectory);
                            if (!Files.exists(outputPath)) {
                                Files.createDirectories(outputPath);
                            }
                            outputStrategy = new FileOutputStrategy(baseDirectory);
                        } else if (outputArg.startsWith("websocket:")) {
                            try {
                                int port = Integer.parseInt(outputArg.substring(10));
                                // Initialize your WebSocket output strategy here
                                outputStrategy = new WebSocketOutputStrategy(port);
                                System.out.println("WebSocket output will be on port: " + port);
                            } catch (NumberFormatException e) {
                                System.err.println(
                                        "Invalid port for WebSocket output. Please specify a valid port number.");
                            }
                        } else if (outputArg.startsWith("tcp:")) {
                            try {
                                int port = Integer.parseInt(outputArg.substring(4));
                                // Initialize your TCP socket output strategy here
                                outputStrategy = new TcpOutputStrategy(port);
                                System.out.println("TCP socket output will be on port: " + port);
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid port for TCP output. Please specify a valid port number.");
                            }
                        } else {
                            System.err.println("Unknown output type. Using default (console).");
                        }
                    }
                    break;
                default:
                    System.err.println("Unknown option '" + args[i] + "'");
                    printHelp();
                    System.exit(1);
            }
        }
    }


    /**
     * user manual for user to be able to use the code
     */
    private static void printHelp() {
        System.out.println("Usage: java HealthDataSimulator [options]");
        System.out.println("Options:");
        System.out.println("  -h                       Show help and exit.");
        System.out.println(
                "  --patient-count <count>  Specify the number of patients to simulate data for (default: 50).");
        System.out.println("  --output <type>          Define the output method. Options are:");
        System.out.println("                             'console' for console output,");
        System.out.println("                             'file:<directory>' for file output,");
        System.out.println("                             'websocket:<port>' for WebSocket output,");
        System.out.println("                             'tcp:<port>' for TCP socket output.");
        System.out.println("Example:");
        System.out.println("  java HealthDataSimulator --patient-count 100 --output websocket:8080");
        System.out.println(
                "  This command simulates data for 100 patients and sends the output to WebSocket clients connected to port 8080.");
    }

    /**
     * Method that is used to create a fixed amount of patientId's
     * Its amount corresponds to the patientCount
     * @param patientCount - amount of patient we want to generates id for
     * @return  - it return all the patientId's
     */
    private static List<Integer> initializePatientIds(int patientCount) {
        List<Integer> patientIds = new ArrayList<>();
        for (int i = 1; i <= patientCount; i++) {
            patientIds.add(i);
            patientRegistry.put(i , new Patient(i));

        }
        return patientIds;
    }

    /**
     * Schedules and adds specific information for each patient using the scheduleTask method
     * @param patientIds - the array of all patientId's
     */

    private static void scheduleTasksForPatients(List<Integer> patientIds) {
        ECGDataGenerator ecgDataGenerator = new ECGDataGenerator(patientCount);
        BloodSaturationDataGenerator bloodSaturationDataGenerator = new BloodSaturationDataGenerator(patientCount);
        BloodPressureDataGenerator bloodPressureDataGenerator = new BloodPressureDataGenerator(patientCount);
        BloodLevelsDataGenerator bloodLevelsDataGenerator = new BloodLevelsDataGenerator(patientCount);
        EmergencyButtonGenerator emergencyButtonGenerator = new EmergencyButtonGenerator(patientCount);
        AlertGenerator alertGenerator = new AlertGenerator(patientCount);

        for (int patientId : patientIds) {
            scheduleTask(() -> patientRegistry.get(patientId).addRecord(ecgDataGenerator.getValues(patientId),"ECG",System.currentTimeMillis()), 1, TimeUnit.SECONDS);
            scheduleTask(() -> patientRegistry.get(patientId).addRecord(bloodSaturationDataGenerator.getValues(patientId),"Saturation",System.currentTimeMillis()), 1, TimeUnit.SECONDS);
            scheduleTask(() -> patientRegistry.get(patientId).addRecord(bloodPressureDataGenerator.getValues(patientId)[0],"SystolicPressure",System.currentTimeMillis()), 1, TimeUnit.SECONDS);
            scheduleTask(() -> patientRegistry.get(patientId).addRecord(bloodPressureDataGenerator.getValues(patientId)[1],"DiastolicPressure",System.currentTimeMillis()), 1, TimeUnit.SECONDS);
            scheduleTask(() -> patientRegistry.get(patientId).addRecord(bloodLevelsDataGenerator.getValues(patientId)[0],"Cholesterol",System.currentTimeMillis()), 2, TimeUnit.SECONDS);
            scheduleTask(() -> patientRegistry.get(patientId).addRecord(bloodLevelsDataGenerator.getValues(patientId)[1],"WhiteBloodCells",System.currentTimeMillis()), 2, TimeUnit.SECONDS);
            scheduleTask(() -> patientRegistry.get(patientId).addRecord(bloodLevelsDataGenerator.getValues(patientId)[2],"RedBloodCells",System.currentTimeMillis()), 2, TimeUnit.SECONDS);
            scheduleTask(() -> patientRegistry.get(patientId).addRecord(emergencyButtonGenerator.getValues(patientId),"EmergencyButton",System.currentTimeMillis()),15,TimeUnit.SECONDS);
        }
        scheduleTask(() -> {
            try {
                HealthDataSimulator.buffer_Analyse();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 60*30, TimeUnit.SECONDS);

    }

    /**
     * Sechudles a task to run after some time has passed
     *
     * @param task - what the code needs to execute as a task
     * @param period - how much time it should take between periods
     * @param timeUnit - the time stamp for the period
     */
    private static void scheduleTask(Runnable task, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(task, random.nextInt(5), period, timeUnit);
    }

}
