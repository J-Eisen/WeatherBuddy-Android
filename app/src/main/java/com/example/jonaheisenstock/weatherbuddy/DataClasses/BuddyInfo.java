package com.example.jonaheisenstock.weatherbuddy;

import android.content.Context;

/**
 * Created by jonaheisenstock on 2/11/18.
 */

public class BuddyInfo extends UserInfo {
    String[] itemNames = {"Umbrella", "Rain Boots", "Snow Boots", "Light Coat", "Heavy Coat", "Sunglasses"};
    Context context;
    UserInfo userInfo = new UserInfo();

    private enum TemperatureScale {FAHRENHEIT, CELSIUS, KELVIN};
    private enum MeasurementSystem {IMPERIAL, METRIC};


    // Item List
    // Shorts:      0
    // Light Coat:  1
    // Heavy Coat:  2
    // Rain Boots:  3
    // Snow Boots:  4
    // Umbrella:    5
    // WTF:         6
    public boolean[] items = {false, false, false, false, false, false, false};
    private String[] names = {"Shorts", "Light Coat", "Heavy Coat", "Rain Boots", "Snow Boots", "Umbrella", "WTF"};
    // data List
    // highTemp: 0
    // lowTemp:  1
    // rain:     2
    // snow:     3
    // precip:   4
    private float[] dataEnglish = new float[5];
    private int[] dataMetric = new int[5];
    // Trigger List
    // Shorts (HighTemp):    0
    // Heavy Coat (LowTemp): 1
    // Rain Boots (Rain):    2
    // Snow Boots (Snow):    3
    // Umbrella (Percip):    4
    private float[] dataTriggers = {80, 40, 1, 1, 60};
    private TemperatureScale tempScale;
    private MeasurementSystem dataSystem ;

    BuddyInfo() {
        for (int i = 0; i < 5; i++) {
            dataEnglish[i] = 0;
            dataMetric[i] = 0;
        }
    tempScale = TemperatureScale.FAHRENHEIT;
    dataSystem = MeasurementSystem.IMPERIAL;
    }

    public void updateBuddy() {
        int feelsLikeE, feelsLikeM, rainM, snowM, precip;
        float rainE, snowE;

        for (int i = 0; i < userInfo.weatherList.size(); i++) {
            feelsLikeE = userInfo.weatherList.get(i).getFeelsLikeE();
            feelsLikeM = userInfo.weatherList.get(i).getFeelsLikeM();
            rainE = userInfo.weatherList.get(i).getRainE();
            rainM = userInfo.weatherList.get(i).getRainM();
            snowE = userInfo.weatherList.get(i).getSnowE();
            snowM = userInfo.weatherList.get(i).getSnowM();
            precip = userInfo.weatherList.get(i).getPrecip();


            //MARK: Updating FeelsLike English
            if (feelsLikeE > (int) dataEnglish[0])
                dataEnglish[0] = (float) feelsLikeE;
            else if (feelsLikeE < (int) dataEnglish[1])
                dataEnglish[1] = (float) feelsLikeE;

            //MARK: Updating FeelsLike Metric
            if (feelsLikeM > dataMetric[0])
                dataMetric[0] = feelsLikeM;
            else if (feelsLikeM < dataMetric[1])
                dataMetric[1] = feelsLikeM;

            //MARK: Updating Rain English
            if (rainE > dataEnglish[2])
                dataEnglish[2] = rainE;

            //MARK: Updating Rain Metric
            if (rainM > dataMetric[2])
                dataMetric[2] = rainM;

            //MARK: Updating Snow English
            if (snowE > dataEnglish[3])
                dataEnglish[3] = snowE;

            //MARK: Updating Snow Metric
            if (snowM > dataMetric[3])
                dataMetric[3] = snowM;

            //MARK: Updating Precipitation
            if (precip > dataMetric[4]) {
                dataMetric[4] = precip;
                dataEnglish[4] = (float) precip;
            }
        }
        checkAll();
    }

    //MARK: Checking Data
    public void checkAll() {
        float[] dataTriggerSet = dataTriggers;
        float[] dataSet = dataEnglish;

        checkTemp();

        for (int i = 2; i < 5; i++) {
            if (dataTriggerSet[i] <= dataSet[i]) {
                items[i + 1] = true;
            }
        }
    }

    private void checkTemp() {
        float[] dataTriggerSet = dataTriggers;
        float[] dataSet = dataEnglish;

        if (dataTriggerSet[0] <= dataSet[1]) {
            items[0] = true;
            items[1] = false;
            items[2] = false;
        } else if (dataTriggerSet[1] >= dataSet[0]) {
            items[0] = false;
            items[1] = false;
            items[2] = true;
        } else if (dataTriggerSet[0] > dataSet[0] &&
                dataTriggerSet[1] < dataSet[1]) {
            items[0] = false;
            items[1] = true;
            items[2] = false;
        } else if (dataTriggerSet[0] < dataSet[0]
                && dataTriggerSet[1] < dataSet[1]) {
            items[0] = true;
            items[1] = true;
            items[2] = false;
        } else if (dataTriggerSet[0] > dataSet[0]
                && dataTriggerSet[1] > dataSet[1]) {
            items[0] = false;
            items[1] = true;
            items[2] = true;
        } else {
            items[0] = false;
            items[1] = false;
            items[1] = false;
            items[6] = true;
        }
    }

    //MARK: Return the data from the buddy
    public float[] getData() {
        float[] dataSet = new float[5];
        if (dataSystem == MeasurementSystem.IMPERIAL) {
            dataSet = dataEnglish;
            switch (tempScale) {
                case CELSIUS:
                    dataSet[0] = (float) dataMetric[0];
                    dataSet[1] = (float) dataMetric[1];
                    break;
                case KELVIN:
                    dataSet[0] = (float) (dataMetric[0] + 273.15);
                    dataSet[1] = (float) (dataMetric[1] + 273.15);
                    break;
                default:
            }
            return dataSet;
        } else {
            for (int i = 0; i < 5; i++) {
                dataSet[i] = (float) dataMetric[i];
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
        return items[i];
    }
    public String getName(int i){
        return names[i];
    }

    public void updateInfo(){
        userInfo.updateData(context);
    }

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
}

