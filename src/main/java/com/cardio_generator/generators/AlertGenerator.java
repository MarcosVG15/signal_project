package com.cardio_generator.generators;

import java.util.Random; // imports should be blocked together
import com.cardio_generator.outputs.OutputStrategy;

public class AlertGenerator implements PatientDataGenerator {

    private static final Random randomGenerator = new Random(); //should be private
    private static final double RESOLUTION_PROBABILITY = 0.9; //Constants have to be defined
    private static final double ALERT_RATE_LAMBDA = 0.1;

    private boolean[] alertStates; // false = resolved, true = pressed // the variable should be in lowercamelcase

    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1]; // code inside a constructor should be endented
    }

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
