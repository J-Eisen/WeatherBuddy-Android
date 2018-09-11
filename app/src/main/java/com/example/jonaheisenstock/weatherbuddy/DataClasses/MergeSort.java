package com.example.jonaheisenstock.weatherbuddy.DataClasses;

import java.util.ArrayList;

public class MergeSort {

    private void mergeWeather(ArrayList<WeatherInfo.WeatherData> array, int l, int m, int r) {
        int i, j, k;
        int n1 = m - l + 1;
        int n2 =  r - m;

        // Temp Arrays
        ArrayList<WeatherInfo.WeatherData> L = new ArrayList<>();
        ArrayList<WeatherInfo.WeatherData> R = new ArrayList<>();

        // Copy Data
        L.addAll(array.subList(l,n1));
        R.addAll(array.subList(m+1,r));
        L.trimToSize(); R.trimToSize();

        // Merge arrays back together
        i = 0; j = 0; k = l; // Initial indexes

        while (i < n1 && j < n2) {
            if (L.get(i).getHour() <= R.get(j).getHour()) {
                array.set(k,L.get(i));
                i++;
            }
            else {
                array.set(k,R.get(i));
                j++;
            }
            k++;
        }

        while (i < n1) {
            array.set(k,L.get(i));
            i++; k++;
        }

        while (j < n2) {
            array.set(k,R.get(i));
            j++; k++;
        }
    }

    private void mergeEvents(ArrayList<LocationInfo.eventData> array, int l, int m, int r) {
        int i, j, k;
        int n1 = m - l + 1;
        int n2 =  r - m;

        // Temp Arrays
        ArrayList<LocationInfo.eventData> L = new ArrayList<>();
        ArrayList<LocationInfo.eventData> R = new ArrayList<>();

        // Copy Data
        L.addAll(array.subList(l,n1));
        R.addAll(array.subList(m+1,r));
        L.trimToSize(); R.trimToSize();

        // Merge arrays back together
        i = 0; j = 0; k = l; // Initial indexes

        while (i < n1 && j < n2) {
            if (L.get(i).getStartTime() <= R.get(j).getStartTime()) {
                array.add(k,L.get(i));
                i++;
            }
            else {
                array.add(k,R.get(i));
                j++;
            }
            k++;
        }

        while (i < n1) {
            array.add(k,L.get(i));
            i++; k++;
        }

        while (j < n2) {
            array.add(k,R.get(i));
            j++; k++;
        }
    }

    public void mergeSortWeather(ArrayList<WeatherInfo.WeatherData> array, int l, int r) {
        if (l < r) {
            int m = l + (r - l)/2;

            mergeSortWeather(array, l, m); mergeSortWeather(array, m+1, r);
            mergeWeather(array, l, m, r);
        }
    }

    public void mergeSortEvents(ArrayList<LocationInfo.eventData> array, int l, int r) {
        if (l < r) {
            int m = l + (r - l)/2;

            mergeSortEvents(array, l, m); mergeSortEvents(array, m+1, r);
            mergeEvents(array, l, m, r);
        }
    }
}
