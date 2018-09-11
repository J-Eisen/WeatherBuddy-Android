package com.example.jonaheisenstock.weatherbuddy.DataClasses;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

/**
 * Created by jonaheisenstock on 2/11/18.
 */

public class BuddyInfo {
    public String[] itemNames = {"Umbrella", "Rain Boots", "Snow Boots", "Light Coat", "Heavy Coat", "Sunglasses"};
    Context context;
    public UserInfo userInfo = new UserInfo();

    public enum TemperatureScale {FAHRENHEIT, CELSIUS, KELVIN}
    public enum MeasurementSystem {IMPERIAL, METRIC}

    // Item List
    // Shorts:      0
    // Light Coat:  1
    // Heavy Coat:  2
    // Rain Boots:  3
    // Snow Boots:  4
    // Umbrella:    5
    // WTF:         6
    public boolean[] items = {false, false, false, false, false, false, false};
    public String[] names = {"Shorts", "Light Coat", "Heavy Coat", "Rain Boots", "Snow Boots", "Umbrella", "WTF"};
    // data List
    // highTemp: 0
    // lowTemp:  1
    // rain:     2
    // snow:     3
    // precip:   4
    @VisibleForTesting
    public double[] dataEnglish = new double[5];
    @VisibleForTesting
    public int[] dataMetric = new int[5];
    // Trigger List
    // Shorts (HighTemp):    0
    // Heavy Coat (LowTemp): 1
    // Rain Boots (Rain):    2
    // Snow Boots (Snow):    3
    // Umbrella (Percip):    4
    private double[] dataTriggers = {80, 40, 1, 1, 60};
    private TemperatureScale tempScale;
    private MeasurementSystem dataSystem;
    public int itemsLength = items.length;
    public int namesLength = names.length;

    public BuddyInfo() {
        for (int i = 0; i < 5; i++) {
            dataEnglish[i] = 0;
            dataMetric[i] = 0;
        }
    tempScale = TemperatureScale.FAHRENHEIT;
    dataSystem = MeasurementSystem.IMPERIAL;
    }

    public void updateBuddy() {
        int feelsLikeE, feelsLikeM, rainM, snowM, precip;
        double rainE, snowE;

        for (int i = 0; i < userInfo.weather.weatherList.size(); i++) {
            feelsLikeE = userInfo.weather.weatherList.get(i).getFeelsLikeE();
            feelsLikeM = userInfo.weather.weatherList.get(i).getFeelsLikeM();
            rainE = userInfo.weather.weatherList.get(i).getRainE();
            rainM = userInfo.weather.weatherList.get(i).getRainM();
            snowE = userInfo.weather.weatherList.get(i).getSnowE();
            snowM = userInfo.weather.weatherList.get(i).getSnowM();
            precip = userInfo.weather.weatherList.get(i).getPrecip();


            // Updating FeelsLike English //
            if (feelsLikeE > (int) dataEnglish[0])
                dataEnglish[0] = (double) feelsLikeE;
            else if (feelsLikeE < (int) dataEnglish[1])
                dataEnglish[1] = (double) feelsLikeE;

            // Updating FeelsLike Metric //
            if (feelsLikeM > dataMetric[0])
                dataMetric[0] = feelsLikeM;
            else if (feelsLikeM < dataMetric[1])
                dataMetric[1] = feelsLikeM;

            // Updating Rain English //
            if (rainE != dataEnglish[2])
                dataEnglish[2] = rainE;

            // Updating Rain Metric //
            if (rainM != dataMetric[2])
                dataMetric[2] = rainM;

            // Updating Snow English //
            if (snowE != dataEnglish[3])
                dataEnglish[3] = snowE;

            // Updating Snow Metric //
            if (snowM != dataMetric[3])
                dataMetric[3] = snowM;

            // Updating Precipitation //
            if (precip != dataMetric[4]) {
                dataMetric[4] = precip;
                dataEnglish[4] = (double) precip;
            }
        }
        checkAll(dataTriggers, dataEnglish);
    }

    // Checking Data //
    public void checkAll(double[] dataTriggers, double[] dataSet) {

        checkTemp(dataTriggers, dataSet);

        for (int i = 2; i < 5; i++) {
            if (dataTriggers[i] <= dataSet[i]) {
                items[i + 1] = true;
            }
            else
                items[i + 1] = false;
        }
    }

