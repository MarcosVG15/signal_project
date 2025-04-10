package com.cardio_generator.outputs;


public interface OutputStrategy {

    /**
     * This interface makes a class create a method that takes in four different parameters these are:
     * @param patientId - The ID of the patient which you want to get the values from
     * @param timestamp - The time at which the person was said
     * @param label - the test that was run on the patient
     * @param data - the data collected from the test
     *
     * This interface is used by all the generators
     */
    void output(int patientId, long timestamp, String label, String data);
}
