package com.cardio_generator.generators;

import java.util.Random; // imports should be blocked together
import com.cardio_generator.outputs.OutputStrategy;



/**
    * This class randomly generates a set of patients that trigger and Alert. It utilises a random number generator and
    * specifc thresholds such that there is a 90 percent probability that a patient does trigger the alert, and inversely
    * a 10 percent probability that a given patient triggers the Alert.
 */

public class AlertGenerator implements PatientDataGenerator {

    /**
        * randomGenerator - is a randomising function allowing us to get a probabilistic approach as to who triggers the alert
        * RESOLUTION_PROBABILITY - is a static constant that creates the upper threshold under which the numbers
        *                                 smaller than it aren't "flagged".
        * ALERT_RATE_LAMBDA - is the desired frequency we wish to see patients triggert the alarm
        * alertStates - is an array of boolean values that contains by index which person is "flagged ", true meaning
        *                      they are " in danger" and false meaning they are "safe".
     */

    private static final double RESOLUTION_PROBABILITY = 0.9; //Constants have to be defined
    private static final double ALERT_RATE_LAMBDA = 0.1;

    private boolean[] alertStates; // false = resolved, true = pressed // the variable should be in lowerCamelcase
    private Random randomGenerator = new Random(); //should be private


    /**
     * Constructor takes in the amount of patients and creates and array of boolean of size patientCount
     * @param patientCount - amount of patients that we want to generate an alert for
     */

    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1]; // code inside a constructor should be endented
    }


    /**
        * Based on a random variable, this method assigns randomly which patient is going to make the alertStates[] true or false
        * @throws RuntimeException makes sure that the id doesn't make the array out of bounds
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (randomGenerator.nextDouble() < RESOLUTION_PROBABILITY) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                double lambda = ALERT_RATE_LAMBDA; // Average rate (alerts per period), adjust based on desired frequency // the variables didn't follow lowerCamelCase
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (RuntimeException e) {// exceptions should be as specific as they can be
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
