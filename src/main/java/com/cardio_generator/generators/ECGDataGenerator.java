package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;


public class ECGDataGenerator implements PatientDataGenerator {


    /**
     *  random - is a random variable
     *  lastEcgValues   is an array of double that will contain the ecg values
     *  PI - is a double constant that contains the values of pi
     */

    private static final Random random = new Random();
    private double[] lastEcgValues;
    private static final double PI = Math.PI;
    private double Values;


    /**
     * The constructor initialises the array lastEcgValues and fills it with 0
     * @param patientCount  - the amount of patienst that exist
     */

    public ECGDataGenerator(int patientCount) {
        lastEcgValues = new double[patientCount + 1];
        // Initialize the last ECG value for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastEcgValues[i] = 0; // Initial ECG value can be set to 0
        }
    }


    /**
     * This method fills the array with EcgValues using simulateEcgWaveform method and addis it to outputStrategy
     * @param patientId - the id of the patient we want to generate the Ecg
     * @param outputStrategy - is the interface that conatins the patient id , the timestamp , the label and the value of that label
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        // TODO Check how realistic this data is and make it more realistic if necessary
        try {
            double ecgValue = simulateEcgWaveform(patientId, lastEcgValues[patientId]);
            outputStrategy.output(patientId, System.currentTimeMillis(), "ECG", Double.toString(ecgValue));
            lastEcgValues[patientId] = ecgValue;
            Values = ecgValue ;
        } catch (Exception e) {
            System.err.println("An error occurred while generating ECG data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }


    /**
     * this method creates random values with certain thresholds to simulate the Ecg Waves
     * it randomises hr with a value between 60 and 80
     * it assigns t the current time
     * assigns ecgFrequency the variable hr/60
     *
     * with these values it creates the values of pWave qrsComplex and tWave which are then added with a randomised
     * value which act as noise and then is return
     * @param patientId - (isn't used ) the id of the patient we want to generate the values for
     * @param lastEcgValue - (isn't used) but it is the last values of the ecg
     * @return returns the addition of the pWave qrsComplex and tWave and a noise component to make the result realistic
     */
    private double simulateEcgWaveform(int patientId, double lastEcgValue) {
        // Simplified ECG waveform generation based on sinusoids
        double hr = 60.0 + random.nextDouble() * 20.0; // Simulate heart rate variability between 60 and 80 bpm
        double t = System.currentTimeMillis() / 1000.0; // Use system time to simulate continuous time
        double ecgFrequency = hr / 60.0; // Convert heart rate to Hz

        // Simulate different components of the ECG signal
        double pWave = 0.1 * Math.sin(2 * PI * ecgFrequency * t);
        double qrsComplex = 0.5 * Math.sin(2 * PI * 3 * ecgFrequency * t); // QRS is higher frequency
        double tWave = 0.2 * Math.sin(2 * PI * 2 * ecgFrequency * t + PI / 4); // T wave is offset

        return pWave + qrsComplex + tWave + random.nextDouble() * 0.05; // Add small noise
    }


    /**
     * Allows me to get the ECG values
     * @return - access the generated values ;
     */
    public double getValues(int patientId ){
        OutputStrategy outputStrategy = new OutputStrategy() {
            @Override
            public void output(int patientId, long timestamp, String label, String data) {

            }
        };
        generate(patientId , outputStrategy);


        return Values;
    }
}
