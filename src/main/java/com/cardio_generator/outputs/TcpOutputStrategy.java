package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;


    /**
     * This code firstly creates a server connection by creating a unique access "link" to a server using the ServerSocket
     * method which is then used to extract their information and combine it all together in a database such that anyone can
     * retrieve all the data from all the patients.
     *
     * @param port - unique value that we can conceptualise as the "machine" feeding the data of the patient through a
     *             specific port
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is an overrided method that prints the data from the patients into the terminal
     *
     * @param patientId - The ID of the patient which you want to get the values from
     * @param timestamp - The time at which the person was said
     * @param label - the test that was run on the patient
     * @param data - the data collected from the test
     *
     * This interface is used by all the generators
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
