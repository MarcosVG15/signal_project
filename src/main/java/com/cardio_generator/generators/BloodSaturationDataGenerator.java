package com.cardio_generator.generators;

import java.util.Random;
import com.cardio_generator.outputs.OutputStrategy;

/**
 * This class generates blood saturation levels by using :
 * random variable - to create unpredictable and varying results
 * integer array of containing the last saturation levels
 */


public class BloodSaturationDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private int[] lastSaturationValues;


    /**
     * This constructor initializes the array with random values from 95 to 100
     * @param patientCount - represents amount of patient we want to generate random blood saturation levels for
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

    /**
     * this method generate the random value for that represents the blood saturation levels for an individual patient,
     * it does this by adding more variability to the already randomised array by adding another random value that ranges from -1 to 1
     * furthermore like many other classes it has a "checker" line that makes sure that the value is between a certain
     * even after randomization in this case the value has to be between 90 and 100.
     *
     * @param patientId - the id of an individual patient
     * @param outputStrategy - output strategy is an interface that contains the patient id, the timestamp , the lable
     *                       of what we are generating and the value for that specific timestamp. This will allow us
     *                       later on to print the values with way more ease
     *
     * @throws Exception - Makes sure program does stop if the id makes the array out of bounds or some other error occures
     */

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");


        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
