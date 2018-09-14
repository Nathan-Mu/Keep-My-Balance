package edu.monash.fit4039.keepmybalance;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import static edu.monash.fit4039.keepmybalance.KMBConstant.ADD_NEW_RECORD_RESPONSE;
import static edu.monash.fit4039.keepmybalance.KMBConstant.CLICK_ADD_NEW_RECORD_REQUEST;
import static edu.monash.fit4039.keepmybalance.KMBConstant.CLICK_CHILD_ITEM_REQUEST;
import static edu.monash.fit4039.keepmybalance.KMBConstant.REMOVE_CHILD_RESPONSE;
import static edu.monash.fit4039.keepmybalance.Time.getCurrentDate;
import static edu.monash.fit4039.keepmybalance.Time.getCurrentTextDate;
import static edu.monash.fit4039.keepmybalance.Time.toText;

/**
 * Created by nathan on 7/5/17.
 */

public class HomeFragment extends Fragment {
    private View vHome;
    private TextView hTvMonthIncome, hTvMonthExpense, hTvMonthReminding, hTvTodayIncome, hTvTodayExpense, hTvLast7daysIncome, hTvLast7daysExpense,
        hTvMonthYear, hTvTodayDate, hTvCurrentWeather, hTvCurrentTemp;
    private UserInfo user;
    private Button hBtnNewRecord;
    private LocationManager locationManager;
    private String locationProvider;
    private boolean canAccessLocation;
    private Location location = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vHome = inflater.inflate(R.layout.fragment_home, container, false);

        this.getActivity().setTitle("Home");

        Intent intent = this.getActivity().getIntent();
        user = intent.getParcelableExtra("user");

        hTvMonthIncome = (TextView) vHome.findViewById(R.id.hTvMonthIncome);
        hTvMonthExpense = (TextView) vHome.findViewById(R.id.hTvMonthExpense);
        hTvMonthReminding = (TextView) vHome.findViewById(R.id.hTvMonthReminding);
        hTvTodayIncome = (TextView) vHome.findViewById(R.id.hTvToadyIncome);
        hTvTodayExpense = (TextView) vHome.findViewById(R.id.hTvTodayExpense);
        hTvLast7daysIncome = (TextView) vHome.findViewById(R.id.hTvLast7daysIncome);
        hTvLast7daysExpense = (TextView) vHome.findViewById(R.id.hTvLast7daysExpense);
        hTvMonthYear = (TextView) vHome.findViewById(R.id.hTvMonthYear);
        hTvTodayDate = (TextView) vHome.findViewById(R.id.hTvTodayDate);
        hTvCurrentTemp = (TextView) vHome.findViewById(R.id.hTvCurrentWeather);
        hTvCurrentWeather = (TextView) vHome.findViewById(R.id.hTvCurrentTemp);
        hBtnNewRecord = (Button) vHome.findViewById(R.id.hBtnNewRecord);

        initUI();

        //if can access the location service, refresh the weather if user location moves
        if (canAccessLocation)
            refreshWeather();

        //add new record
        hBtnNewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (location == null) {
                //if user location is null, he cannot add a record
                showText("Please enable location services");
            } else {
                Intent newIntent = new Intent(vHome.getContext(), AddNewRecordActivity.class);
                newIntent.putExtra("user", user);
                newIntent.putExtra("location", location);
                startActivityForResult(newIntent, CLICK_ADD_NEW_RECORD_REQUEST);
            }
            }
        });

        return vHome;
    }

    //resource: https://developer.android.com/reference/android/location/LocationListener.html
    LocationListener locationListener =  new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        //if location is changed, it will init/refresh the weather
        @Override
        public void onLocationChanged(Location location) {
            HomeFragment.this.location = location;
            initWeather(location);
        }
    };

    //init UI
    private void initUI() {
        hTvMonthYear.setText(toText(getCurrentDate(), "MMM, yyyy"));
        hTvTodayDate.setText("Today " + getCurrentTextDate());

        showWeather();

        HomePageStatics homePageStatics = new HomePageStatics();
        homePageStatics.execute(new Integer[] {user.getUserId()});
    }

    //show the weather and temp based on current location
    public void showWeather() {
        Activity activity = this.getActivity();
        canAccessLocation = new LocationPermissionManager(activity).canAccessLocation();
        if(!canAccessLocation) {
            //if cannot access the location,ask for the permission
            new LocationPermissionManager(activity).requestPermissions();
        }
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        //get location providers
        if(providers.contains(LocationManager.GPS_PROVIDER)){
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)){
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            //ask for user to open GPS or WiFi
            showText("No location Provider. Please open WiFi or GPS.");
            return ;
        }
        try {
            locationManager.requestLocationUpdates(locationProvider, 1, 1, locationListener);
            location = locationManager.getLastKnownLocation(locationProvider);
            if (location == null) {
                try {
                    //if user firstly open the GPS, let system refresh and record the location
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                location = locationManager.getLastKnownLocation(locationProvider);
                locationManager.requestLocationUpdates(locationProvider, 1, 1, locationListener);
            }
            if (location != null)
                initWeather(location);
            else
                showText("Please open GPS or Wifi and refresh page");
        }
        catch (SecurityException secEx) {
            showText("Please enable location services");
        }
    }

    //get weather info from network(open weather), on init
    public void initWeather(Location currentLocation) {
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.execute(currentLocation);
    }

    //refresh weather info
    public void refreshWeather() {
        try {
            locationManager.requestLocationUpdates(locationProvider, 3000, 10, locationListener);
        }
        catch (SecurityException secEx) {
            showText("Please enable location services");
        }
    }

    //get home statics (incomes, expenses) from server which has already generated in server side
    private class HomePageStatics extends AsyncTask<Integer, Void, double[]>
    {
        @Override
        protected double[] doInBackground(Integer... params) {
            return RestClient.getHomePageStatics(params[0]);
        }

        @Override
        protected void onPostExecute(double[] statics) {
            //set the statics
            DecimalFormat df =new DecimalFormat("#0.00");
            hTvMonthIncome.setText(df.format(statics[0]));
            hTvMonthExpense.setText(df.format(statics[1]));
            hTvMonthReminding.setText(df.format(statics[0] - statics[1]));
            hTvTodayIncome.setText(df.format(statics[2]));
            hTvTodayExpense.setText(df.format(statics[3]));
            hTvLast7daysIncome.setText(df.format(statics[4]));
            hTvLast7daysExpense.setText(df.format(statics[5]));
        }
    }

    //get weather info from open weather
    private class CurrentWeather extends AsyncTask<Location, Void, JSONObject>
    {
        @Override
        protected JSONObject doInBackground(Location... params) {
            return SearchService.getCurrentWeather(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject weatherJson) {
            try {
                String description = weatherJson.getJSONArray("weather").getJSONObject(0).getString("main");
                String tempK = weatherJson.getJSONObject("main").getString("temp");
                DecimalFormat df = new DecimalFormat("#.0");
                String tempT = df.format(Double.valueOf(tempK) - 273.15);
                hTvCurrentWeather.setText("Current Weather: " + description);
                hTvCurrentTemp.setText("Current Temperature: " + tempT + "℃");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //get child activity response code
    //if the request and response matches, it means user just add a new record
    //then show the success message and init UI
    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        if (requestCode == CLICK_ADD_NEW_RECORD_REQUEST && responseCode == ADD_NEW_RECORD_RESPONSE) {
            showText("Add successfully");
            initUI();
        }
    }

    //show a notification on screen
    private void showText(String string) {
        Toast.makeText(vHome.getContext(), string, Toast.LENGTH_SHORT).show();
    }


}
