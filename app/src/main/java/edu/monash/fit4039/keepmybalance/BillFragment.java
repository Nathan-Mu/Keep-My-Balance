package edu.monash.fit4039.keepmybalance;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.monash.fit4039.keepmybalance.JSONReader.getDoubleFromJSONObject;
import static edu.monash.fit4039.keepmybalance.JSONReader.getStringFromJSONObject;
import static edu.monash.fit4039.keepmybalance.KMBConstant.CLICK_CHILD_ITEM_REQUEST;
import static edu.monash.fit4039.keepmybalance.KMBConstant.CUSTOM_DARK_RED;
import static edu.monash.fit4039.keepmybalance.KMBConstant.CUSTOM_GREEN;
import static edu.monash.fit4039.keepmybalance.KMBConstant.EXPENSE;
import static edu.monash.fit4039.keepmybalance.KMBConstant.INCOME;
import static edu.monash.fit4039.keepmybalance.KMBConstant.REMOVE_CHILD_RESPONSE;
import static edu.monash.fit4039.keepmybalance.Time.*;

/**
 * Created by nathan on 17/5/17.
 */

public class BillFragment extends Fragment{
    View vBill;
    private UserInfo user;
    private Button bBtnSetMonth, bBtnShowOnMap, bBtnReport;
    private ExpandableListView fLvMonthlyRecords = null;
    private TextView bTvIncome, bTvExpense, bTvReminding;
    private List<String> dayList = new ArrayList<>();
    private Map<String, List<FundChange>> map = new HashMap<>();
    private int pickYear = 0;
    private int pickMonth = 0;
    private boolean canAccessLocation;
    private LocationManager locationManager;
    private String locationProvider;
    private Location location = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vBill = inflater.inflate(R.layout.fragment_bill, container, false);

        this.getActivity().setTitle("Bill");

        //get user from intent
        Intent intent = this.getActivity().getIntent();
        user = intent.getParcelableExtra("user");

        bBtnSetMonth = (Button) vBill.findViewById(R.id.bBtnSetMonth);
        bBtnShowOnMap = (Button) vBill.findViewById(R.id.bBtnShowOnMap);
        bBtnReport = (Button) vBill.findViewById(R.id.bBtnReport);
        bTvExpense = (TextView) vBill.findViewById(R.id.bTvExpense);
        bTvIncome = (TextView) vBill.findViewById(R.id.bTvIncome);
        bTvReminding = (TextView) vBill.findViewById(R.id.bTvReminding);
        fLvMonthlyRecords = (ExpandableListView) vBill.findViewById(R.id.fLvMonthlyRecords);

        initUI();

