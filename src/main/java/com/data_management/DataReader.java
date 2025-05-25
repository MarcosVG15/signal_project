package com.data_management;

import java.io.IOException;

public interface DataReader {

    void startStreaming() throws IOException;
    void stopStreaming() throws IOException;


}
