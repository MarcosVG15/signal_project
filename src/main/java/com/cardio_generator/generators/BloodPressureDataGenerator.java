package com.cardio_generator.generators;

import java.util.Random;
import com.cardio_generator.outputs.OutputStrategy;



public class BloodPressureDataGenerator implements PatientDataGenerator {

    /**
     * This class generate values for blood pressure of a patient
     *
     *  random - is a random variable such that all patient has a different result
     *  lastSystolicValues - is an array of integers that contains systolic levels which represents the
     *                           pressure in the arteries when you heart gave a pump
     *  lastDiastolicValues - is an array of integers that contains the diastolic levels which is represents the
     *                            pressure in the arteries when the heart is at rest between beats
     */


    private static final Random random = new Random();

    private int[] lastSystolicValues;
    private int[] lastDiastolicValues;

    /**
     * This constructor initialises the integer arrays and fills them up with a preliminary set of random values
     * we make sure that the levels are between 110 and 130 for systolic values whilst for the diastolic values
     * the code makes sure that the levels are between 70 and 85
     *
     * @param patientCount  - amount of patients we want to generate blood pressure for
     */


    public BloodPressureDataGenerator(int patientCount) {
        lastSystolicValues = new int[patientCount + 1];
        lastDiastolicValues = new int[patientCount + 1];

        // Initialize with baseline values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSystolicValues[i] = 110 + random.nextInt(20); // Random baseline between 110 and 130
            lastDiastolicValues[i] = 70 + random.nextInt(15); // Random baseline between 70 and 85
        }
    }

    /**
     * This overrided class generates values of a specific patient  by taking the array generated in the constructor
     * and adding another randomised value.Following this it makes sure that the newly generated value is still in a
     * realistic range by taking the maximum value from the newSystolicValue and 90 of which we will take the smallest
     * value from the result of the first operation and 180.
     *
     *
     *
     * @param patientId - the id of the patient we want to generate values for
     * @param outputStrategy - is the interface that contains the patients id as well as the property and the
     *                       value of that property
     * @throws Exception - makes sure the patient id doesn't make the array out of bounds
     */


    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            int systolicVariation = random.nextInt(5) - 2; // -2, -1, 0, 1, or 2
            int diastolicVariation = random.nextInt(5) - 2;
            int newSystolicValue = lastSystolicValues[patientId] + systolicVariation;
            int newDiastolicValue = lastDiastolicValues[patientId] + diastolicVariation;
            // Ensure the blood pressure stays within a realistic and safe range
            newSystolicValue = Math.min(Math.max(newSystolicValue, 90), 180);
            newDiastolicValue = Math.min(Math.max(newDiastolicValue, 60), 120);
            lastSystolicValues[patientId] = newSystolicValue;
            lastDiastolicValues[patientId] = newDiastolicValue;

            outputStrategy.output(patientId, System.currentTimeMillis(), "SystolicPressure",
                    Double.toString(newSystolicValue));
            outputStrategy.output(patientId, System.currentTimeMillis(), "DiastolicPressure",
                    Double.toString(newDiastolicValue));
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood pressure data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
