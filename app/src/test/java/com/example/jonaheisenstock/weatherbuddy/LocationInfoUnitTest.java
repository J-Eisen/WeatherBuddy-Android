package com.example.jonaheisenstock.weatherbuddy;

import android.content.Context;

import com.example.jonaheisenstock.weatherbuddy.DataClasses.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


//TODO: Switch to Mockito Tests
public class LocationInfoUnitTest {

    @Mock
    LocationInfo mLocationInfo;
    Context mContext;

    @Before
    public void setupTestCalendarParser() {
        doAnswer((a) -> {
            System.out.println("calendar parse called");
            return null;
                }).when(mLocationInfo).calendarParser(mContext);
    }

    @Test
    public void testCalendarParser() throws Exception {
        mLocationInfo.calendarParser(mContext);
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

            assertEquals(Math.round(TEST_START/60), testInstance.getStartTime());
            assertEquals(Math.round(TEST_END/60), testInstance.getEndTime());
            assertNotEquals(TEST_LOCATION, testInstance.getLocation());

            testInstance.manualSetLocation(TEST_LOCATION);
            assertEquals(TEST_LOCATION,testInstance.getLocation());
//        }
    }

    @Test
    public void testInstanceDataConvertFromString() throws Exception {

    }
}