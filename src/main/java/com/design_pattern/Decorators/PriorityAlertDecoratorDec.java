package com.design_pattern.Decorators;

public class PriorityAlertDecoratorDec extends AlertDecoratorDec {

    public PriorityAlertDecoratorDec(AlertInterfaceDec alert) {
        super(alert);
    }

    public String getCriticalCondition(int Importance){
        switch (Importance){
            case(2):
                return "CRITICAL " + super.getCondition();
            case(1):
                return "IMPORTANT " + super.getCondition();
            default:
                return super.getCondition();
        }

    }


}