    @VisibleForTesting
    public void checkTemp(double[] dataTriggers, double[] dataSet) {

        if (dataTriggers[0] <= dataSet[1]) { // Shorts
            items[0] = true;
            items[1] = false;
            items[2] = false;
        } else if (dataTriggers[1] >= dataSet[0]) { // Heavy Coat
            items[0] = false;
            items[1] = false;
            items[2] = true;
        } else if (dataTriggers[0] > dataSet[0] // Light Coat
                && dataTriggers[1] < dataSet[1]) {
            items[0] = false;
            items[1] = true;
            items[2] = false;
        } else if (dataTriggers[0] < dataSet[0] // Light Coat and Shorts
                && dataTriggers[0] > dataSet[1]) {
            items[0] = true;
            items[1] = true;
            items[2] = false;
        } else if (dataTriggers[1] < dataSet[0] // Light AND Heavy Coat (Layer)
                && dataTriggers[1] > dataSet[1]) {
            items[0] = false;
            items[1] = true;
            items[2] = true;
        } else {
            items[0] = false;
            items[1] = false;
            items[2] = false;
            items[6] = true;
        }
    }

    // Gets //

    // Return the data from the buddy //
    public double[] getData() {
        double[] dataSet = new double[5];
        if (dataSystem == MeasurementSystem.IMPERIAL) {
            dataSet = dataEnglish;
            switch (tempScale) {
                case CELSIUS:
                    dataSet[0] = (double) dataMetric[0];
                    dataSet[1] = (double) dataMetric[1];
                    break;
                case KELVIN:
                    dataSet[0] = (double) (dataMetric[0] + 273.15);
                    dataSet[1] = (double) (dataMetric[1] + 273.15);
                    break;
                default:
            }
            return dataSet;
        } else {
            for (int i = 0; i < 5; i++) {
                dataSet[i] = (double) dataMetric[i];
            }
            switch (tempScale) {
                case FAHRENHEIT:
                    dataSet[0] = dataEnglish[0];
                    dataSet[1] = dataEnglish[1];
                    break;
                case KELVIN:
                    dataSet[0] += 273.15;
                    dataSet[1] += 273.15;
                    break;
                default:
            }
            return dataSet;
        }
    }
    public boolean getItem(int i){
        if (i >= items.length)
            return items[items.length-1];
        else
            return items[i];
    }
    public String getName(int i){
        if (i >= names.length)
            return names[names.length-1];
        else
            return names[i];
    }

    public TemperatureScale getTempScale(){
        return tempScale;
    }

    public MeasurementSystem getDataSystem() {
        return dataSystem;
    }

    public void updateInfo(){
        userInfo.updateData(context);
    }

    // Sets //

    public void setTempScale(int input){
        switch (input){
            case 0:
                tempScale = TemperatureScale.FAHRENHEIT;
                break;
            case 1:
                tempScale = TemperatureScale.CELSIUS;
                break;
            case 2:
                tempScale = TemperatureScale.KELVIN;
                break;
            default:
                System.out.println("Error setting Temperature Scale");
        }

    }

    public void setDataSystem(int input){
        if (input == 0)
            dataSystem = MeasurementSystem.IMPERIAL;
        else if (input == 1)
            dataSystem = MeasurementSystem.METRIC;
        else
            System.out.println("Error Setting Data System");
    }

    public void setDataTriggers(int i, double trigger){
        dataTriggers[i] = trigger;
    }

    public void setDataTriggers(double[] triggers){
        if (triggers.length == dataTriggers.length)
            dataTriggers = triggers;
        else
            System.out.println("Error - new trigger set length mismatch");
    }

    public void setDataEnglish(int i, double data){
        dataEnglish[i] = data;
    }

    public void setDataEnglish(double[] data){
        dataEnglish = data;
    }

    public void setDataMetric(int i, int data){
        dataMetric[i] = data;
    }

    public void setDataMetric(int[] data){
        dataMetric = data;
    }

    @VisibleForTesting
    public void setWeatherList(int hour, int location, double[] weatherE, int[] weatherM){
        userInfo.weather.weatherList.add(new WeatherInfo().new WeatherData(hour, location,
                    (int) weatherE[0], weatherM[0],
                        weatherE[1], weatherM[1],
                        weatherE[2], weatherM[2], weatherM[3]));
    }

    @VisibleForTesting
    public void clearWeatherList(){
        userInfo.weather.weatherList.clear();
    }
}

