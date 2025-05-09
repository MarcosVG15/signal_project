package com.design_pattern.Decorators;

// Represents an alert
public interface AlertInterfaceDec {
    public String getPatientId();

    public String getCondition() ;

    public long getTimestamp();
}