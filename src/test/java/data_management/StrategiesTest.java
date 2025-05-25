package data_management;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataReader;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.design_pattern.Factory.*;
import com.design_pattern.strategy.*;
import org.junit.jupiter.api.Test;

import com.data_management.PatientRecord;

import javax.xml.crypto.Data;
import java.util.List;

class StrategiesTest{


    /**
     * We want a peak in the heart rate which mean that one of these values has to be above average , as you can see
     * we can simulated this condition by adding a series of equal data points such that the average would be 34 and
     * then added a higher value which would trigger the alert
     * @throws IOException - required for the method
     */
    @Test
    void testECGOptimalConditions() throws IOException {
        HeartRateAlertFactory factory = new HeartRateAlertFactory();
        HeartRateStrategy heartRateStrategy = new HeartRateStrategy(factory);
        DataStorage dataStorage = DataStorage.getInstance();
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient patient = new Patient(2);
        patient.addRecord(34 , "ECG" ,1);
        patient.addRecord(34 , "ECG" ,2);
        patient.addRecord(34 , "ECG" ,3);
        patient.addRecord(34 , "ECG" ,4);
        patient.addRecord(34 , "ECG" ,5);
        patient.addRecord(67 , "ECG" ,6);

        heartRateStrategy.checkAlert(2,patient.getRecords() , generator);

        Alert expectedAlert1 = new Alert("2" , "ECG" , 6);
        assertEquals(generator.getAlerts().get(0).getPatientId() ,expectedAlert1.getPatientId());
        assertEquals(generator.getAlerts().get(0).getCondition() ,expectedAlert1.getCondition());
        assertEquals(generator.getAlerts().get(0).getTimestamp() ,expectedAlert1.getTimestamp());


    }


    /**
     * We want to check what happens to the system if there is no data , and if it will cause a bug
     * @throws IOException
     */
    @Test
    void testECGEdgeCase_NoRecords() throws IOException {
        HeartRateAlertFactory factory = new HeartRateAlertFactory();
        HeartRateStrategy heartRateStrategy = new HeartRateStrategy(factory);
        DataStorage dataStorage = DataStorage.getInstance();
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient patient = new Patient(2);
        assertDoesNotThrow(() -> heartRateStrategy.checkAlert(2,patient.getRecords() , generator));


    }

    /**
     * We want to make sure that there are no error even if the data fluctuates or even gets lower than average
     * @throws IOException
     */
    @Test
    void testECG_NoTrigger() throws IOException {
        HeartRateAlertFactory factory = new HeartRateAlertFactory();
        HeartRateStrategy heartRateStrategy = new HeartRateStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient patient = new Patient(1);

        patient.addRecord(13 , "ECG" ,1);
        patient.addRecord(32 , "ECG" ,2);
        patient.addRecord(37 , "ECG" ,3);
        patient.addRecord(34 , "ECG" ,4);
        patient.addRecord(30 , "ECG" ,5);
        patient.addRecord(0 , "ECG" ,6);


        heartRateStrategy.checkAlert(1,patient.getRecords() , generator);

        assertEquals(generator.getAlerts().size() , 0);

    }

    /**
     * We want to trigger an alert when the button is pressed
     */
    @Test
    void testEmergencyButtonStrategy(){
        EmergencyButtonFactory factory = new EmergencyButtonFactory();
        EmergencyButtonStrategy emergencyButtonStrategy = new EmergencyButtonStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient patient = new Patient(1);

        patient.addRecord(1 , "EmergencyButton" ,1);


        emergencyButtonStrategy.checkAlert(1,patient.getRecords() , generator);

        Alert expectedAlert1 = new Alert("1" , "EmergencyButton" , 1);
        assertEquals(generator.getAlerts().get(0).getPatientId() ,expectedAlert1.getPatientId());
        assertEquals(generator.getAlerts().get(0).getCondition() ,expectedAlert1.getCondition());
        assertEquals(generator.getAlerts().get(0).getTimestamp() ,expectedAlert1.getTimestamp());

    }

