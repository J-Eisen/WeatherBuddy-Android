package com.example.jonaheisenstock.weatherbuddy;

import com.example.jonaheisenstock.weatherbuddy.DataClasses.BuddyInfo;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class BuddyUnitTest {
    BuddyInfo buddy = new BuddyInfo();

    //TODO: No weatherE/weatherM high/low temps
    //TODO: Add error messages
    @Test
    public void testUpdateBuddy() throws Exception {
        final int DEFAULT_HOUR = 0;
        final int DEFAULT_LOCATION = 10007;
        final double[] DEFAULT_WEATHER_ENGLISH = {70, 0.5, 0.5, 20};
        final int[] DEFAULT_WEATHER_METRIC = {20, 1, 1, 20};
        double[] weatherE = DEFAULT_WEATHER_ENGLISH;
        int[] weatherM = DEFAULT_WEATHER_METRIC;
        ArrayList<double[]> expectedDataE = new ArrayList();
        ArrayList<int[]> expectedDataM = new ArrayList();
        for (int i = 0; i < 4; i++) {
            buddy.setWeatherList(DEFAULT_HOUR, DEFAULT_LOCATION, DEFAULT_WEATHER_ENGLISH, DEFAULT_WEATHER_METRIC);
            switch (i) {
                case 0: // Testing Altering 1 item at a time
                    for (int j = -1; j < 4; j++) {
                        if (j < 0) {
                            weatherE[0] -= 10;
                            weatherM[0] -= 10;
                        } else {
                            weatherE[j] += 5;
                            weatherM[j] += 5;
                        }
                        buddy.setWeatherList(j + 2, DEFAULT_LOCATION, weatherE, weatherM);
                        buddy.updateBuddy();
                        Assert.assertArrayEquals(weatherE,buddy.dataEnglish,0.01);
                        Assert.assertArrayEquals(weatherM,buddy.dataMetric);
                        weatherE = DEFAULT_WEATHER_ENGLISH;
                        weatherM = DEFAULT_WEATHER_METRIC;
                    }
                    break;
                case 1: // Testing Altering 2 items at a time
                case 2: // Testing Altering 3 items at a time
                    for (int j = 0; j < 4; j++){
                        for (int k = 0; k < 4; k++){
                            weatherE[k]++;
                            weatherM[k]++;
                        }
                        weatherE[j] = DEFAULT_WEATHER_ENGLISH[j];
                        weatherM[j] = DEFAULT_WEATHER_METRIC[j];
                        buddy.setWeatherList(1,10007, weatherE, weatherM);
                        assertArrayEquals(weatherE,buddy.dataEnglish,0.1);
                        assertArrayEquals(weatherM,buddy.dataMetric);
                    }
                    break;
                case 3: // Testing altering all items at once
                    for (int j = 0; j < 4; j++){
                        weatherE[j]++;
                        weatherM[j]++;
                    }
                    buddy.setWeatherList(1,10007, weatherE, weatherM);
                    assertArrayEquals(weatherE,buddy.dataEnglish,0.1);
                    assertArrayEquals(weatherM,buddy.dataMetric);
                    break;
                default:
            }
            buddy.clearWeatherList();
        }
    }

    @Test
    public void testCheckTemp() throws Exception {
        String errorMessage = "CheckTemp - Unknown Error";
        boolean defaultItems[] = {false, false, false, false};
        double[] testTriggers = {2 , 0};
        double[] testData = new double[2];
        boolean[] items = new boolean[4];
        int increment;
        int reps;

        for (int i = 0; i < 5; i++) {
            switch (i) {
                // Test Case TFF //
                case 0:
                    testData[0] = 4;
                    testData[1] = 3; // Move: Down
                    defaultItems[0] = true;
                    increment = -1;
                    errorMessage = "CheckTemp Error - TFF, Rep #";
                    break;
                // Test Case FFT //
                case 1:
                    testData[0] = -1; // Move: Down
                    testData[1] = -2;
                    defaultItems[2] = true;
                    increment = -1;
                    errorMessage = "CheckTemp Error - FFT, Rep #";
                    break;
                // Test Case FTF //
                case 2:
                    testData[0] = 1.5; // Move: Down
                    testData[1] = 1; // Move: Down
                    defaultItems[1] = true;
                    increment = 1;
                    errorMessage = "CheckTemp Error - FTF, Rep #";
                    break;
                // Test Case TTF //
                case 3:
                    testData[0] = 3; // Move: Down
                    testData[1] = 1; // Move: Up
                    defaultItems[0] = true;
                    defaultItems[1] = true;
                    increment = 2;
                    errorMessage = "CheckTemp Error - TTF, Rep #";
                    break;
                // Test Case FTT //
                case 4:
                    testData[0] = 1; // Move: Down
                    testData[1] = -1; // Move: Up
                    defaultItems[1] = true;
                    defaultItems[2] = true;
                    increment = 2;
                    errorMessage = "CheckTemp Error - FTT, Rep #";
                    break;
                // Test Case FFFT // //TODO: Fix the FFFT test (LOW PRIORITY)
                default:
                    /*testData[0] = -2;
                    testData[1] = 3;
                    defaultItems[3] = true;*/
                    increment = 0;
                    errorMessage = "CheckTemp Error - FFFT";
            }

            if (i < 2)
                reps = 2;
            else
                reps = 1;
            for (int j = 0; j < reps; j++) {
                buddy.checkTemp(testTriggers, testData);
                for (int k = 0; k < 3; k++) {
                    items[k] = buddy.items[k];
                }
                items[3] = buddy.items[6];

                Assert.assertArrayEquals(errorMessage.concat(Integer.toString(j+1)), defaultItems, items);
                for (int k = 0; k < testData.length; k++) {
                    if (i <= 3 && k == 0)
                        testData[k] -= increment;
                    else
                        testData[k] += increment;
                }

                if (j == reps-1) { // Flip t -> f on last test
                    for (Boolean defaultItem : defaultItems) {
                        if (defaultItem == true)
                            defaultItem = false;
                    }
                }
            }
        }
    }

    @Test
    public void testCheckAll() throws Exception {
        String errorMessage = "CheckAll - ";
        String errorCode = "Unknown Error";
        final boolean DEFAULT_ITEMS[] = {false, false, false};
        final double TEST_TRIGGERS[] = {70, 40, 1, 1, 60};
        final double DEFAULT_TEST_DATA[] = {90, 80, 0.5, 0.5, 30};
        double testData[] = DEFAULT_TEST_DATA;
        boolean testItems[] = new boolean[3];
        boolean actualItems[] = {false, false, false};
        for (int i = 0; i < 8; i++){
            switch (i) {
                case 0: //FFF
                    errorCode = "Error: Default Failed";
                    break;
                case 1: //TFF
                    if (i == 1)
                        errorCode = "Error: Single Tests - TFF";
                case 2: // FTF
                    if (i == 2)
                        errorCode = "Error: Single Tests - FTF";
                case 3: // FFT
                    if (i == 3)
                        errorCode = "Error: Single Tests - FFT";
                    testData[i+1] = testData[i+1]*2+1;
                    testItems[i-1] = true;
                    break;
                case 4: //TTF
                    if (i == 4)
                        errorCode = "Error: TTF";
                    testData[2] = testData[2]*2+1;
                    testData[3] = testData[3]*2+1;
                    testItems[0] = true;
                    testItems[1] = true;
                    break;
                case 5: //TTT
                    if (i == 5)
                        errorCode = "Error: TTT";
                    testData[2] = testData[2]*2+1;
                    testData[3] = testData[3]*2+1;
                    testData[4] = testData[4]*2+1;
                    testItems[0] = true;
                    testItems[1] = true;
                    testItems[2] = true;
                    break;
                case 6: // FTT
                    errorCode = "Error: FTT";
                    testData[3] = testData[3]*2+1;
                    testData[4] = testData[4]*2+1;
                    testItems[1] = true;
                    testItems[2] = true;
                    break;
                case 7: // TFT
                    errorCode = "Error: TFT";
                    testData = DEFAULT_TEST_DATA;
                    testData[2] = testData[2];
                    testData[4] = testData[4];
                    testItems[0] = true;
                    testItems[2] = true;
                    break;
                default:
                    errorCode = "Error: Unknown Error Code";
            }
            buddy.checkAll(TEST_TRIGGERS, testData);
            for (int j = 0; j < 3; j++) {
                actualItems[j] = buddy.items[j+3];
            }
            assertArrayEquals(setErrorMessage(errorCode,errorMessage), testItems, actualItems);
            // Reset
            testData = DEFAULT_TEST_DATA;
            buddy.checkAll(TEST_TRIGGERS, DEFAULT_TEST_DATA);
            testItems = DEFAULT_ITEMS;
        }
    }

    @Test
    public void testGetData() throws Exception {
        double[] dataEnglishDefault = {2, 1.5, 0.5, 0.1, 5};
        int[] dataMetricDefault = {20, 15, 5, 1, 50};
        double[] testData = new double[5];
        double[] returnData;
        String errorMessage = "Unknown Setup Error";
        
        buddy.setDataEnglish(dataEnglishDefault);
        buddy.setDataMetric(dataMetricDefault);

        for (int i = 0; i < 2; i++) {
            buddy.setDataSystem(i);
            if (i == 0) {
                testData = dataEnglishDefault;
                errorMessage = "Failure - Imperial | ";
            }
            else {
                for (int m = 0; m < dataMetricDefault.length; m++) {
                    testData[m] = (double) dataMetricDefault[m];
                }
                errorMessage = "Failure - Metric | ";
            }
            for (int j = 0; j < 3; j++) {
                buddy.setTempScale(j);
                if (i == 0) {
                    if (j == 0)
                        errorMessage.concat("Fahrenheit");
                    else if (j == 1) {
                        testData[0] = (double) dataMetricDefault[0];
                        testData[1] = (double) dataMetricDefault[1];
                        errorMessage.concat("Celsius");
                    }
                    else {
                        testData[0] += 273.15;
                        testData[1] += 273.15;
                        errorMessage.concat("Kelvin");
                    }
                } else {
                    if (j == 0) {
                        testData[0] = dataEnglishDefault[0];
                        testData[1] = dataEnglishDefault[1];
                        errorMessage.concat("Fahrenheit");
                    }
                    else if (j == 1) {
                        testData[0] = (double) dataMetricDefault[0];
                        testData[1] = (double) dataMetricDefault[1];
                        errorMessage.concat("Celsius");
                    }
                    else {
                        testData[0] += 273.15;
                        testData[1] += 273.15;
                        errorMessage.concat("Kelvin");
                    }
                }
                returnData = buddy.getData();
                assertArrayEquals(errorMessage, testData, returnData, 0.05);
            }
        }
    }

    @Test
    public void testGetItem() throws Exception {
        boolean[] itemDefaults = {false, false, false, false, false, false, false};
        for (int i = 0; i < buddy.itemsLength; i++){
            Assert.assertEquals("Failure - Item array Mismatch when using buddy.getItem()", itemDefaults[i], buddy.getItem(i));
        }
        Assert.assertEquals("Failure - getItem() overflow error i = array length", itemDefaults[itemDefaults.length-1], buddy.getItem(itemDefaults.length));
        Assert.assertEquals("Failure - getItem() overflow error i > array length", itemDefaults[itemDefaults.length-1], buddy.getItem(itemDefaults.length+10));
    }

    @Test
    public void testGetName() throws Exception {
        String[] nameDefaults = {"Shorts", "Light Coat", "Heavy Coat", "Rain Boots", "Snow Boots", "Umbrella", "WTF"};
        for (int i = 0; i < buddy.namesLength; i++){
            Assert.assertEquals("Failure - Name array Mismatch when using buddy.getName()", nameDefaults[i], buddy.getName(i));
        }
        Assert.assertEquals("Failure - getName() overflow error i = array length", nameDefaults[nameDefaults.length-1], buddy.getName(nameDefaults.length));
        Assert.assertEquals("Failure - getName() overflow error i > array length", nameDefaults[nameDefaults.length-1], buddy.getName(nameDefaults.length+10));
    }

    @Test
    public void testTempScale() throws Exception {
        BuddyInfo.TemperatureScale defaultTempScale[] = {BuddyInfo.TemperatureScale.FAHRENHEIT, BuddyInfo.TemperatureScale.CELSIUS, BuddyInfo.TemperatureScale.KELVIN};
        for (int i = 0; i < 3; i++) {
            buddy.setTempScale(i);
            Assert.assertEquals("Failure - setTemp() or getTemp()", defaultTempScale[i], buddy.getTempScale());
        }
    }

    @Test
    public void testSetDataSystem() throws Exception {
        BuddyInfo.MeasurementSystem defaultDataSystem[] = {BuddyInfo.MeasurementSystem.IMPERIAL, BuddyInfo.MeasurementSystem.METRIC};
        for (int i = 0; i < 2; i++) {
            buddy.setDataSystem(i);
            Assert.assertEquals("Failure - setDataSystem() or getDataSystem()", defaultDataSystem[i], buddy.getDataSystem());
        }
    }

    private String setErrorMessage(String message, String defaultMessage) {
        String newMessage = defaultMessage.concat(message);
        return newMessage;
    }
}


