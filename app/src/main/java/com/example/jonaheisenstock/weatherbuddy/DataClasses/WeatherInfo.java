package com.example.jonaheisenstock.weatherbuddy.DataClasses;

import android.Manifest;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
public class WeatherInfo extends MergeSort {

    //TODO: Convert from zipcode to coordinates
    //TODO: Write an autoreset 1x a day so that the high doesn't keep climbing and the low doesn't keep dropping
    //TODO: Add NON calendar functionality (GPS & manual entry)

    //For JSON Data
    @VisibleForTesting
    public LinkedList<String> jsonData = new LinkedList<>();
    final String URL_BASE = "http://api.wunderground.com/api/fb2d4e978c2c2a11/hourly/q/";
    RequestQueue jsonQueue;

    // Lists of Weather Classes
    @VisibleForTesting public ArrayList<WeatherData> rawWeatherList = new ArrayList<>();
    @VisibleForTesting public ArrayList<WeatherData> weatherList = new ArrayList<>();
    @VisibleForTesting public boolean flag = false;

    // Gets weather info from WUnderground for weatherList
    // Calls getJson (which calls parseJson)
    // Adds unsorted jsonData to rawData, which sorts it
    public void weatherParser(LocationInfo locationInfo, PermissionCheck permissionCheck, Context context) {
        if (permissionCheck.check(context, Manifest.permission.INTERNET)) {
            Iterator<Integer> locationIterator = locationInfo.getLocationSet().iterator();
            int tempLocation;
            String url;

            while (locationIterator.hasNext()) {
                tempLocation = locationIterator.next();
                url = URL_BASE + String.valueOf(tempLocation) + ".json";

                // get JSONObject from JSON file
                getJson(url);

                // Add all info to rawWeatherList
                while (!jsonData.isEmpty()) {
                    rawWeatherList.add(new WeatherData(jsonData, tempLocation));
                    flag = true;
                }
            }
        } else {
            //TODO: Add something where user's internet access is requested again
        }
    }

