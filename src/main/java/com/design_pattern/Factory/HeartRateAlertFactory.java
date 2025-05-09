package com.design_pattern.Factory;

import com.alerts.Alert;
import com.design_pattern.strategy.HeartRateStrategy;

public class HeartRateAlertFactory extends AlertFactory{

    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        Alert alert = new Alert(patientId , condition  , timestamp) ;
        return alert ;
    }

}