    /**
     * We want the system to be redundant to unforeseen circumstances such as having a value that would be impossible
     */
    @Test
    void testEmergencyButton_WeirdCases(){
        EmergencyButtonFactory factory = new EmergencyButtonFactory();
        EmergencyButtonStrategy emergencyButtonStrategy = new EmergencyButtonStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient patient = new Patient(1);

        patient.addRecord(12 , "EmergencyButton" ,System.currentTimeMillis());
        patient.addRecord(0 , "EmergencyButton" ,System.currentTimeMillis());


        emergencyButtonStrategy.checkAlert(1,patient.getRecords() , generator);
        assertEquals(generator.getAlerts().size() , 0);

    }

    /**
     * We test the most basic conditions where we would expect an alert to be triggered in an easy fashion
     */
    @Test
    void testBloodPressureStrategy_TriggerThresholds(){
        BloodPressureAlertFactory factory = new BloodPressureAlertFactory();
        BloodPressureStrategy strategy = new BloodPressureStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient patient = new Patient(1);

        //Too high pressure
        patient.addRecord(181 , "SystolicPressure" ,1);
        patient.addRecord(121 , "DiastolicPressure" ,2);

        //Too low pressure
        patient.addRecord(89 , "SystolicPressure" ,3);
        patient.addRecord(59 , "DiastolicPressure" ,4);

        strategy.checkAlert(1,patient.getRecords() , generator);

        assertEquals(generator.getAlerts().size() , 4); // only testing for size but as you can see they do match

    }

    /**
     * Similarly we want to test whether we would trigger and alert in the best scenario for decreasing systolic pressure
     * As well as see whether the system can still detect other heart pressure anomalies
     */
    @Test
    void testBloodPressureStrategy_DecreasingPressureTrigger_Systolic(){
        BloodPressureAlertFactory factory = new BloodPressureAlertFactory();
        BloodPressureStrategy strategy = new BloodPressureStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient p1 = new Patient(1);

        //Decrease in systolic pressure
        p1.addRecord(179 , "SystolicPressure" ,7);
        p1.addRecord(151 , "SystolicPressure" ,6);
        p1.addRecord(121 , "SystolicPressure" ,5);
        p1.addRecord(24 , "DiastolicPressure" ,4);
        p1.addRecord(94 , "SystolicPressure" ,8);

        strategy.checkAlert(1,p1.getRecords() , generator);

        Alert expectedAlert1 = new Alert("1" , "DECREASING SYSTOLIC PRESSURE" , 8);
        assertEquals(generator.getAlerts().get(1).getPatientId() ,expectedAlert1.getPatientId());
        assertEquals(generator.getAlerts().get(1).getCondition() ,expectedAlert1.getCondition());
        assertEquals(generator.getAlerts().get(1).getTimestamp() ,expectedAlert1.getTimestamp());

        Alert expectedAlert2 = new Alert("1" , "DiastolicPressure" , 4);
        assertEquals(generator.getAlerts().get(0).getPatientId() ,expectedAlert2.getPatientId());
        assertEquals(generator.getAlerts().get(0).getCondition() ,expectedAlert2.getCondition());
        assertEquals(generator.getAlerts().get(0).getTimestamp() ,expectedAlert2.getTimestamp());


    }

    /**
     * We want to check that we need to have contsistent decrease to trigger the alarm and that it will not trigger once
     * The patient it reestablished.
     */

    @Test
    void testBloodPressureStrategy_AdvancedDecrease_Systolic(){
        BloodPressureAlertFactory factory = new BloodPressureAlertFactory();
        BloodPressureStrategy strategy = new BloodPressureStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;



        Patient p2 = new Patient(1);

        p2.addRecord(179 , "SystolicPressure" ,7);
        p2.addRecord(151 , "SystolicPressure" ,6);
        p2.addRecord(121 , "SystolicPressure" ,5);
        p2.addRecord(140 , "SystolicPressure" ,8);
        p2.addRecord(160 , "SystolicPressure" ,9);
        p2.addRecord(100 , "SystolicPressure" ,13);

        strategy.checkAlert(2,p2.getRecords() , generator);

        assertEquals(generator.getAlerts().size() , 0);


    }

