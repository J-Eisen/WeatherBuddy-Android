package com.example.jonaheisenstock.weatherbuddy.DataClasses;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
public class LocationInfo {

    // Permission Check Class setup
    PermissionCheck permissionCheck = new PermissionCheck();

    // Lists of Calendar or Event Classes
    private ArrayList<calendarData> calendarList = new ArrayList<>();
    private ArrayList<InstanceData> instanceList = new ArrayList<>();
    private HashSet<Integer> locationSet = new HashSet<>();
    private int defaultLocation = 10017;

    // Calendar Info List to Get
    private static final String[] CALENDAR_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                       // 0
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,     // 1
            CalendarContract.Calendars.CALENDAR_COLOR,            // 2
    };

    // The indices for the projection array above.
    private static final int CALENDAR_PROJECTION_ID_INDEX = 0;
    private static final int CALENDAR_PROJECTION_DISPLAY_NAME_INDEX = 1;
    private static final int CALENDAR_PROJECTION_COLOR_INDEX = 2;

    // Event Info List to Get
    private static final String[] INSTANCE_PROJECTION = new String[]{
            CalendarContract.Instances.CALENDAR_ID,             // 0
            CalendarContract.Instances.START_MINUTE,            // 1
            CalendarContract.Instances.END_MINUTE,              // 2
            CalendarContract.Instances.EVENT_LOCATION,          // 3
    };

    // The indices for the projection array above.
    private static final int INSTANCE_PROJECTION_CALENDAR_ID_INDEX = 0;
    private static final int INSTANCE_PROJECTION_START_MINUTE_INDEX = 1;
    private static final int INSTANCE_PROJECTION_END_MINUTE_INDEX = 2;
    private static final int INSTANCE_PROJECTION_EVENT_LOCATION_INDEX = 3;

    // Gets calendar and event info for instanceList
    public void calendarParser(Context context) {
        if (permissionCheck.check(context, Manifest.permission.READ_CALENDAR)) {
            Date now = new Date();
            Cursor calCursor = null;
            ContentResolver calCr = context.getContentResolver();
            Uri uri = CalendarContract.Calendars.CONTENT_URI;
            String calSelection = "((" + CalendarContract.Calendars.VISIBLE + " = ?))";
            String[] calSelectionArgs = new String[]{"1"};
            calCursor = calCr.query(uri, CALENDAR_PROJECTION, calSelection, calSelectionArgs, null);

            // Pulling calendars
            while (calCursor.moveToLast()) {
                long calID = 0;
                String calDisplayName = null;
                String calColor = null;

                calID = calCursor.getLong(CALENDAR_PROJECTION_ID_INDEX);
                calDisplayName = calCursor.getString(CALENDAR_PROJECTION_DISPLAY_NAME_INDEX);
                calColor = calCursor.getString(CALENDAR_PROJECTION_COLOR_INDEX);

                calendarList.add(new calendarData(calID, calDisplayName, calColor));

                Uri instanceUri = CalendarContract.Instances.CONTENT_URI;
                Cursor instanceCursor = null;
                ContentResolver instanceCr = context.getContentResolver();
                //TODO: Add user ability to choose if their unreplied events show up too
                String instanceSelection = "((" + CalendarContract.Instances.START_MINUTE  + " >= ?) AND ("
                        + CalendarContract.Instances.START_MINUTE + " <= ?) AND ("
                        + CalendarContract.Instances.CALENDAR_ID + " == ?))";
                String[] instanceSelectionArgs = new String[]{now.getTime()
                        + ", " + tomorrowDate(now)
                        + ", " + calID};
                instanceCursor = instanceCr.query(instanceUri, INSTANCE_PROJECTION, instanceSelection, instanceSelectionArgs, null);

                while (instanceCursor.moveToLast()) {
                    long instanceCalID = 0;
                    int instanceStart = 0;
                    int instanceEnd = 0;
                    String instanceLocation = null;


                    instanceCalID = instanceCursor.getLong(INSTANCE_PROJECTION_CALENDAR_ID_INDEX);
                    instanceStart = instanceCursor.getInt(INSTANCE_PROJECTION_START_MINUTE_INDEX);
                    instanceEnd = instanceCursor.getInt(INSTANCE_PROJECTION_END_MINUTE_INDEX);
                    instanceLocation = instanceCursor.getString(INSTANCE_PROJECTION_EVENT_LOCATION_INDEX);

                    instanceList.add(new InstanceData(instanceCalID, instanceStart, instanceEnd, instanceLocation));
                }
            }
            Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());
            for (InstanceData instance : instanceList) {
                if (geocoder.isPresent()) {
                    try {
                        instance.convertFromString(geocoder);
                        locationSet.add(instance.location);
                    }
                    catch (IOException e) {}
                }
            }
        }
        else {
            locationSet.add(defaultLocation);
        }
    }

    @VisibleForTesting
    public long tomorrowDate(Date tomorrow) {
        int dHours;
        if (tomorrow.getHours() > 6) {
            dHours = tomorrow.getHours() - 6;
            tomorrow.setTime(tomorrow.getTime() + 86400000 - (dHours * 3600000));
        }
        else
            tomorrow.setTime(tomorrow.getTime() + 86400000);
        return tomorrow.getTime();
    }

    @VisibleForTesting
    public HashSet<Integer> getLocationSet(){
        return locationSet;
    }

    // Data Classes //

    class calendarData {
        private long id;
        private String name, color;
        boolean include;

        calendarData(long id, String name, String color) {
            this.id = id;
            this.name = name;
            this.color = color;
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public class InstanceData {
        private long calId;
        private int startTime, endTime, location;
        private String tempLocation;


        @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
        public InstanceData(long calId, int startMinutes, int endMinutes, String location) {
            // Set Up id
            this.calId = calId;
            tempLocation = location;

            // Set Up startTime
            startTime = Math.round(startMinutes/60);

            // Set Up endTime
            endTime = Math.round(endMinutes/60);
        }

        @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
        public int getStartTime() {
            return startTime;
        }

        @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
        public int getEndTime() {
            return endTime;
        }

        @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
        public int getLocation() {
            return location;
        }

        void convertFromString(Geocoder geocoder) throws IOException {
            try {
                location = Integer.parseInt(geocoder.getFromLocationName(tempLocation, 1).get(0).getPostalCode());
            } catch (IllegalArgumentException e) {
                location = defaultLocation;
            }
        }

        @VisibleForTesting
        public void manualSetLocation(int location){
            this.location = location;
        }
    }

}
