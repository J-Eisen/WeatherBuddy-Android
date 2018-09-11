package com.example.jonaheisenstock.weatherbuddy;

import android.content.Context;

import com.example.jonaheisenstock.weatherbuddy.DataClasses.*;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;


public class WeatherInfoUnitTest {

    WeatherInfo weatherInfo = new WeatherInfo();

    @Test
    public void testWeatherParser(){

    }

    @Test
    public void testSetWeatherList(){

    }

    @Test
    public void testPickWeatherData(){
        final int[] DEFAULT_HOURS = {0,1,2,3};
        final int[] DEFAULT_LOCATIONS = {10007, 90210};
        final int[][] DEFAULT_FEELSLIKE_ENGLISH = {{70,71,72,73},{80,81,82,83}};
        final int[][] DEFAULT_FEELSLIKE_METRIC = {{10,11,12,13},{20,21,22,23}};
        final double[][] DEFAULT_RAIN_ENGLISH = {{1.0,1.1,1.2,1.3},{2.0,2.1,2.2,2.3}};
        final int[][] DEFAULT_RAIN_METRIC = {{10,11,12,13},{20,21,22,23}};
        final double[][] DEFAULT_SNOW_ENGLISH = {{1.0,1.1,1.2,1.3},{2.0,2.1,2.2,2.3}};
        final int[][] DEFAULT_SNOW_METRIC = {{10,11,12,13},{20,21,22,23}};
        final int[][] DEFAULT_PRECIPITATION = {{10,11,12,13},{20,21,22,23}};

        ArrayList<WeatherInfo.WeatherData> rawWeatherList = new ArrayList<>();

        int endTime;    //t1
        int startTime;  //t2
        int location1 = DEFAULT_LOCATIONS[0];  //l1
        int location2 = DEFAULT_LOCATIONS[1];  //l2

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++){
                rawWeatherList.add(new WeatherInfo().new WeatherData(DEFAULT_HOURS[j],DEFAULT_LOCATIONS[i],
                        DEFAULT_FEELSLIKE_ENGLISH[i][j],DEFAULT_FEELSLIKE_METRIC[i][j],
                        DEFAULT_RAIN_ENGLISH[i][j],DEFAULT_RAIN_METRIC[i][j],
                        DEFAULT_SNOW_ENGLISH[i][j],DEFAULT_SNOW_METRIC[i][j],DEFAULT_PRECIPITATION[i][j]));
            }
        }

        for (int i = 0; i < 10; i++){
            switch(i) {
                case 0: //Should return nil
                    startTime = -2;
                    break;
                case 1: //Test for true, then break part 1, then 2, then 3?
                    //CASE TRUE
                    endTime = 3;
                    startTime = 3;
//t1 - 1 >= weather.getHour() && weather.getHour() <= t2
// && weather.getLocation() == l1
                case 2:
//t1 <= weather.getHour() && weather.getHour() <= t2 + 1
// && weather.getLocation() == l2
            }
        }
    }

    @Test
    public void testLoadJSONfromAsset(){

    }

    @Test
    public void testWeatherDataSetsAndGets(){
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

    private ArrayList setupWeatherArrayList(){
        ArrayList<WeatherInfo.WeatherData> list = new ArrayList<>();

        return list;
    }
}
