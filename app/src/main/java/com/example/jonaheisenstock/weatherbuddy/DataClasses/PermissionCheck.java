package com.example.jonaheisenstock.weatherbuddy.DataClasses;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

// Class to check permissions for location, calendar, and internet
// TODO: Set up cases for permission changes and no permission granted

public class PermissionCheck {

    public boolean check(Context context, String permissionName){
        return (ContextCompat.checkSelfPermission(context,permissionName) == PackageManager.PERMISSION_GRANTED);
    }
}
