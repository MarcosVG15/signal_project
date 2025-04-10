package com.alerts;


 /**
     * In essence this code makes sure that where an "Alert" has been set in another class
     * that we can retrieve the necessary information to diagnose the patient
     */
// Represents an alert
public class Alert {

    /**
     * This class contains three variables :
     * @param patientId - such that we can know which patient trigger the alert
     * @param condition - such that we can understand what the cause of the Alert
     * @param timestamp - to know at what time the patient trigger the Alert
     */

    private String patientId;
    private String condition;
    private long timestamp;

    public Alert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    /**
        * @return  the patient ID , it is an accessor method
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * @return  the patient condition, it is an accessor method
     */
    public String getCondition() {
        return condition;
    }

    /**
     * @return  the timestamp , it is an accessor method
     */
    public long getTimestamp() {
        return timestamp;
    }
}
