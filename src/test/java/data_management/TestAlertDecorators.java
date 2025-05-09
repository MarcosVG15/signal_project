package data_management;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.design_pattern.Decorators.AlertDecoratorDec;
import com.design_pattern.Decorators.PriorityAlertDecoratorDec;
import com.design_pattern.Decorators.RepeatedAlertDecoratorDec;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import java.io.IOException;

public class TestAlertDecorators {

    @Test
    void testAlerts_NoRecords() throws IOException {
        Alert alert0 = new Alert("1", "TestCondition", 0);
        AlertDecoratorDec alertD = new AlertDecoratorDec(alert0);
        PriorityAlertDecoratorDec alert = new PriorityAlertDecoratorDec(alertD);
        assertEquals("CRITICAL TestCondition" ,  alert.getCriticalCondition(2));
        assertEquals("IMPORTANT TestCondition" ,  alert.getCriticalCondition(1));

    }
    @Test
    void testAlerts_RepeatedAlertDecorator() throws IOException {
        Alert alert0 = new Alert("1", "TestCondition", 0);
        AlertDecoratorDec alertD = new AlertDecoratorDec(alert0);
        DataStorage dataStorage = DataStorage.getInstance() ;
        dataStorage.addPatientData(1,32 , "ECG" ,0);
        dataStorage.addPatientData(1,32 , "ECG" ,1);
        dataStorage.addPatientData(1,32 , "ECG" ,2);
        dataStorage.addPatientData(1,32 , "ECG" ,3);
        dataStorage.addPatientData(1,12 , "ECG" ,4);
        dataStorage.addPatientData(1,12 , "ECG" ,5);


        AlertGenerator alertGenerator = new   AlertGenerator(dataStorage);

        Patient patient = new Patient(1);

        long[] interval = new long[2];
        interval[0] = 1 ;
        interval[1] = 5 ;

        alertGenerator.evaluateData(dataStorage.getAllPatients().get(0));
        RepeatedAlertDecoratorDec alert = new RepeatedAlertDecoratorDec(alertD ,alertGenerator  , interval);
        alert.evaluate(alertD) ;


    }


}
