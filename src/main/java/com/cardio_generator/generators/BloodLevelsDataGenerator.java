package com.cardio_generator.generators;

import java.util.Random;
import com.cardio_generator.outputs.OutputStrategy;




/**
 * This class as the title indicates allows us to generate three separate arrays:
 *      1. One for the Basline Cholesterol levels
 *      2. One for the Basline White Cells levels
 *      3. One for the Basline red cells levels

 *  each of which are randomly generated from a generate function that derives from the interface
 *  PatientDataGenerator
 */

public class BloodLevelsDataGenerator implements PatientDataGenerator {
    /**
     *  random  is a random variable that allows us to generate random Blood levels
     *  baselineCholesterol is an array of Cholesterol levels
     *  baselineWhiteCells is an array of values containing white cell levels
     *  baselineRedCells is an array that will be filled with values of red cell levels
     */




    private static final Random random = new Random();
    private final double[] baselineCholesterol;
    private final double[] baselineWhiteCells;
    private final double[] baselineRedCells;
    private double[] Values = new double[3];


    public BloodLevelsDataGenerator(int patientCount) {
        // Initialize arrays to store baseline values for each patient
        baselineCholesterol = new double[patientCount + 1];
        baselineWhiteCells = new double[patientCount + 1];
        baselineRedCells = new double[patientCount + 1];

        // Generate baseline values for each patient

        /**
         * This for loop allows us to generate realistic by varing values of cholesterol , white cell
         * quantity and red cell quantity.
         */


        for (int i = 1; i <= patientCount; i++) {
            baselineCholesterol[i] = 150 + random.nextDouble() * 50; // Initial random baseline
            baselineWhiteCells[i] = 4 + random.nextDouble() * 6; // Initial random baseline
            baselineRedCells[i] = 4.5 + random.nextDouble() * 1.5; // Initial random baseline
        }
    }

    /**
     * @param patientId - the user give the patient we want to generate values for
     * @param outputStrategy - is an interface that contains the patients id ,  timestamp , a label of the properties ( Cholesterol - would be a label)
     *                       as well as the value for the specific label
     *
     * This overrided method , adds even more variability to the output by generating another random number that is scaled in a realistic way such as we get
     * believable variablility.
     *
     * @throws Exception as maybe the patient ID which serves as a index in an array may cause out of bounds errors
     */

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Generate values around the baseline for realism
            double cholesterol = baselineCholesterol[patientId] + (random.nextDouble() - 0.5) * 10; // Small variation
            double whiteCells = baselineWhiteCells[patientId] + (random.nextDouble() - 0.5) * 1; // Small variation
            double redCells = baselineRedCells[patientId] + (random.nextDouble() - 0.5) * 0.2; // Small variation
            Values[0] = cholesterol ;
            Values[1] = whiteCells ;
            Values[2] = redCells ;

            // Output the generated values
            outputStrategy.output(patientId, System.currentTimeMillis(), "Cholesterol", Double.toString(cholesterol));
            outputStrategy.output(patientId, System.currentTimeMillis(), "WhiteBloodCells",
                    Double.toString(whiteCells));
            outputStrategy.output(patientId, System.currentTimeMillis(), "RedBloodCells", Double.toString(redCells));
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood levels data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }

    /**
     * getter method that allows us to retrieve the values generated
     * Values[0]  - return Cholesterol
     * Values[1]  - return White Blood cells
     * Values[2] - return Red Blood cells
     *
     * @return - all generated values;
     */
    public double[] getValues(int patientId){
        try {
            // Generate values around the baseline for realism
            double cholesterol = baselineCholesterol[patientId] + (random.nextDouble() - 0.5) * 10; // Small variation
            double whiteCells = baselineWhiteCells[patientId] + (random.nextDouble() - 0.5) * 1; // Small variation
            double redCells = baselineRedCells[patientId] + (random.nextDouble() - 0.5) * 0.2; // Small variation
            Values[0] = cholesterol ;
            Values[1] = whiteCells ;
            Values[2] = redCells ;

        } catch (Exception e) {
            System.err.println("An error occurred while generating blood levels data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
        return Values ;
    }
}
