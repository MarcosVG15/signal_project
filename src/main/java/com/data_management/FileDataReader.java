package com.data_management;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileDataReader implements DataReader {
    private final Path path;

    public FileDataReader(String path) {
        this.path = Paths.get(path);

    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.txt")) {
            for (Path file : stream) {
                fileScanner(file, dataStorage);
            }
        }
    }
        public void fileScanner (Path fPath, DataStorage dataStorage) throws IOException {
            Pattern style = Pattern.compile(" Patient ID: (\\d+), Timestamp: (\\d+), Label: (\\w+), Data: ([-+]?\\d*\\.?\\d+)"
            );
            Files.lines(fPath).forEach(line -> {
                Matcher matches = style.matcher(line);
                if (matches.matches()) {
                    try {
                        int patientId = Integer.parseInt(matches.group(1));
                        double measurementValue = Integer.parseInt(matches.group(4));
                        String recordType = (matches.group(3));
                        long timestamp = Long.parseLong(matches.group(2));

                        dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);

                    } catch (NumberFormatException e) {
                        System.out.println("Error in parsing" + line);
                    }

                }
            });


        }
    }

