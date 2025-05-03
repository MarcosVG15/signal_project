package com.design_pattern.Factory;

import com.alerts.Alert;

public abstract class AlertFactory {

    public Alert createAlert(String patientId , String condition , long timestemp) {
        Alert alert = null ;
        return alert ;
    }
}
