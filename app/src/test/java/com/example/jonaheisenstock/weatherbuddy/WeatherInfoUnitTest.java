package com.example.jonaheisenstock.weatherbuddy;

import android.content.Context;
import android.location.Location;

import com.example.jonaheisenstock.weatherbuddy.DataClasses.*;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;


public class WeatherInfoUnitTest {

    WeatherInfo weatherInfo = new WeatherInfo();

    @Test
    public void testWeatherParser() throws Exception {}

    //TODO: Figure out if a testSetWeatherList is needed at all
//    @Test
//    public void testSetWeatherList() throws Exception {
//        final int[] START_TIMES = {};
//        final int[] END_TIMES = {};
//        final int[] LOCATIONS = {};
//        final int[] HOURS = {};
//
//        ArrayList<WeatherInfo.WeatherData> expectedRawWeatherList = new ArrayList<>();
//        ArrayList<WeatherInfo.WeatherData> expectedWeatherList = new ArrayList<>();
//        ArrayList<LocationInfo.InstanceData> expectedInstanceList = new ArrayList<>();
//
//
//        for (int i = 0; i < LOCATIONS.length; i++) {
//            expectedInstanceList.add(new LocationInfo().new InstanceData(0,0,0,"10007"));
//            expectedInstanceList.get(i).manualSetLocation(LOCATIONS[i]);
//            weatherInfo.weatherList.clear();
//            weatherInfo.rawWeatherList.clear();
//            weatherInfo.rawWeatherList.addAll(expectedRawWeatherList);
//        }
//    }

    @Test
    public void testPickWeatherData() throws Exception {
        ArrayList<WeatherInfo.WeatherData> expectedRawWeatherList = new ArrayList<>();
        ArrayList<WeatherInfo.WeatherData> expectedWeatherList = new ArrayList<>();
        final int[] HOURS = {2,4,1,4};
        final int[] LOCATIONS = {1,1,0,0};

        // Set Up weather lists //
        expectedWeatherList.clear();
        weatherInfo.weatherList.clear();
        weatherInfo.rawWeatherList.clear();

        for(int i = 0; i < 4; i++){
            expectedRawWeatherList.add(new WeatherInfo().new WeatherData(HOURS[i],LOCATIONS[i],
                    i,i,i,i,i,i,i));
        }

        weatherInfo.rawWeatherList.addAll(expectedRawWeatherList);

        // Testing
        weatherInfo.pickWeatherData(1,2,0,1);

        expectedWeatherList.add(expectedRawWeatherList.get(2));
        expectedWeatherList.add(expectedRawWeatherList.get(0));
        expectedRawWeatherList.remove(2);
        expectedRawWeatherList.remove(0);

        Assert.assertEquals("Removal Error", expectedRawWeatherList, weatherInfo.rawWeatherList);
        Assert.assertEquals("Addition Error", expectedWeatherList, weatherInfo.weatherList);
    }

    @Test
    public void testLoadJSONfromAsset() throws Exception {}

    @Test
    public void testWeatherDataSetsAndGets() throws Exception {
        final int DEFAULT_HOUR = 0;
        final int DEFAULT_LOCATION = 10007;
        final double[] DEFAULT_WEATHER_ENGLISH = {70, 0.5, 0.5, 40};
        final int[] DEFAULT_WEATHER_METRIC = {20, 1, 1, 40};
        WeatherInfo.WeatherData weatherData = new WeatherInfo().new WeatherData(DEFAULT_HOUR, DEFAULT_LOCATION,
                (int)DEFAULT_WEATHER_ENGLISH[0], DEFAULT_WEATHER_METRIC[0],
                DEFAULT_WEATHER_ENGLISH[1], DEFAULT_WEATHER_METRIC[1],
                DEFAULT_WEATHER_ENGLISH[2], DEFAULT_WEATHER_METRIC[2], DEFAULT_WEATHER_METRIC[3]);
        //Testing Gets
        Assert.assertEquals(DEFAULT_HOUR,weatherData.getHour());
        Assert.assertEquals(DEFAULT_LOCATION,weatherData.getLocation());
        Assert.assertEquals((int)DEFAULT_WEATHER_ENGLISH[0],weatherData.getFeelsLikeE(),0.1);
        Assert.assertEquals(DEFAULT_WEATHER_ENGLISH[1],weatherData.getRainE(),0.1);
        Assert.assertEquals(DEFAULT_WEATHER_ENGLISH[2],weatherData.getSnowE(),0.1);
        Assert.assertEquals(DEFAULT_WEATHER_METRIC[0],weatherData.getFeelsLikeM());
        Assert.assertEquals(DEFAULT_WEATHER_METRIC[1],weatherData.getRainM());
        Assert.assertEquals(DEFAULT_WEATHER_METRIC[2],weatherData.getSnowM());
        Assert.assertEquals(DEFAULT_WEATHER_METRIC[3],weatherData.getPrecip());

        //Testing Sets
        weatherData.setHour(DEFAULT_HOUR+1);
        weatherData.setFeelsLikeE((int)DEFAULT_WEATHER_ENGLISH[0]+1);
        weatherData.setRainE(DEFAULT_WEATHER_ENGLISH[1]+1);
        weatherData.setSnowE(DEFAULT_WEATHER_ENGLISH[2]+1);
        weatherData.setFeelsLikeM(DEFAULT_WEATHER_METRIC[0]+1);
        weatherData.setRainM(DEFAULT_WEATHER_METRIC[1]+1);
        weatherData.setSnowM(DEFAULT_WEATHER_METRIC[2]+1);
        weatherData.setPrecip(DEFAULT_WEATHER_METRIC[3]+1);
        Assert.assertEquals(DEFAULT_HOUR+1,weatherData.getHour());
        Assert.assertEquals((int)DEFAULT_WEATHER_ENGLISH[0]+1,weatherData.getFeelsLikeE(),0.1);
        Assert.assertEquals(DEFAULT_WEATHER_ENGLISH[1]+1,weatherData.getRainE(),0.1);
        Assert.assertEquals(DEFAULT_WEATHER_ENGLISH[2]+1,weatherData.getSnowE(),0.1);
        Assert.assertEquals(DEFAULT_WEATHER_METRIC[0]+1,weatherData.getFeelsLikeM());
        Assert.assertEquals(DEFAULT_WEATHER_METRIC[1]+1,weatherData.getRainM());
        Assert.assertEquals(DEFAULT_WEATHER_METRIC[2]+1,weatherData.getSnowM());
        Assert.assertEquals(DEFAULT_WEATHER_METRIC[3]+1,weatherData.getPrecip());

    }
}
