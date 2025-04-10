package com.cardio_generator.outputs;

public class ConsoleOutputStrategy implements OutputStrategy {

    /**
     * This method takes in four variables and then prints the results
     * @param patientId - the id of the patient
     * @param timestamp - the time at which the patient was examined
     * @param label - the property that the patient was tested on
     * @param data - the values that the patient had for that specific property
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        System.out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
    }
}
