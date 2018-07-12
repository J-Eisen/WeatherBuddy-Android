package com.example.jonaheisenstock.weatherbuddy;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jonaheisenstock on 2/10/18.
 */

class UserInfo {

//    private static final int PERMISSION_REQUEST_READ_CALENDAR = 0;
//
//    private static final String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_CALENDAR};

    //Array Lists

    //For JSON Data
    ArrayList<Integer> hours = new ArrayList<>();
    ArrayList<Integer> feelsLikeEnglish = new ArrayList<>();
    ArrayList<Integer> feelsLikeMetric = new ArrayList<>();
    ArrayList<Float> rainEnglish = new ArrayList<>();
    ArrayList<Integer> rainMetric = new ArrayList<>();
    ArrayList<Float> snowEnglish = new ArrayList<>();
    ArrayList<Integer> snowMetric = new ArrayList<>();
    ArrayList<Integer> precipitation = new ArrayList<>();

    // Lists of classes //

    // Lists of Weather Classes
    ArrayList<weatherData> rawWeatherList = new ArrayList<>();
    ArrayList<weatherData> weatherList = new ArrayList<>();

    // Lists of Calendar or Event Classes
    ArrayList<calendarData> calendarList = new ArrayList<>();
    ArrayList<eventData> eventList = new ArrayList<>();
    ArrayList<Integer> locationList = new ArrayList<>();
    int defaultLocation = 10017;

    Context context;


