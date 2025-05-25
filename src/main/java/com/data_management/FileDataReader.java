package com.data_management;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileDataReader implements FileDataReaderInterface {
    private final Path path;

    public FileDataReader(String path) {
        this.path = Paths.get(path);

    }


    public void readData(DataStorage dataStorage) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.txt")) {
            for (Path file : stream) {
                fileScanner(file, dataStorage);
            }
        }
    }
        public void fileScanner (Path fPath, DataStorage dataStorage) throws IOException {
            Pattern style = Pattern.compile("Patient ID: (\\d+), Timestamp: (\\d+), Label: ([^,]+), Data: ([-+]?\\d*\\.?\\d+)");


            Files.lines(fPath).forEach(line -> {
                Matcher matcher = style.matcher(line);
                if (matcher.matches()) {
                    try {
                        int patientId = Integer.parseInt(matcher.group(1));
                        double measurementValue = Double.parseDouble(matcher.group(4));
                        String recordType = (matcher.group(3));
                        long timestamp = Long.parseLong(matcher.group(2));

                        dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
                       // System.out.println(patientId + " | "+ timestamp + " | "+recordType+ " | "+ measurementValue);

                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing" + line);
                    }

                }
            });


        }
    }

