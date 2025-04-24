package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * this interface contains a method that allows to generate/ modify the data for a specific patient
 */

public interface PatientDataGenerator {

    /**
     It takes in a patientId and a OutputStrategy interface with which each
     class that implements this interface will change the content of this
     method to "save" specific data in the outputStrategy.

     @param patientId   - ID for specific patient such that we know who the data belongs to
     @param outputStrategy - interface that allows us to save and output the information tailored to the specific patient
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
