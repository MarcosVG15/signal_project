package com.design_pattern.Factory;

import com.alerts.Alert;

public class EmergencyButtonFactory extends AlertFactory{


    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        Alert alert = new Alert(patientId , condition  , timestamp) ;
        return alert ;
    }
}
