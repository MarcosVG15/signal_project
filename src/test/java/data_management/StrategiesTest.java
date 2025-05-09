package data_management;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;

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

    @Test
    void testECGOptimalConditions() throws IOException {
        HeartRateAlertFactory factory = new HeartRateAlertFactory();
        HeartRateStrategy heartRateStrategy = new HeartRateStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient patient = new Patient(2);
        patient.addRecord(34 , "ECG" ,System.currentTimeMillis());
        patient.addRecord(34 , "ECG" ,System.currentTimeMillis());
        patient.addRecord(34 , "ECG" ,System.currentTimeMillis());
        patient.addRecord(34 , "ECG" ,System.currentTimeMillis());
        patient.addRecord(34 , "ECG" ,System.currentTimeMillis());
        patient.addRecord(12 , "ECG" ,System.currentTimeMillis());

        heartRateStrategy.checkAlert(2,patient.getRecords() , generator);
    }

    @Test
    void testECGEdgeCase_NoRecords() throws IOException {
        HeartRateAlertFactory factory = new HeartRateAlertFactory();
        HeartRateStrategy heartRateStrategy = new HeartRateStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient patient = new Patient(2);

        heartRateStrategy.checkAlert(2,patient.getRecords() , generator);
    }


    @Test
    void testECG_NoTrigger() throws IOException {
        HeartRateAlertFactory factory = new HeartRateAlertFactory();
        HeartRateStrategy heartRateStrategy = new HeartRateStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient patient = new Patient(1);

        patient.addRecord(13 , "ECG" ,System.currentTimeMillis());
        patient.addRecord(32 , "ECG" ,System.currentTimeMillis());
        patient.addRecord(37 , "ECG" ,System.currentTimeMillis());
        patient.addRecord(34 , "ECG" ,System.currentTimeMillis());
        patient.addRecord(30 , "ECG" ,System.currentTimeMillis());
        patient.addRecord(30 , "ECG" ,System.currentTimeMillis());


        heartRateStrategy.checkAlert(1,patient.getRecords() , generator);
    }


    @Test
    void testEmergencyButtonStrategy(){
        EmergencyButtonFactory factory = new EmergencyButtonFactory();
        EmergencyButtonStrategy emergencyButtonStrategy = new EmergencyButtonStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient patient = new Patient(1);

        patient.addRecord(1 , "EmergencyButton" ,System.currentTimeMillis());


        emergencyButtonStrategy.checkAlert(1,patient.getRecords() , generator);

    }

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

    }

    @Test
    void testBloodPressureStrategy_TriggerThresholds(){
        BloodPressureAlertFactory factory = new BloodPressureAlertFactory();
        BloodPressureStrategy strategy = new BloodPressureStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient patient = new Patient(1);

        //Too high pressure
        patient.addRecord(181 , "SystolicPressure" ,System.currentTimeMillis());
        patient.addRecord(121 , "DiastolicPressure" ,System.currentTimeMillis());

        //Too low pressure
        patient.addRecord(89 , "SystolicPressure" ,System.currentTimeMillis());
        patient.addRecord(59 , "DiastolicPressure" ,System.currentTimeMillis());

        strategy.checkAlert(1,patient.getRecords() , generator);

    }

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



    }
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
        p2.addRecord(24 , "DiastolicPressure" ,4);
        p2.addRecord(94 , "SystolicPressure" ,8);
        p2.addRecord(179 , "SystolicPressure" ,7);
        p2.addRecord(130 , "SystolicPressure" ,6);
        p2.addRecord(110 , "SystolicPressure" ,5);
        p2.addRecord(24 , "DiastolicPressure" ,4);
        p2.addRecord(91 , "SystolicPressure" ,8);

        strategy.checkAlert(2,p2.getRecords() , generator);
    }

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
        p1.addRecord(68 , "DiastolicPressure" ,5);

        strategy.checkAlert(1,p1.getRecords() , generator);

    }
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
        p2.addRecord(65 , "DiastolicPressure" ,8);
        p2.addRecord(80 , "DiastolicPressure" ,7);
        p2.addRecord(89 , "DiastolicPressure" ,6);
        p2.addRecord(100 , "DiastolicPressure" ,5);
        p2.addRecord(186 , "SystolicPressure" ,4);
        p2.addRecord(91 , "DiastolicPressure" ,8);

        strategy.checkAlert(2,p2.getRecords() , generator);
    }


    @Test
    void testOxygenSaturation_Threshold(){
        OxygenSaturationAlertFactory factory = new OxygenSaturationAlertFactory();
        OxygenSaturationStrategy oxygenSaturationStrategy = new OxygenSaturationStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient patient = new Patient(1);

        patient.addRecord(78 , "Saturation" ,System.currentTimeMillis());
        patient.addRecord(93 , "Saturation" ,System.currentTimeMillis());


        oxygenSaturationStrategy.checkAlert(1,patient.getRecords() , generator);

    }


    @Test
    void testOxygenSaturation_Decrease(){
        OxygenSaturationAlertFactory factory = new OxygenSaturationAlertFactory();
        OxygenSaturationStrategy oxygenSaturationStrategy = new OxygenSaturationStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient patient = new Patient(1);

        patient.addRecord(95, "Saturation" ,System.currentTimeMillis());
        patient.addRecord(97 , "Saturation" ,System.currentTimeMillis());
        patient.addRecord(98 , "Saturation" ,System.currentTimeMillis());
        patient.addRecord(93 , "Saturation" ,System.currentTimeMillis()+1000*60*5);
        patient.addRecord(99 , "Saturation" ,System.currentTimeMillis()+1000*60*10);
        patient.addRecord(93 , "Saturation" ,System.currentTimeMillis()+1000*60*20);

        oxygenSaturationStrategy.checkAlert(1,patient.getRecords() , generator);

    }

    @Test
    void testHypotensiveHypoxemia(){
        HypotensiveHypoxemiaAlertFactory factory = new HypotensiveHypoxemiaAlertFactory();
        HypotensiveHypoxemiaStrategy hypoxemiaStrategy = new HypotensiveHypoxemiaStrategy(factory);
        DataStorage dataStorage = null;
        AlertGenerator generator = new AlertGenerator(dataStorage) ;

        Patient patient = new Patient(1);

        patient.addRecord(78, "SystolicPressure" ,System.currentTimeMillis());
        patient.addRecord(76 , "Saturation" ,System.currentTimeMillis());
        patient.addRecord(98 , "Saturation" ,System.currentTimeMillis());
        patient.addRecord(67 , "Saturation" ,System.currentTimeMillis()+1020);
        patient.addRecord(100, "SystolicPressure" ,System.currentTimeMillis()+ 2020);
        patient.addRecord(78, "SystolicPressure" ,System.currentTimeMillis()+2020);
        patient.addRecord(67 , "Saturation" ,System.currentTimeMillis()+2020);


        hypoxemiaStrategy.checkAlert(1,patient.getRecords() , generator);

    }


}