    // Get the JSON from the url and convert to a JSONArray
    @VisibleForTesting
    public void getJson(String url){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        parseJson(response.getJSONArray("hourly_forecast"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> error.printStackTrace());
        jsonQueue.add(request);
    }

    // Parse JSON and dump relevant data into a LinkedList
    @VisibleForTesting
    public void parseJson(JSONArray weatherArray) {
        // Parse Weather
        for (int i = 0; i < weatherArray.length(); i++) {
            try {
                JSONObject weatherDetail = weatherArray.getJSONObject(i);

                // Getting hours
                JSONObject weatherTime = weatherDetail.getJSONObject("FCTTIME");
                jsonData.add(weatherTime.getString("hour"));
                // Getting FeelsLike
                JSONObject feelsLike = weatherDetail.getJSONObject("feelslike");
                jsonData.add(feelsLike.getString("english"));
                jsonData.add(feelsLike.getString("metric"));
                // Getting rain
                JSONObject rain = weatherDetail.getJSONObject("qpf");
                jsonData.add(rain.getString("english"));
                jsonData.add(rain.getString("metric"));
                // Getting snow
                JSONObject snow = weatherDetail.getJSONObject("snow");
                jsonData.add(snow.getString("english"));
                jsonData.add(snow.getString("metric"));
                // Getting precipitation
                jsonData.add(weatherDetail.getString("pop"));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //
    @VisibleForTesting
    public void setWeatherList(ArrayList<LocationInfo.InstanceData> locationInstanceList) {
        int time1, time2, loc1, loc2;
        if (locationInstanceList.size() > 2) {
            mergeSortInstances(locationInstanceList, 0, locationInstanceList.size());

            if (rawWeatherList.size() > 1)
                mergeSortWeather(rawWeatherList, 0, rawWeatherList.size());

            for (int i = 0; i <= locationInstanceList.size() - 2; i++) {
                time1 = locationInstanceList.get(i).getEndTime();
                loc1 = locationInstanceList.get(i).getLocation();
                time2 = locationInstanceList.get(i + 1).getStartTime();
                loc2 = locationInstanceList.get(i + 1).getLocation();
                pickWeatherData(time1, time2, loc1, loc2);
            }
        }
        else if (locationInstanceList.size() > 1) {
            pickWeatherData(locationInstanceList.get(0).getStartTime(),locationInstanceList.get(0).getEndTime(),
                    locationInstanceList.get(0).getLocation(), locationInstanceList.get(0).getLocation());
        }
        else {
            weatherList.addAll(rawWeatherList);
            rawWeatherList.clear();
        }
        mergeSortWeather(weatherList, 0, weatherList.size());
    }

    // Gets weather data from rawWeatherList to add to weather list for setWeatherList
    @VisibleForTesting
    public void pickWeatherData(int t1, int t2, int l1, int l2) {
        for (int i = rawWeatherList.size() - 1; i > -1; i--) {
            if (rawWeatherList.get(i).getHour() >= t1 - 1 &&
                    rawWeatherList.get(i).getHour() <= t2 + 1) {
                if (rawWeatherList.get(i).getHour() <= t2
                        && rawWeatherList.get(i).getLocation() == l1) {
                    weatherList.add(rawWeatherList.get(i));
                    rawWeatherList.remove(i);
                }
                else if (t1 <= rawWeatherList.get(i).getHour() &&
                        rawWeatherList.get(i).getLocation() == l2) {
                    weatherList.add(rawWeatherList.get(i));
                    rawWeatherList.remove(i);
                }
            }
        }
    }

    // Data Classes //
    //TODO: Find a way to avoid making sets & gets public
    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    public class WeatherData {
        private int hour, feelsLikeM, rainM, snowM, precip, location;
        private double feelsLikeE, rainE, snowE;

        @VisibleForTesting
        public WeatherData(LinkedList<String> json, int location) {
            setHour(Integer.parseInt(json.remove()));
            setFeelsLikeE(Double.parseDouble(json.remove()));
            setFeelsLikeM(Integer.parseInt(json.remove()));
            setRainE(Double.parseDouble(json.remove()));
            setRainM(Integer.parseInt(json.remove()));
            setSnowE(Double.parseDouble(json.remove()));
            setSnowM(Integer.parseInt(json.remove()));
            setPrecip(Integer.parseInt(json.remove()));
            this.location = location;
        }
        @VisibleForTesting
        public WeatherData(int hour, int location,
                    double feelsLikeE, int feelsLikeM,
                    double rainE, int rainM,
                    double snowE, int snowM, int precip){
            this.hour = hour;
            this.location = location;
            this.feelsLikeE = feelsLikeE;
            this.feelsLikeM = feelsLikeM;
            this.rainE = rainE;
            this.rainM = rainM;
            this.snowE = snowE;
            this.snowM = snowM;
            this.precip = precip;

        }

        public void setHour(int hour) {
            this.hour = hour;
        }
        public void setFeelsLikeE(double feelsLikeE) {
            this.feelsLikeE = feelsLikeE;
        }
        public void setFeelsLikeM(int feelsLikeM) {
            this.feelsLikeM = feelsLikeM;
        }
        public void setRainE(double rainE) {
            this.rainE = rainE;
        }
        public void setRainM(int rainM) {
            this.rainM = rainM;
        }
        public void setSnowE(double snowE) {
            this.snowE = snowE;
        }
        public void setSnowM(int snowM) {
            this.snowM = snowM;
        }
        public void setPrecip(int precip) {
            this.precip = precip;
        }

        public int getHour() {
            return hour;
        }
        public int getLocation() {
            return location;
        }
        public double getFeelsLikeE() {
            return feelsLikeE;
        }
        public int getFeelsLikeM() {
            return feelsLikeM;
        }
        public double getRainE() {
            return rainE;
        }
        public int getRainM() {
            return rainM;
        }
        public double getSnowE() {
            return snowE;
        }
        public int getSnowM() {
            return snowM;
        }
        public int getPrecip() {
            return precip;
        }

    }

}
