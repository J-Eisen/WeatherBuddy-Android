package com.example.jonaheisenstock.weatherbuddy.DataClasses;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;

@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
public class WeatherInfo extends MergeSort {

    //TODO: Convert from zipcode to coordinates
    //TODO: Write an autoreset 1x a day so that the high doesn't keep climbing and the low doesn't keep dropping

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

    // Lists of Weather Classes
    ArrayList<WeatherData> rawWeatherList = new ArrayList<>();
    ArrayList<WeatherData> weatherList = new ArrayList<>();

    // Gets weather info from WUnderground for weatherList
    public void weatherParser(LocationInfo locationInfo, Context context) {
        for (int i = 0; i < locationInfo.locationList.size(); i++) {
            try {
                // get JSONObject from JSON file
                JSONObject obj = new JSONObject(loadJSONfromAsset(locationInfo.locationList.get(i), context.getApplicationContext()));

                // fetch JSONArray
                JSONArray weatherArray = obj.getJSONArray("hourly_forecast");

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
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            for (int j = 0; j < hours.size(); j++) {
                rawWeatherList.add(new WeatherData(hours, locationInfo.locationList.get(i),
                        feelsLikeEnglish, feelsLikeMetric,
                        rainEnglish, rainMetric,
                        snowEnglish, snowMetric,
                        precipitation));
            }
        }
    }

    private void setWeatherList(LocationInfo locationInfo) {
        int time1, time2, loc1, loc2;
        if (locationInfo.eventList.size() > 1)
            mergeSortEvents(locationInfo.eventList,0,locationInfo.eventList.size());

        if (rawWeatherList.size() > 1)
            mergeSortWeather(rawWeatherList, 0, rawWeatherList.size());

        for (int i = 0; i < locationInfo.eventList.size() - 2; i++) {
            time1 = locationInfo.eventList.get(i).getEndTime();
            loc1 = locationInfo.eventList.get(i).getLocation();
            time2 = locationInfo.eventList.get(i + 1).getStartTime();
            loc2 = locationInfo.eventList.get(i + 1).getLocation();
            pickWeatherData(time1, time2, loc1, loc2);
        }
    }

    // Gets weather data from rawWeatherList to add to weather list for setWeatherList
    private void pickWeatherData(int t1, int t2, int l1, int l2) {
        for (WeatherData weather : rawWeatherList)
            if (weather.getHour() > t2 + 1) {
                break;
            }
            else {
                if (t1 - 1 >= weather.getHour() && weather.getHour() <= t2
                        && weather.getLocation() == l1) {
                    weatherList.add(weather);
                    rawWeatherList.remove(weather);
                }
                else if (t1 <= weather.getHour() && weather.getHour() <= t2 + 1
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

    // Data Classes //
    //TODO: Find a way to avoid making sets & gets public
    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    public class WeatherData {
        private int hour, feelsLikeE, feelsLikeM, rainM, snowM, precip, location;
        private double rainE, snowE;

        WeatherData(ArrayList h, int location,
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
        @VisibleForTesting
        public WeatherData(int hour, int location,
                    int feelsLikeE, int feelsLikeM,
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
        //TODO: FeelsLikeE defaults to int. Why?
        public void setFeelsLikeE(int feelsLikeE) {
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
        public int getFeelsLikeE() {
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
