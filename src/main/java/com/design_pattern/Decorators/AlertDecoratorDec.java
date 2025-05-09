package com.design_pattern.Decorators;


public class AlertDecoratorDec implements AlertInterfaceDec {

    private AlertInterfaceDec alert  ;

    public AlertDecoratorDec(AlertInterfaceDec alert){
        this.alert = alert;
    }

    @Override
    public String getPatientId() {
        return alert.getPatientId();
    }

    @Override
    public String getCondition() {
        return alert.getCondition();
    }

    @Override
    public long getTimestamp() {
        return alert.getTimestamp();
    }
}