        //show a date picker dialog which only contains month and year
        bBtnSetMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickMonth > 0) {
                    new MonthYearDatePickerDialog(vBill.getContext(), 0, monthYearDatePickerDialog, pickYear, pickMonth - 1, getCalendarDay()).show();
                }
                else {
                    new MonthYearDatePickerDialog(vBill.getContext(), 0, monthYearDatePickerDialog, getCalendarYear(), getCalendarMonth(), getCalendarDay()).show();
                }
            }
        });

        bBtnShowOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshCurrentLocation();
                if (!map.isEmpty() && location != null) {
                    //if there is statics this month, and the current location is not null, show on map
                    ArrayList<FundChange> list = new ArrayList<>();
                    for (String day: dayList) {
                        for (FundChange f: map.get(day)) {
                            list.add(f);
                        }
                    }
                    Intent newIntent = new Intent(vBill.getContext(), MapActivity.class);
                    newIntent.putExtra("fundChanges", list);
                    newIntent.putExtra("location", location);
                    startActivity(newIntent);
                }
                else if (map.isEmpty()) {
                    //if there is no record this month
                    showText("No record to show on map.");
                }
            }
        });

        bBtnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map.isEmpty()) {
                    //if there is no record this month
                    showText("No record to show on report");
                }
                else if (pickMonth > 0) {
                    //if the month is picked (not default)
                    showReport(pickMonth, pickYear, user.getUserId());
                } else {
                    //if user has not picked the month, show the report of current month
                    showReport(getCalendarMonth() + 1, getCalendarYear(), user.getUserId());
                }
            }
        });

        //if user click the child item of list view, it view show the details of this record
        fLvMonthlyRecords.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String selectedDay = dayList.get(groupPosition);
                FundChange selectedFundChange = map.get(selectedDay).get(childPosition);
                Intent newIntent = new Intent(vBill.getContext(), ViewActivity.class);
                newIntent.putExtra("selectedFundChange", selectedFundChange);
                startActivityForResult(newIntent, CLICK_CHILD_ITEM_REQUEST);
                return true;
            }
        });

        return vBill;
    }

    //init UI
    private void initUI() {
        //set date (month/year)
        bBtnSetMonth.setText(String.format("%02d", (getCalendarMonth() + 1)) + "/" + getCalendarYear());
        //get and load bills (expendable list view)
        BillFragmentData billFragmentData = new BillFragmentData();
        billFragmentData.execute(getCalendarMonth() + 1, getCalendarYear(), user.getUserId());
    }

    //show a notification on screen
    private void showText(String string) {
        Toast.makeText(vBill.getContext(), string, Toast.LENGTH_SHORT).show();
    }

    //show a date picker which only contains month and date
    //resources: https://github.com/bendemboski/DateSlider
    //resources: https://developer.android.com/reference/android/widget/DatePicker.html
    private MonthYearDatePickerDialog.OnDateSetListener monthYearDatePickerDialog = new MonthYearDatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker startDatePicker, int year, int monthOfYear, int dayOfMonth) {
            pickMonth = monthOfYear + 1;
            pickYear = year;
            String textDate = pickMonth + "/" + pickYear;
            if (Time.toDate(textDate, "MM/yyyy").after(Time.getCurrentDate())) {
                //the picked month cannot be in the future
                showText("You cannot pick a month in future.");
            }
            else {
                bBtnSetMonth.setText(String.format("%02d", (monthOfYear + 1)) + "/" + year);
                BillFragmentData billFragmentData = new BillFragmentData();
                billFragmentData.execute(pickMonth, pickYear, user.getUserId());
            }
        }
    };

    //get and load the bills (fundchanges)
    //resource: http://moodle.vle.monash.edu/pluginfile.php/5474351/mod_resource/content/1/FIT5046-AndroidTute7-AsyncTask-2017.pdf
    private class BillFragmentData extends AsyncTask<Integer, Void, List<JSONObject>>
    {
        @Override
        protected List<JSONObject> doInBackground(Integer... params) {
            return RestClient.getMonthlyFundChange(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(List<JSONObject> jsonObjects) {
            dayList = new ArrayList<>();
            map = new HashMap<>();
            double income = 0;
            double expense = 0;
            //calculate the total income and expense
            //put the data in day list (a list of days that has records) and map
            for (JSONObject json: jsonObjects) {
                FundChange fundChange = new FundChange(json);
                if (fundChange.getChangeType().equalsIgnoreCase(EXPENSE))
                    expense += fundChange.getAmount();
                else if (fundChange.getChangeType().equalsIgnoreCase(INCOME))
                    income += fundChange.getAmount();
                String textDate = toText(fundChange.getChangeDate(), "dd/MMM/yyyy");
                if (!dayList.contains(textDate)) {
                    dayList.add(textDate);
                    map.put(textDate, new ArrayList<FundChange>());
                }
                map.get(textDate).add(fundChange);
            }
            DecimalFormat df = new DecimalFormat("0.00");
            bTvIncome.setText(df.format(income));
            bTvExpense.setText(df.format(expense));
            bTvReminding.setText(df.format(income - expense));
            //set the expandable listview adapter
            BillAdapter adapter = new BillAdapter(dayList, map, vBill.getContext());
            fLvMonthlyRecords.setAdapter(adapter);
        }
    }

    //get and check the child activity's response
    //if that means the requirement, that means it has just deleted a record
    //show a success message and init ui
    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        if (requestCode == CLICK_CHILD_ITEM_REQUEST && responseCode == REMOVE_CHILD_RESPONSE) {
            showText("Delete Successfully");
            initUI();
        }
    }

    //show report (pie chart)
    //firstly get the statics (which is analyzed in server side) in report
    //resource: https://developer.android.com/reference/android/os/AsyncTask.html
    private void showReport(int month, int year, int userId) {
        new AsyncTask<Integer, Void, List<JSONObject>>() {
            @Override
            protected List<JSONObject> doInBackground(Integer... params) {
                return RestClient.getMonthlyExpenseRate(params[0], params[1], params[2]);
            }

            @Override
            protected void onPostExecute(List<JSONObject> jsons) {
                String[] parentCategories = new String[jsons.size()];
                double[] rates = new double[jsons.size()];
                //get parent categories and corresponding rates in json objects
                for (int i = 0; i < jsons.size(); i++) {
                    parentCategories[i] = getStringFromJSONObject(jsons.get(i), "category");
                    rates[i] = getDoubleFromJSONObject(jsons.get(i), "rate");
                }
                Intent newIntent = new Intent(vBill.getContext(), PieChartActivity.class);
                newIntent.putExtra("categories", parentCategories);
                newIntent.putExtra("rates", rates);
                startActivity(newIntent);
            }
        }.execute(month, year, userId);
    }

    //refresh current location
    public void refreshCurrentLocation() {
        Activity activity = this.getActivity();
        //check if the app has the permission to access the location service
        canAccessLocation = new LocationPermissionManager(activity).canAccessLocation();
        //if not, ask for permission
        if(!canAccessLocation) {
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
            //if no location providers, prompt user to open them
            showText("No location Provider. Please open WiFi or GPS.");
            return ;
        }
        try {
            locationManager.requestLocationUpdates(locationProvider, 1, 1, locationListener);
            //get last time recorded location
            location = locationManager.getLastKnownLocation(locationProvider);
            if (location == null) {
                try {
                    //if user just open the GPS, let the location be refreshed and recorded
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                location = locationManager.getLastKnownLocation(locationProvider);
                locationManager.requestLocationUpdates(locationProvider, 1, 1, locationListener);
            }
            if (location == null)
                showText("Please open GPS or Wifi and refresh page");
        }
        catch (SecurityException secEx) {
            showText("Please enable location services");
        }
    }

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

        //if location changed, record the new location
        @Override
        public void onLocationChanged(Location location) {
            BillFragment.this.location = location;
        }
    };
}