    /**
     * Similarly to the systolic we want to see whether it can accurately measure the decrease in diastolic pressure
     */
    @Test
    void testBloodPressureStrategy_DecreasingPressureTrigger_Diastolic(){
        BloodPressureAlertFactory factory = new BloodPressureAlertFactory();
        BloodPressureStrategy strategy = new BloodPressureStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient p1 = new Patient(1);

        //Decrease in systolic pressure
        p1.addRecord(119 , "DiastolicPressure" ,7);
        p1.addRecord(108 , "DiastolicPressure" ,6);
        p1.addRecord(97 , "DiastolicPressure" ,5);
        p1.addRecord(182 , "SystolicPressure" ,8);
        p1.addRecord(68 , "DiastolicPressure" ,9);

        strategy.checkAlert(1,p1.getRecords() , generator);

        Alert expectedAlert1 = new Alert("1" , "DECREASING DIASTOLIC PRESSURE" , 9);
        assertEquals(generator.getAlerts().get(1).getPatientId() ,expectedAlert1.getPatientId());
        assertEquals(generator.getAlerts().get(1).getCondition() ,expectedAlert1.getCondition());
        assertEquals(generator.getAlerts().get(1).getTimestamp() ,expectedAlert1.getTimestamp());

        Alert expectedAlert2 = new Alert("1" , "SystolicPressure" , 8);
        assertEquals(generator.getAlerts().get(0).getPatientId() ,expectedAlert2.getPatientId());
        assertEquals(generator.getAlerts().get(0).getCondition() ,expectedAlert2.getCondition());
        assertEquals(generator.getAlerts().get(0).getTimestamp() ,expectedAlert2.getTimestamp());

    }

    /**
     * Mirroring the systolic tests where we see if the interference and fluctuation will trigger an alert
     */

    @Test
    void testBloodPressureStrategy_AdvancedDecrease_Diastolic(){
        BloodPressureAlertFactory factory = new BloodPressureAlertFactory();
        BloodPressureStrategy strategy = new BloodPressureStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;



        Patient p2 = new Patient(1);

        p2.addRecord(119 , "DiastolicPressure" ,7);
        p2.addRecord(108 , "DiastolicPressure" ,6);
        p2.addRecord(97 , "DiastolicPressure" ,5);
        p2.addRecord(24 , "SystolicPressure" ,4);
        p2.addRecord(90 , "DiastolicPressure" ,3);
        p2.addRecord(100 , "DiastolicPressure" ,2);
        p2.addRecord(110 , "DiastolicPressure" ,1);


        strategy.checkAlert(1,p2.getRecords() , generator);

        Alert expectedAlert2 = new Alert("1" , "SystolicPressure" , 4);
        assertEquals(generator.getAlerts().get(0).getPatientId() ,expectedAlert2.getPatientId());
        assertEquals(generator.getAlerts().get(0).getCondition() ,expectedAlert2.getCondition());
        assertEquals(generator.getAlerts().get(0).getTimestamp() ,expectedAlert2.getTimestamp());
    }


    /**
     * Test a simple case where there is a difference of saturation that is significant but because it isn't a DROP in saturation
     * we do not expect an alert
     */
    @Test
    void testOxygenSaturation_Threshold(){
        OxygenSaturationAlertFactory factory = new OxygenSaturationAlertFactory();
        OxygenSaturationStrategy oxygenSaturationStrategy = new OxygenSaturationStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient patient = new Patient(1);

        patient.addRecord(78 , "Saturation" ,1);
        patient.addRecord(93 , "Saturation" ,2);



        oxygenSaturationStrategy.checkAlert(1,patient.getRecords() , generator);

        Alert expectedAlert2 = new Alert("1" , "Saturation" , 1);
        assertEquals(generator.getAlerts().get(0).getPatientId() ,expectedAlert2.getPatientId());
        assertEquals(generator.getAlerts().get(0).getCondition() ,expectedAlert2.getCondition());
        assertEquals(generator.getAlerts().get(0).getTimestamp() ,expectedAlert2.getTimestamp());
    }

