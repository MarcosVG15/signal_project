package com.cardio_generator.outputs;


import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * It is a class that implements {@link OutputStrategy}
 * to save all patient information in a file of our choice
 */
// The name of the File was lower case and was fixed to follow the CamelCase writing convention
public class FileOutputStrategy implements OutputStrategy {

    private String baseDirectory; // Names of Variables have to start with lower case and followed with Capital case for the next word

    // constants should be static
    public static final ConcurrentHashMap<String, String> FILE_MAP = new ConcurrentHashMap<>(); // Any time we encounter a final , we have to write the name in capitals;

    /**
     * The constructor takes in a directory as a string and assignes it as a global private variable
     * @param baseDirectory - address of the directory where we want to save the data
     */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }


    /**
     * This method  creates if needed a file where the patient information will be stored in
     *
     * @throws IOException - makes sure that the filed is created without any error and if there is that they are flagged.
     *
     * After taking care of creating a file we now start adding the values to it
     * @throws IOException makes sure that the file gets filled without any issues
     *
     * @param patientId - the ID of the patient
     * @param timestamp -  at what time the test was "taken"
     * @param label - what the test consisted of
     * @param data- what the value was
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // FilePath variable should be lower case
        // Set the FilePath variable
        String filePath = FILE_MAP.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        // Code had to be linewrapped
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(
                        Paths.get(filePath),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n",
                    patientId, timestamp, label, data); // code had to be line - wrapped
        } catch (IOException e) {// change Exception to IOException as exceptions should be the most specific possible
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}