    void updateData(Context context) {
        calendarParser(context.getApplicationContext());
        weatherParser(context.getApplicationContext());
    }


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
    private void calendarParser(Context context) {
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

    // Gets weather info from WUnderground for weatherList
    private void weatherParser(Context context) {
        for (int i = 0; i < locationList.size(); i++) {
            try {
                JSONObject obj = new JSONObject(loadJSONfromAsset(locationList.get(i), context.getApplicationContext())); // get JSONObject from JSON file

                JSONArray weatherArray = obj.getJSONArray("hourly_forecast"); // fetch JSONArray

                int hour;

                for (int j = 0; j < weatherArray.length(); j++) {
                    // create a JSONObject for fetching single user weatherList
                    JSONObject weatherDetail = weatherArray.getJSONObject(i);
                    // Getting hours
                    JSONObject weatherTime = weatherDetail.getJSONObject("FCTTIME");
                    hour = Integer.parseInt(weatherTime.getString("hour"));
//                if (hour == calendar.endTime) {
                    hours.add(hour);
                    // Getting FeelsLike
                    JSONObject feelsLike = weatherDetail.getJSONObject("feelslike");
                    feelsLikeEnglish.add(Integer.parseInt(feelsLike.getString("english")));
                    feelsLikeMetric.add(Integer.parseInt(feelsLike.getString("metric")));
                    // Getting rain
                    JSONObject rain = weatherDetail.getJSONObject("qpf");
                    rainEnglish.add(Float.parseFloat(rain.getString("english")));
                    rainMetric.add(Integer.parseInt(rain.getString("metric")));
                    // Getting snow
                    JSONObject snow = weatherDetail.getJSONObject("snow");
                    snowEnglish.add(Float.parseFloat(snow.getString("english")));
                    snowMetric.add(Integer.parseInt(snow.getString("metric")));
                    // Getting precipitation
                    precipitation.add(Integer.parseInt(weatherDetail.getString("pop")));
//                }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int j = 0; j < hours.size(); j++) {
                rawWeatherList.add(new weatherData(hours, locationList.get(i),
                        feelsLikeEnglish, feelsLikeMetric,
                        rainEnglish, rainMetric,
                        snowEnglish, snowMetric,
                        precipitation));
            }
        }
    }

    private void setWeatherList() {
        int time1, time2, loc1, loc2;
        if (eventList.size() > 1) {
            eventBubbleSort(eventList);
        }
        if (rawWeatherList.size() > 1) {
            weatherBubbleSort(rawWeatherList);
        }

        for (int i = 0; i < eventList.size() - 1; i++) {
            time1 = eventList.get(i).getEndTime();
            loc1 = eventList.get(i).getLocation();
            time2 = eventList.get(i + 1).getStartTime();
            loc2 = eventList.get(i + 1).getLocation();
            pickWeatherData(time1, time2, loc1, loc2);
        }
    }

    // Gets weather data from rawWeatherList to add to weather list for setWeatherList
    private void pickWeatherData(int t1, int t2, int l1, int l2) {
        for (weatherData weather :
                rawWeatherList)
            if (weather.getHour() > t2 + 1) {
                break;
            } else {
                if (t1 - 1 >= weather.getHour() && weather.getHour() <= t2
                        && weather.getLocation() == l1) {
                    weatherList.add(weather);
                    rawWeatherList.remove(weather);
                } else if (t1 <= weather.getHour() && weather.getHour() <= t2 + 1
                        && weather.getLocation() == l2) {
                    weatherList.add(weather);
                    rawWeatherList.remove(weather);
                }
            }
    }

    private String loadJSONfromAsset(int location, Context context) {
        String url = "http://api.wunderground.com/api/fb2d4e978c2c2a11/hourly/q/" + location + ".json";
        String json = null;
        try {
            InputStream in = context.getAssets().open(url);
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    // Sorting Methods //
    private static void eventBubbleSort(ArrayList<eventData> array) {
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

    private static void weatherBubbleSort(ArrayList<weatherData> array) {
        for (int i = (array.size() - 1); i >= 0; i--) {
            for (int j = 1; j <= i; j++) {
                if (array.get(j - 1).getHour() > array.get(j).getHour()) {
                    weatherData tempI = array.get(i);
                    weatherData tempJ = array.get(j);
                    array.set(i, tempJ);
                    array.set(j, tempI);
                }
            }
        }
    }

    // Permissions Check // //TODO: Get permissions check to work

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

    class eventData {
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

        private int getEndTime() {
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

    class weatherData {
        private int hour, feelsLikeE, feelsLikeM, rainM, snowM, precip, location;
        private float rainE, snowE;

        weatherData(ArrayList h, int location,
                    ArrayList fE, ArrayList fM,
                    ArrayList rE, ArrayList rM,
                    ArrayList sE, ArrayList sM,
                    ArrayList p) {
            setHour(Integer.parseInt((String) h.remove(0)));
            setFeelsLikeE(Integer.parseInt((String) fE.remove(0)));
            setFeelsLikeM(Integer.parseInt((String) fM.remove(0)));
            setRainE(Float.parseFloat((String) rE.remove(0)));
            setRainM(Integer.parseInt((String) rM.remove(0)));
            setSnowE(Float.parseFloat((String) sE.remove(0)));
            setSnowM(Integer.parseInt((String) sM.remove(0)));
            setPrecip(Integer.parseInt((String) p.remove(0)));
            this.location = location;
        }

        void setHour(int hour) {
            this.hour = hour;
        }
        void setFeelsLikeE(int feelsLikeE) {
            this.feelsLikeE = feelsLikeE;
        }
        void setFeelsLikeM(int feelsLikeM) {
            this.feelsLikeM = feelsLikeM;
        }
        void setRainE(float rainE) {
            this.rainE = rainE;
        }
        void setRainM(int rainM) {
            this.rainM = rainM;
        }
        void setSnowE(float snowE) {
            this.snowE = snowE;
        }
        void setSnowM(int snowM) {
            this.snowM = snowM;
        }
        void setPrecip(int precip) {
            this.precip = precip;
        }

        private int getHour() {
            return hour;
        }

        int getLocation() {
            return location;
        }
        int getFeelsLikeE() {
            return feelsLikeE;
        }
        int getFeelsLikeM() {
            return feelsLikeM;
        }
        float getRainE() {
            return rainE;
        }
        int getRainM() {
            return rainM;
        }
        float getSnowE() {
            return snowE;
        }
        int getSnowM() {
            return snowM;
        }
        int getPrecip() {
            return precip;
        }

    }
}