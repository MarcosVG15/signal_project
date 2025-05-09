package com.design_pattern.strategy;

import com.alerts.AlertGenerator;
import com.data_management.PatientRecord;

import java.util.List;

public interface AlertInterface {
    void checkAlert(int patientId, List<PatientRecord> record, AlertGenerator generator);
}
