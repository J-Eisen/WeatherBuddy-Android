package com.example.jonaheisenstock.weatherbuddy.DataClasses;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

/**
 * Created by jonaheisenstock on 2/10/18.
 */

@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
class UserInfo {

//    private static final int PERMISSION_REQUEST_READ_CALENDAR = 0;
//
//    private static final String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_CALENDAR};
//TODO: Finish creation of subclasses: WeatherInfo & LocationInfo

    WeatherInfo weather = new WeatherInfo();
    LocationInfo location = new LocationInfo();

    Context context;

    public void updateData(Context context) {
        location.calendarParser(context.getApplicationContext());
        weather.weatherParser(location, context.getApplicationContext());
    }

    // Permissions Check // //TODO: Get permissions check to work
}