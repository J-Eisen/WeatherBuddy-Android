package com.example.jonaheisenstock.weatherbuddy;

import com.example.jonaheisenstock.weatherbuddy.DataClasses.LocationInfo;
import com.example.jonaheisenstock.weatherbuddy.DataClasses.MergeSort;
import com.example.jonaheisenstock.weatherbuddy.DataClasses.WeatherInfo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class MergeSortUnitTest {

   @Test
    public void testMergeSortInstances(){
       ArrayList<LocationInfo.InstanceData> testArray = new ArrayList<>();
       MergeSort mergeSort = new MergeSort();

       final Integer[] DEFAULT_NUMBERS = {0,1,1,3,4,4,5,6,8,9};
       ArrayList<Integer> deck = new ArrayList<Integer>(Arrays.asList(DEFAULT_NUMBERS));
       for (int i = 0; i < 10; i++){
           testArray.add(new LocationInfo().new InstanceData(i, deck.remove((int)Math.random()*9),i,"i"));
       }
       mergeSort.mergeSortInstances(testArray, 0, testArray.size());

       for (int i = 0; i < 10; i++){
           Assert.assertEquals("Error: Array Mismatch at index: " + i,(int)DEFAULT_NUMBERS[i],testArray.get(i).getStartTime());
       }
    }

    @Test
    public void testMergeSortWeather(){
        ArrayList<WeatherInfo.WeatherData> testArray = new ArrayList<>();
        MergeSort mergeSort = new MergeSort();

        final Integer[] DEFAULT_NUMBERS = {0,1,1,3,4,4,5,6,8,9};
        ArrayList<Integer> deck = new ArrayList<Integer>(Arrays.asList(DEFAULT_NUMBERS));
        for (int i = 0; i < 10; i++){
            testArray.add(new WeatherInfo().new WeatherData(deck.remove((int)Math.random()*9),i,i,i,i,i,i,i,i));
        }
        mergeSort.mergeSortWeather(testArray, 0, testArray.size());

        for (int i = 0; i < 10; i++){
            Assert.assertEquals("Error: Array Mismatch at index: " + i,(int)DEFAULT_NUMBERS[i],testArray.get(i).getHour());
        }
    }
}