    /**
     * This test tries to induce an error by rapidly decreasing the pressure in an interval of 5 minutes which would trigger an
     * alert and then a second very big drop in pressure , but because the interval is larger than 10 minutes, we do not analyse
     */
    @Test
    void testOxygenSaturation_Decrease(){
        OxygenSaturationAlertFactory factory = new OxygenSaturationAlertFactory();
        OxygenSaturationStrategy oxygenSaturationStrategy = new OxygenSaturationStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient patient = new Patient(1);

        patient.addRecord(95, "Saturation" ,1);
        patient.addRecord(97 , "Saturation" ,2);
        patient.addRecord(98 , "Saturation" ,3);
        patient.addRecord(93 , "Saturation" ,4+1000*60*5);
        patient.addRecord(99 , "Saturation" ,5+1000*60*10);
        patient.addRecord(93 , "Saturation" ,6+1000*60*20);

        oxygenSaturationStrategy.checkAlert(1,patient.getRecords() , generator);

        Alert expectedAlert2 = new Alert("1" , "RAPID DECREASE IN OXYGEN " , 300004);
        assertEquals(generator.getAlerts().get(0).getPatientId() ,expectedAlert2.getPatientId());
        assertEquals(generator.getAlerts().get(0).getCondition() ,expectedAlert2.getCondition());
        assertEquals(generator.getAlerts().get(0).getTimestamp() ,expectedAlert2.getTimestamp());
    }

    /**
     * We expect there to bee three alerts , One for the first two initial values where they are clearly both undert the
     * thresholds , then we want to pass a few other saturation values to see whether the alarm would still trigger
     * finally we effectively "reset" the systolic pressure and then drop it again to see whether the test is responsive to change
     */

    @Test
    void testHypotensiveHypoxemia(){
        HypotensiveHypoxemiaAlertFactory factory = new HypotensiveHypoxemiaAlertFactory();
        HypotensiveHypoxemiaStrategy hypoxemiaStrategy = new HypotensiveHypoxemiaStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient patient = new Patient(1);

        patient.addRecord(78, "SystolicPressure" ,1);
        patient.addRecord(76 , "Saturation" ,2);
        patient.addRecord(98 , "Saturation" ,3);
        patient.addRecord(67 , "Saturation" ,4+1020);
        patient.addRecord(100, "SystolicPressure" ,5+ 2020);
        patient.addRecord(78, "SystolicPressure" ,6+2020);
        patient.addRecord(67 , "Saturation" ,7+2020);


        hypoxemiaStrategy.checkAlert(1,patient.getRecords() , generator);

        Alert expectedAlert1 = new Alert("1" , "HYPOTENSIVE HYPOXEMIA ALERT" , 2);
        assertEquals(generator.getAlerts().get(0).getPatientId() ,expectedAlert1.getPatientId());
        assertEquals(generator.getAlerts().get(0).getCondition() ,expectedAlert1.getCondition());
        assertEquals(generator.getAlerts().get(0).getTimestamp() ,expectedAlert1.getTimestamp());

        Alert expectedAlert2 = new Alert("1" , "HYPOTENSIVE HYPOXEMIA ALERT" , 3);
        assertEquals(generator.getAlerts().get(1).getPatientId() ,expectedAlert2.getPatientId());
        assertEquals(generator.getAlerts().get(1).getCondition() ,expectedAlert2.getCondition());
        assertEquals(generator.getAlerts().get(1).getTimestamp() ,expectedAlert2.getTimestamp());

        Alert expectedAlert3 = new Alert("1" , "HYPOTENSIVE HYPOXEMIA ALERT" , 2027);
        assertEquals(generator.getAlerts().get(2).getPatientId() ,expectedAlert3.getPatientId());
        assertEquals(generator.getAlerts().get(2).getCondition() ,expectedAlert3.getCondition());
        assertEquals(generator.getAlerts().get(2).getTimestamp() ,expectedAlert3.getTimestamp());


        
    }





}
