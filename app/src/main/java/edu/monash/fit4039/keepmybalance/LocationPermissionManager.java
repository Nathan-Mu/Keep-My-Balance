package edu.monash.fit4039.keepmybalance;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * Created by nathan on 2/6/17.
 */

public class LocationPermissionManager {
    private Activity activity;
    private boolean canAccessLocation;
    private static final int LOCATION_REQUEST_CODE = 1337;

    public LocationPermissionManager(Activity activity) {
        this.activity = activity;
    }

    public boolean canAccessLocation() {
        canAccessLocation = (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        return canAccessLocation;
    }

    public void requestPermissions() {
        // Check permission for fine location
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // http://stackoverflow.com/questions/41310510/what-is-the-difference-between-shouldshowrequestpermissionrationale-and-requestp
            new AlertDialog.Builder(activity)
                    .setTitle("Permission required")
                    .setMessage("This application need you to enable location services for it to work!")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);}
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            activity.finish();
                        }
                    })
                    .show();
        }
        else {
            // request permission
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }
}
