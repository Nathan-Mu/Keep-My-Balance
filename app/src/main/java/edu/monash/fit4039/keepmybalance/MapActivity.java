package edu.monash.fit4039.keepmybalance;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.mapping.maps.MapboxMap;
import com.mapquest.mapping.maps.OnMapReadyCallback;

import java.util.ArrayList;

//resource: https://developer.mapquest.com/documentation/android-sdk/
//license: https://developer.mapquest.com/legal
public class MapActivity extends AppCompatActivity {

    private MapboxMap mMapboxMap;
    private MapView mMapView;
    private ArrayList<FundChange> allLocations;
    private ArrayList<FundChange> locations;
    private Icon iconUser, iconOver100, iconNotOver100;
    private Spinner mSpDistance;
    private static final Integer[] DISTANCE_OPTIONS = {0, 1, 2, 5, 10, 50, 0};
    private static final String[] CHOOSE_OPTIONS = {"----Please choose----", "1km", "2km", "5km", "10km", "50km", "Any"};
    private ArrayAdapter<String> adapter;
    private LatLng currentPosition;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        currentLocation = intent.getParcelableExtra("location");
        currentPosition =  new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        allLocations = intent.getParcelableArrayListExtra("fundChanges");
        locations = allLocations;

        //set icon
        IconFactory iconFactory = IconFactory.getInstance(this);
        iconOver100 = iconFactory.fromResource(R.drawable.map_marker_red);
        iconUser = iconFactory.fromResource(R.drawable.map_marker_blue);
        iconNotOver100 = iconFactory.fromResource(R.drawable.map_marker_pink);

        mSpDistance = (Spinner) findViewById(R.id.mSpDistance);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, CHOOSE_OPTIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpDistance.setAdapter(adapter);
        mMapView = (MapView) findViewById(R.id.mMapView);
        mMapView.onCreate(savedInstanceState);

        mSpDistance.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0 && position != 6) {
                    //if user choose a specified distance, it will the points inside the distance
                    locations = new ArrayList<FundChange>();
                    for (FundChange f: allLocations) {
                        Double distance = Distance.getDistance(f.getLocationLatitude(), f.getLocationLongitude(), currentLocation.getLatitude(), currentLocation.getLongitude());
                        if (distance <= (double) DISTANCE_OPTIONS[position])
                            locations.add(f);
                    }
                } else {
                    //if user not choose or choose "any" it will show all locations
                    locations = allLocations;
                }
                if (mMapboxMap != null)
                    mMapboxMap.clear();
                setMap();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause()
    { super.onPause(); mMapView.onPause(); }

    @Override
    protected void onDestroy()
    { super.onDestroy(); mMapView.onDestroy(); }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    //add a single marker to a point on the map
    public void addMarker(MapboxMap mapboxMap, LatLng positionStudent, String locationName, Icon icon)
    {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(positionStudent);
        markerOptions.title(locationName);
        markerOptions.icon(icon);
        mapboxMap.addMarker(markerOptions);
    }

    //add all markers
    public void addAllFundChangeMarkers() {
        for (FundChange fundChange: locations) {
            LatLng position = new LatLng(fundChange.getLocationLatitude(), fundChange.getLocationLongitude());
            String locationName = fundChange.getLocationName();
            if (fundChange.getAmount() > 100) {
                addMarker(mMapboxMap, position, locationName, iconOver100);
            } else {
                addMarker(mMapboxMap, position, locationName, iconNotOver100);
            }
        }
    }

    //set the map visiable and set the markers
    public void setMap() {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mMapboxMap = mapboxMap;
                mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 10));
                addMarker(mMapboxMap, currentPosition, "Your Position", iconUser);
                addAllFundChangeMarkers();
            }
        });
    }
}
