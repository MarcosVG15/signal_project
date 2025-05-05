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
        System.out.println(generator.getAlerts());
    }
    @Test
    public void testAbnormalBehaviour() {

        Patient r1 = new Patient(1);
        r1.addRecord(1,"EmergencyButton" , System.currentTimeMillis());
        r1.addRecord(0,"EmergencyButton" , System.currentTimeMillis());
        r1.addRecord(-1,"EmergencyButton" , System.currentTimeMillis());
        r1.addRecord(1,"EmergencyButton" , System.currentTimeMillis());



        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        generator.ButtonEmergency(r1.getRecords());
    }

    @Test
    public void testECGAlert() {

        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        //Shows that program only triggers if the value PEAKS over a certain threshold

        Patient r1 = new Patient(1);
        r1.addRecord(2,"ECG" , System.currentTimeMillis());
        r1.addRecord(2,"ECG" , System.currentTimeMillis());
        r1.addRecord(2,"ECG" , System.currentTimeMillis());
        r1.addRecord(2,"ECG" , System.currentTimeMillis());
        r1.addRecord(2,"ECG" , System.currentTimeMillis());
        r1.addRecord(-1,"ECG" , System.currentTimeMillis());


        generator.ECGAlert(r1.getRecords());


        //I have set a threshold of 1 which means that it will only trigger if the value increases by more than 1
        Patient r2 = new Patient(1);
        r2.addRecord(2,"ECG" , System.currentTimeMillis());
        r2.addRecord(2,"ECG" , System.currentTimeMillis());
        r2.addRecord(2,"ECG" , System.currentTimeMillis());
        r2.addRecord(2,"ECG" , System.currentTimeMillis());
        r2.addRecord(2,"ECG" , System.currentTimeMillis());
        r2.addRecord(3.1,"ECG" , System.currentTimeMillis());


        generator.ECGAlert(r2.getRecords());




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
    }

    @Test
    public void testHypotensiveHypoxemiaAlert() {

        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        // Case when there isn't enough systolic blood pressure but enough oxygen
        Patient r1 = new Patient(1);
        r1.addRecord(23,"SystolicPressure" , System.currentTimeMillis());
        r1.addRecord(95,"Saturation" , System.currentTimeMillis());
        generator.hypotensiveHypoxemiaAlert(r1.getRecords());


        // Case when there is enough blood pressure but not enough oxygen saturation
        Patient r2 = new Patient(2);
        r2.addRecord(90,"SystolicPressure" , System.currentTimeMillis());
        r2.addRecord(78,"Saturation" , System.currentTimeMillis());
        generator.hypotensiveHypoxemiaAlert(r2.getRecords());


        // Case when there isn't enough of both
        Patient r3 = new Patient(3);
        r3.addRecord(89,"SystolicPressure" , System.currentTimeMillis());
        r3.addRecord(90,"Saturation" , System.currentTimeMillis());
        generator.hypotensiveHypoxemiaAlert(r3.getRecords());

        // Case when there is enough of both
        Patient r4 = new Patient(4);
        r4.addRecord(91,"SystolicPressure" , System.currentTimeMillis());
        r4.addRecord(93,"Saturation" , System.currentTimeMillis());
        generator.hypotensiveHypoxemiaAlert(r4.getRecords());



    }


    /**
     * Test if the blood saturation is below 92
     */
    @Test
    public void testBloodSaturationLevel() {

        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        Patient r1 = new Patient(1);
        r1.addRecord(91,"Saturation" , System.currentTimeMillis());

        generator.bloodSaturationAlerts(r1.getRecords());
    }


    @Test
    public void testBloodSaturationRapidDrop() {

        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        Patient r1 = new Patient(1);
        r1.addRecord(100,"Saturation" , System.currentTimeMillis());
        r1.addRecord(99,"Saturation" , System.currentTimeMillis());
        r1.addRecord(92,"Saturation" , System.currentTimeMillis()+1000*60*5);// rapid drop in a 5 minute interval

        generator.bloodSaturationAlerts(r1.getRecords());
    }

    @Test
    public void testBloodSaturationNoTrigger() {

        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        Patient r1 = new Patient(1);
        r1.addRecord(100,"Saturation" , System.currentTimeMillis());
        r1.addRecord(100,"Saturation" , System.currentTimeMillis());
        r1.addRecord(99,"Saturation" , System.currentTimeMillis());
        r1.addRecord(92,"Saturation" , System.currentTimeMillis()+1000*60*10);// rapid drop in a 10 minute interval shouldn't trigger alert
        r1.addRecord(95,"Saturation" , System.currentTimeMillis()+1000*60*20);

        generator.bloodSaturationAlerts(r1.getRecords());
    }

    @Test
    public void bloodPressureDataAlertTrigger() {

        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        // Trigger if Systolic Pressure is too high
        Patient r1 = new Patient(1);
        r1.addRecord(182,"SystolicPressure" , System.currentTimeMillis());
        r1.addRecord(119,"DiastolicPressure" , System.currentTimeMillis());
        generator.bloodPressureDataAlert(r1.getRecords());


        // Trigger if Diastolic Pressure is too high
        Patient r2 = new Patient(2);
        r2.addRecord(178,"SystolicPressure" , System.currentTimeMillis());
        r2.addRecord(121,"DiastolicPressure" , System.currentTimeMillis());

        generator.bloodPressureDataAlert(r2.getRecords());

        // Trigger if Systolic Pressure is too low
        Patient r3 = new Patient(3);
        r3.addRecord(87,"SystolicPressure" , System.currentTimeMillis());
        r3.addRecord(120,"DiastolicPressure" , System.currentTimeMillis());

        generator.bloodPressureDataAlert(r3.getRecords());

        // Trigger if Diastolic Pressure is too low
        Patient r4 = new Patient(4);
        r4.addRecord(159,"SystolicPressure" , System.currentTimeMillis());
        r4.addRecord(55,"DiastolicPressure" , System.currentTimeMillis());

        generator.bloodPressureDataAlert(r4.getRecords());

    }
    @Test
    public void testBloodPressureByTrend() {

        DataStorage storage  = new DataStorage() ;
        AlertGenerator generator = new AlertGenerator(storage) ;

        Patient r1 = new Patient(1);
        r1.addRecord(178,"SystolicPressure" , System.currentTimeMillis());
        r1.addRecord(157,"SystolicPressure" , System.currentTimeMillis()+1000);
        r1.addRecord(158,"SystolicPressure" , System.currentTimeMillis()+1000);
        r1.addRecord(127,"SystolicPressure" , System.currentTimeMillis()+1000);
        r1.addRecord(117,"SystolicPressure" , System.currentTimeMillis()+1000);

        generator.bloodPressureDataAlert(r1.getRecords());

        Patient r2 = new Patient(2);
        r2.addRecord(119,"DiastolicPressure" , System.currentTimeMillis());
        r2.addRecord(95,"DiastolicPressure" , System.currentTimeMillis()+1000);
        r2.addRecord(64,"DiastolicPressure" , System.currentTimeMillis()+1000);
        r2.addRecord(94,"DiastolicPressure" , System.currentTimeMillis()+1000);

        generator.bloodPressureDataAlert(r2.getRecords());
    }









}
