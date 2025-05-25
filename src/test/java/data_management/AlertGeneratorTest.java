package data_management;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.design_pattern.Factory.AlertFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient ;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlertGeneratorTest {

    @Mock
    private AlertFactory mockGenerator;

    @Test
    public void testEmergencyButtonPressed() {

        List<PatientRecord> record = new ArrayList<>();

        Patient r1 = new Patient(1);
        r1.addRecord(1,"EmergencyButton" , System.currentTimeMillis());
        r1.addRecord(0,"EmergencyButton" , System.currentTimeMillis());

        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        generator.ButtonEmergency(r1.getRecords());

        Alert expectedAlert = new Alert("1" , "EmergencyButton", System.currentTimeMillis());
        assertEquals(generator.getAlerts().get(0).getCondition(),expectedAlert.getCondition());


        System.out.println(generator.getAlerts());
    }
    @Test
    public void testAbnormalBehaviour() {




        Patient r1 = new Patient(1);
        r1.addRecord(1,"EmergencyButton" , 1);
        r1.addRecord(0,"EmergencyButton" , 2);
        r1.addRecord(-1,"EmergencyButton" , 3);
        r1.addRecord(1,"EmergencyButton" , 4);


        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        generator.ButtonEmergency(r1.getRecords());


        Alert expectedAlert1 = new Alert("1" , "EmergencyButton", 1);
        Alert expectedAlert2 = new Alert("1" , "EmergencyButton", 4);

        // I expect all properties to be indentical
        assertEquals(generator.getAlerts().get(0).getPatientId(),expectedAlert1.getPatientId());
        assertEquals(generator.getAlerts().get(0).getCondition(),expectedAlert1.getCondition());
        assertEquals(generator.getAlerts().get(0).getTimestamp(),expectedAlert1.getTimestamp());


        // I expect Two alerts
        assertEquals(generator.getAlerts().get(1).getPatientId(),expectedAlert2.getPatientId());
        assertEquals(generator.getAlerts().get(1).getCondition(),expectedAlert2.getCondition());
        assertEquals(generator.getAlerts().get(1).getTimestamp(),expectedAlert2.getTimestamp());

    }

    @Test
    public void testECGAlert() {

        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        //Shows that program only triggers if the value PEAKS over a certain threshold

        Patient r1 = new Patient(1);
        r1.addRecord(2,"ECG" , 1);
        r1.addRecord(2,"ECG" , 2);
        r1.addRecord(2,"ECG" , 3);
        r1.addRecord(2,"ECG" , 4);
        r1.addRecord(2,"ECG" , 5);
        r1.addRecord(-5,"ECG" , 6);


        generator.ECGAlert(r1.getRecords());

        assertEquals(generator.getAlerts().size() , 0);


        //I have set a threshold of 1 which means that it will only trigger if the value increases by more than 1
        Patient r2 = new Patient(2);
        r2.addRecord(2,"ECG" , 1);
        r2.addRecord(2,"ECG" , 2);
        r2.addRecord(2,"ECG" , 3);
        r2.addRecord(2,"ECG" , 4);
        r2.addRecord(2,"ECG" , 5);
        r2.addRecord(3.1,"ECG" , 6);


        generator.ECGAlert(r2.getRecords());

        Alert expectedAlert2 = new Alert("2" , "ECG", 6);

        assertEquals(generator.getAlerts().get(0).getPatientId(),expectedAlert2.getPatientId());
        assertEquals(generator.getAlerts().get(0).getCondition(),expectedAlert2.getCondition());
        assertEquals(generator.getAlerts().get(0).getTimestamp(),expectedAlert2.getTimestamp());



    }


    /** We need to construct an average of at least 5 data points to evaluate the following data
     * this implies that we need more than 5 records , which is what this tests out. It checks that
     * the system won't crash when there isn't enough data
     */
    @Test
    public void testECGLackOfData() {


        Patient r1 = new Patient(1);
        r1.addRecord(2,"ECG" , System.currentTimeMillis());
        r1.addRecord(2,"ECG" , System.currentTimeMillis());
        r1.addRecord(2,"ECG" , System.currentTimeMillis());
        r1.addRecord(-1,"ECG" , System.currentTimeMillis());


        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        generator.ECGAlert(r1.getRecords());

        assertEquals(generator.getAlerts().size() , 0);
    }

    @Test
    public void testHypotensiveHypoxemiaAlert() {

        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        // Case when there isn't enough systolic blood pressure but enough oxygen
        Patient r1 = new Patient(1);
        r1.addRecord(23,"SystolicPressure" , 1);
        r1.addRecord(95,"Saturation" , 2);
        generator.hypotensiveHypoxemiaAlert(r1.getRecords());
        assertEquals(generator.getAlerts().size() , 0);



        // Case when there is enough blood pressure but not enough oxygen saturation
        Patient r2 = new Patient(2);
        r2.addRecord(90,"SystolicPressure" , 3);
        r2.addRecord(78,"Saturation" , 4);
        generator.hypotensiveHypoxemiaAlert(r2.getRecords());
        assertEquals(generator.getAlerts().size() , 0);



        // Case when there isn't enough of both
        Patient r3 = new Patient(3);
        r3.addRecord(89,"SystolicPressure" , 5);
        r3.addRecord(90,"Saturation" , 6);
        generator.hypotensiveHypoxemiaAlert(r3.getRecords());

        Alert expectedAlert1 = new Alert("3", "HypotensiveHypoxemia",6);

        assertEquals(generator.getAlerts().get(0).getPatientId(),expectedAlert1.getPatientId());
        assertEquals(generator.getAlerts().get(0).getCondition(),expectedAlert1.getCondition());
        assertEquals(generator.getAlerts().get(0).getTimestamp(),expectedAlert1.getTimestamp());


        // Case when there is enough of both
        Patient r4 = new Patient(4);
        r4.addRecord(91,"SystolicPressure" , 7);
        r4.addRecord(93,"Saturation" , 8);
        generator.hypotensiveHypoxemiaAlert(r4.getRecords());
        assertEquals(generator.getAlerts().size() , 1); // same size as previously, It shouldnt change


        // case when there is a data in between
        Patient r5 = new Patient(5);
        r5.addRecord(91,"SystolicPressure" , 9);
        r5.addRecord(89,"SystolicPressure" , 10);
        r5.addRecord(90,"Saturation" , 11);
        generator.hypotensiveHypoxemiaAlert(r5.getRecords());

        Alert expectedAlert2 = new Alert("5", "HypotensiveHypoxemia",11);

        assertEquals(generator.getAlerts().get(1).getPatientId(),expectedAlert2.getPatientId());
        assertEquals(generator.getAlerts().get(1).getCondition(),expectedAlert2.getCondition());
        assertEquals(generator.getAlerts().get(1).getTimestamp(),expectedAlert2.getTimestamp());



    }


    /**
     * Test if the blood saturation is below 92
     */
    @Test
    public void testBloodSaturationLevel() {

        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        Patient r1 = new Patient(1);
        r1.addRecord(91,"Saturation" , 1);

        generator.bloodSaturationAlerts(r1.getRecords());

        Alert expectedAlert1 = new Alert("1", "Saturation",1);

        assertEquals(generator.getAlerts().get(0).getPatientId(),expectedAlert1.getPatientId());
        assertEquals(generator.getAlerts().get(0).getCondition(),expectedAlert1.getCondition());
        assertEquals(generator.getAlerts().get(0).getTimestamp(),expectedAlert1.getTimestamp());


    }


    @Test
    public void testBloodSaturationRapidDrop() {

        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        Patient r1 = new Patient(1);
        r1.addRecord(100,"Saturation" , 1);
        r1.addRecord(99,"Saturation" , 2);
        r1.addRecord(92,"Saturation" , 3+1000*60*5);// rapid drop in a 5 minute interval

        generator.bloodSaturationAlerts(r1.getRecords());

        Alert expectedAlert1 = new Alert("1", "Saturation",3+1000*60*5);

        assertEquals(generator.getAlerts().get(0).getPatientId(),expectedAlert1.getPatientId());
        assertEquals(generator.getAlerts().get(0).getCondition(),expectedAlert1.getCondition());
        assertEquals(generator.getAlerts().get(0).getTimestamp(),expectedAlert1.getTimestamp());
    }

    @Test
    public void testBloodSaturationNoTrigger() {

        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        Patient r1 = new Patient(1);
        r1.addRecord(100,"Saturation" , 1);
        r1.addRecord(100,"Saturation" , 2);
        r1.addRecord(99,"Saturation" , 3);
        r1.addRecord(92,"Saturation" , 4+1000*60*10);// rapid drop in a 10 minute interval shouldn't trigger alert
        r1.addRecord(95,"Saturation" , 5+1000*60*20);

        generator.bloodSaturationAlerts(r1.getRecords());

        assertEquals(generator.getAlerts().size(),0);

    }

    @Test
    public void bloodPressureDataAlertTrigger() {

        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        // Trigger if Systolic Pressure is too high
        Patient r1 = new Patient(1);
        r1.addRecord(182,"SystolicPressure" , 1);
        r1.addRecord(119,"DiastolicPressure" , 2);
        generator.bloodPressureDataAlert(r1.getRecords());

        Alert expectedAlert1 = new Alert("1", "SystolicPressure",1);

        assertEquals(generator.getAlerts().get(0).getPatientId(),expectedAlert1.getPatientId());
        assertEquals(generator.getAlerts().get(0).getCondition(),expectedAlert1.getCondition());
        assertEquals(generator.getAlerts().get(0).getTimestamp(),expectedAlert1.getTimestamp());


        // Trigger if Diastolic Pressure is too high
        Patient r2 = new Patient(2);
        r2.addRecord(178,"SystolicPressure" , 3);
        r2.addRecord(121,"DiastolicPressure" , 4);

        generator.bloodPressureDataAlert(r2.getRecords());

        Alert expectedAlert2 = new Alert("2", "DiastolicPressure",4);

        assertEquals(generator.getAlerts().get(1).getPatientId(),expectedAlert2.getPatientId());
        assertEquals(generator.getAlerts().get(1).getCondition(),expectedAlert2.getCondition());
        assertEquals(generator.getAlerts().get(1).getTimestamp(),expectedAlert2.getTimestamp());

        // Trigger if Systolic Pressure is too low
        Patient r3 = new Patient(3);
        r3.addRecord(87,"SystolicPressure" ,5);
        r3.addRecord(120,"DiastolicPressure" , 6);

        generator.bloodPressureDataAlert(r3.getRecords());

        Alert expectedAlert3 = new Alert("3", "SystolicPressure",5);

        assertEquals(generator.getAlerts().get(2).getPatientId(),expectedAlert3.getPatientId());
        assertEquals(generator.getAlerts().get(2).getCondition(),expectedAlert3.getCondition());
        assertEquals(generator.getAlerts().get(2).getTimestamp(),expectedAlert3.getTimestamp());

        // Trigger if Diastolic Pressure is too low
        Patient r4 = new Patient(4);
        r4.addRecord(159,"SystolicPressure" , 7);
        r4.addRecord(55,"DiastolicPressure" , 8);

        generator.bloodPressureDataAlert(r4.getRecords());

        Alert expectedAlert4 = new Alert("4", "DiastolicPressure",8);

        assertEquals(generator.getAlerts().get(3).getPatientId(),expectedAlert4.getPatientId());
        assertEquals(generator.getAlerts().get(3).getCondition(),expectedAlert4.getCondition());
        assertEquals(generator.getAlerts().get(3).getTimestamp(),expectedAlert4.getTimestamp());



    }
    @Test
    public void testBloodPressureByTrend() {

        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        Patient r1 = new Patient(1);
        r1.addRecord(178,"SystolicPressure" , 0);
        r1.addRecord(157,"SystolicPressure" , 1+1000);
        r1.addRecord(158,"SystolicPressure" , 2+1000);
        r1.addRecord(127,"SystolicPressure" , 3+1000);
        r1.addRecord(117,"SystolicPressure" , 4+1000);

        generator.bloodPressureDataAlert(r1.getRecords());

        Alert expectedAlert1 = new Alert("1", "SystolicPressure",1004);

        assertEquals(generator.getAlerts().get(0).getPatientId(),expectedAlert1.getPatientId());
        assertEquals(generator.getAlerts().get(0).getCondition(),expectedAlert1.getCondition());
        assertEquals(generator.getAlerts().get(0).getTimestamp(),expectedAlert1.getTimestamp());

        Patient r2 = new Patient(2);
        r2.addRecord(119,"DiastolicPressure" , 0);
        r2.addRecord(95,"DiastolicPressure" , 1+1000);
        r2.addRecord(64,"DiastolicPressure" , 2+1000);
        r2.addRecord(94,"DiastolicPressure" , 3+1000);

        generator.bloodPressureDataAlert(r2.getRecords());

        Alert expectedAlert2 = new Alert("2", "DiastolicPressure",1003);

        assertEquals(generator.getAlerts().get(1).getPatientId(),expectedAlert2.getPatientId());
        assertEquals(generator.getAlerts().get(1).getCondition(),expectedAlert2.getCondition());
        assertEquals(generator.getAlerts().get(1).getTimestamp(),expectedAlert2.getTimestamp());
    }









}
