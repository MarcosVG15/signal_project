package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;


// The name of the File was lower case and was fixed to follow the CamelCase writing convention
public class FileOutputStrategy implements OutputStrategy {

    private String baseDirectory; // Names of Variables have to start with lower case and followed with Capital case for the next word

    // constants should be static
    public static final ConcurrentHashMap<String, String> FILE_MAP = new ConcurrentHashMap<>(); // Any time we encounter a final , we have to write the name in capitals;

    public FileOutputStrategy(String baseDirectory) {

        this.baseDirectory = baseDirectory;
    }

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