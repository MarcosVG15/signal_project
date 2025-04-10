package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * this is an interface that makes sure all classes that implement it have a generate method which takes in patientID
 *  and use the other interface OutputStrategy
 */

public interface PatientDataGenerator {
    void generate(int patientId, OutputStrategy outputStrategy);
}
