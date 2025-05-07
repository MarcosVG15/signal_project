package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

import java.util.Random;

public class EmergencyButtonGenerator implements PatientDataGenerator{

    private double[] eButtonArray ;
    private static Random random  = new Random() ;
    private  static final double A = 5 ;
    private  static final double B = 10 ;
    private double Values;


    public EmergencyButtonGenerator(int patientCount){
        this.eButtonArray = new double[patientCount+1];

        for(int i = 0 ; i<eButtonArray.length ; i++){
            eButtonArray[i] = random.nextDouble()*10; // give a random value
        }
    }

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {

            double chance = 0.01 + eButtonArray[patientId] * 0.01;
            boolean pressed = random.nextDouble() < chance;

            double value = pressed ? 1.0 : 0.0;
            outputStrategy.output(patientId, System.currentTimeMillis(), "EmergencyButton", Double.toString(value));
            Values = value ;

            eButtonArray[patientId] = value;

        } catch (Exception e) {
            System.err.println("An error occurred while generating ECG data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }


    public double getValues(int patientId){
        OutputStrategy outputStrategy = new OutputStrategy() {
            @Override
            public void output(int patientId, long timestamp, String label, String data) {

            }
        };
        generate(patientId , outputStrategy);

        return Values;
    }

}
