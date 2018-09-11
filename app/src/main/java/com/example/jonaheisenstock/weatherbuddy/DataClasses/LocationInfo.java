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
import java.util.Locale;

@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
public class LocationInfo {
    // Lists of Calendar or Event Classes
    ArrayList<calendarData> calendarList = new ArrayList<>();
    ArrayList<eventData> eventList = new ArrayList<>();
    ArrayList<Integer> locationList = new ArrayList<>();
    int defaultLocation = 10017;

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
    private static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Events.CALENDAR_ID,                // 0
            CalendarContract.Events.DTSTART,                    // 1
            CalendarContract.Events.DTEND,                      // 2
            CalendarContract.Events.DURATION,                   // 3
            CalendarContract.Events.EVENT_LOCATION,             // 4
    };

    // The indices for the projection array above.
    private static final int EVENT_PROJECTION_CALENDAR_ID_INDEX = 0;
    private static final int EVENT_PROJECTION_DTSTART_INDEX = 1;
    private static final int EVENT_PROJECTION_DTEND_INDEX = 2;
    private static final int EVENT_PROJECTION_DURATION_INDEX = 3;
    private static final int EVENT_PROJECTION_EVENT_LOCATION_INDEX = 4;


    // Gets calendar and event info for eventList
    public void calendarParser(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Date now = new Date();
            Cursor calCursor = null;
            ContentResolver calCr = context.getContentResolver();
            Uri uri = CalendarContract.Calendars.CONTENT_URI;
            String calSelection = "((" + CalendarContract.Calendars.VISIBLE + " = ?))";
            String[] calSelectionArgs = new String[]{"1"};
            calCursor = calCr.query(uri, CALENDAR_PROJECTION, calSelection, calSelectionArgs, null);
            while (calCursor.moveToLast()) {
                long calID = 0;
                String calDisplayName = null;
                String calColor = null;

                calID = calCursor.getLong(CALENDAR_PROJECTION_ID_INDEX);
                calDisplayName = calCursor.getString(CALENDAR_PROJECTION_DISPLAY_NAME_INDEX);
                calColor = calCursor.getString(CALENDAR_PROJECTION_COLOR_INDEX);

                calendarList.add(new calendarData(calID, calDisplayName, calColor));

                Uri eventUri = CalendarContract.Events.CONTENT_URI;
                Cursor eventCursor = null;
                ContentResolver eventCr = context.getContentResolver();
                String eventSelection = "((" + CalendarContract.Events.DTSTART + " >= ?) AND ("
                        + CalendarContract.Events.DTSTART + " <= ?) AND ("
                        + CalendarContract.Events.SELF_ATTENDEE_STATUS + " != ?))"; //TODO: Add user ability to choose if their unreplied events show up too
                String[] eventSelectionArgs = new String[]{now.getTime()
                        + ", " + tomorrowDate(now)
                        + ", 2"};
                eventCursor = eventCr.query(eventUri, EVENT_PROJECTION, eventSelection, eventSelectionArgs, null);

                while (eventCursor.moveToLast()) {
                    long eventCalID = 0;
                    long eventStart = 0;
                    long eventEnd = 0;
                    String eventDuration = null;
                    String eventLocation = null;

                    eventCalID = eventCursor.getLong(EVENT_PROJECTION_CALENDAR_ID_INDEX);
                    eventStart = eventCursor.getLong(EVENT_PROJECTION_DTSTART_INDEX);
                    eventEnd = eventCursor.getLong(EVENT_PROJECTION_DTEND_INDEX);
                    eventDuration = eventCursor.getString(EVENT_PROJECTION_DURATION_INDEX);
                    eventLocation = eventCursor.getString(EVENT_PROJECTION_EVENT_LOCATION_INDEX);

                    eventList.add(new eventData(eventCalID, eventStart, eventEnd, eventDuration, eventLocation));
                }
            }
            Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());
            for (eventData event :
                    eventList) {
                if (geocoder.isPresent()) {
                    try {
                        event.convertFromString(geocoder);
                    } catch (IOException e) {

                    }
                }
            }
            updateLocationList();
        } else {
            locationList.add(defaultLocation);
        }
    }

    long tomorrowDate(Date tomorrow) {
        int dHours;
        if (tomorrow.getHours() > 6) {
            dHours = tomorrow.getHours() - 6;
            tomorrow.setTime(tomorrow.getTime() + 86400000 - (dHours * 3600000));
        } else
            tomorrow.setTime(tomorrow.getTime() + 86400000);
        return tomorrow.getTime();
    }

    private void updateLocationList() {
        boolean found;
        for (eventData e : eventList) {
            found = false;
            for (int i = 0; i < locationList.size(); i++) {
                if (locationList.get(i) == e.getLocation())
                    found = true;
            }
            if (!found) {
                locationList.add(e.getLocation());
            }
        }
    }

    // MergeSort Methods //
    protected static void eventBubbleSort(ArrayList<eventData> array) {
        for (int i = (array.size() - 1); i >= 0; i--) {
            for (int j = 1; j <= i; j++) {
                if (array.get(j - 1).getStartTime() > array.get(j).getStartTime()) {
                    eventData tempI = array.get(i);
                    eventData tempJ = array.get(j);
                    array.set(i, tempJ);
                    array.set(j, tempI);
                }
            }
        }
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

    protected class eventData {
        private long id;
        private int startTime, endTime, location;
        private String tempLocation;

        eventData(long id, long start, long end, String duration, String location) {
            this.id = id;                                   // Set Up id
            tempLocation = location;

            Date dateStart = new Date(start * 1000);       // Set Up startTime
            startTime = dateStart.getHours();

            if (duration == null) {                          // Set Up endTime
                Date dateEnd = new Date(end * 1000);
                endTime = dateEnd.getHours();
            } else {
                //TODO: Implement RFC2445 to Int
                endTime = 0;
            }
        }

        protected int getStartTime() {
            return startTime;
        }

        protected int getEndTime() {
            return endTime;
        }

        protected int getLocation() {
            return location;
        }

        void convertFromString(Geocoder geocoder) throws IOException {
            try {
                location = Integer.parseInt(geocoder.getFromLocationName(tempLocation, 1).get(0).getPostalCode());
            } catch (IllegalArgumentException e) {
                location = defaultLocation;
            }
        }
    }

}
