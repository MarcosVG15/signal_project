package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;

public class WebSocketClientAdapter extends WebSocketClient implements DataReader{

    private DataStorage storage ;

    public WebSocketClientAdapter(URI serverUri, DataStorage dataStorage) {
        super(serverUri);
        this.storage = dataStorage;
    }

    @Override
    public void startStreaming() throws IOException {
        this.connect();

    }

    @Override
    public void stopStreaming() throws IOException {
        this.close();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {

        System.out.println("Connect Has been Made");

    }

    /**
     * This takes the message that is formatted in the webSocketOutputStrategy and is then added into the data storage
     * The data storage will process and accumulate this data
     * @param message - message that contains the information for the hospital
     */
    @Override
    public void onMessage(String message ) {
        System.out.println("Received Message : "+ message);

        try{
            String[] values = message.split(",");

            if(values.length != 4){
                System.out.println("Invalid Message : "+ message);
                return;
            }
            else{

                int patientID = Integer.parseInt(values[0]);
                long timestamp = Long.parseLong(values[1]);
                String recordType = values[2];
                String rawValue = values[3];
                double data = Double.parseDouble(rawValue.replace("%", ""));

                storage.addPatientData(patientID,data,recordType,timestamp);

            }

        }catch(Exception ex){
            System.out.println("Failed to parse or store the message : "+ ex.getMessage());
        }

    }

    /**
     *  Is called once a web socket has been closed
     * @param code - based on protocol will give out a number that represents specific reasons why it has closed
     * @param reason - description of why is was closed
     * @param remote - whether it has been initiated by a remote device
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed with exit code " + code +
                ", reason: " + reason + ", remote: " + remote);
    }

    /**
     * When an error occures , this message will be displayed
     * @param e - Exception that has been called
     */
    @Override
    public void onError(Exception e) {
        System.out.println("WebSocket Error : "+ e.getMessage());

    }
}
