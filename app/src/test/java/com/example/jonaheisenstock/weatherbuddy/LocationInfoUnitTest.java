package com.example.jonaheisenstock.weatherbuddy;


import com.example.jonaheisenstock.weatherbuddy.DataClasses.*;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

public class LocationInfoUnitTest {

    LocationInfo locationInfo = new LocationInfo();

    @Test
    public void testCalendarParser() throws Exception {
    }

    @Test
    public void testTomorrowDate() throws Exception {
    }

    @Test
    public void testInstanceDataSetsAndGets() throws Exception {
        final long TEST_ID = 0;
        final int TEST_START = 60;
        final int TEST_END = 110;
        final int TEST_LOCATION = 1;
        final String TEST_LOCATION_STRING = "10007";

            LocationInfo.InstanceData testInstance = new LocationInfo().new InstanceData(TEST_ID, TEST_START, TEST_END, TEST_LOCATION_STRING);

            Assert.assertEquals(Math.round(TEST_START/60), testInstance.getStartTime());
            Assert.assertEquals(Math.round(TEST_END/60), testInstance.getEndTime());
            Assert.assertNotEquals(TEST_LOCATION, testInstance.getLocation());

            testInstance.manualSetLocation(TEST_LOCATION);
            Assert.assertEquals(TEST_LOCATION,testInstance.getLocation());
//        }
    }

    @Test
    public void testInstanceDataConvertFromString() throws Exception {

    }
}