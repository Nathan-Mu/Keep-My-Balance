package edu.monash.fit4039.keepmybalance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapquest.mapping.maps.OnMapReadyCallback;
import com.mapquest.mapping.maps.MapboxMap;
import com.mapquest.mapping.maps.MapView;
import java.text.DecimalFormat;

import static edu.monash.fit4039.keepmybalance.KMBConstant.CUSTOM_DARK_RED;
import static edu.monash.fit4039.keepmybalance.KMBConstant.CUSTOM_GREEN;
import static edu.monash.fit4039.keepmybalance.KMBConstant.EXPENSE;
import static edu.monash.fit4039.keepmybalance.KMBConstant.INCOME;
import static edu.monash.fit4039.keepmybalance.KMBConstant.REMOVE_CHILD_RESPONSE;
import static edu.monash.fit4039.keepmybalance.Time.toText;

public class ViewActivity extends AppCompatActivity {
    private TextView vTvDate, vTvFundChangeType, vTvAmount, vTvCategory, vTvAccount, vTvLocation, vTvDescription;
    private FundChange fundChange;
    private MapboxMap vMapboxMap;
    private MapView vMapView;
    private Button vBtnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        setTitle("View");

        Intent intent = getIntent();
        fundChange = intent.getParcelableExtra("selectedFundChange");

        vTvDate = (TextView) findViewById(R.id.vTvDate);
        vTvFundChangeType = (TextView) findViewById(R.id.vTvFundChangeType);
        vTvAmount = (TextView) findViewById(R.id.vTvAmount);
        vTvCategory = (TextView) findViewById(R.id.vTvCategory);
        vTvAccount = (TextView) findViewById(R.id.vTvAccount);
        vTvLocation = (TextView) findViewById(R.id.vTvLocation);
        vTvDescription = (TextView) findViewById(R.id.vTvDescription);
        vBtnDelete = (Button) findViewById(R.id.vBtnDelete);

        vMapView = (MapView) findViewById(R.id.mapquestMapView);
        vMapView.onCreate(savedInstanceState);

        //set text to each attribute
        vTvDate.setText(toText(fundChange.getChangeDate(), "dd/MMM/yyyy"));
        vTvFundChangeType.setText(fundChange.getChangeType());
        if (fundChange.getChangeType().equalsIgnoreCase(INCOME))
            vTvFundChangeType.setTextColor(CUSTOM_GREEN);
        else if (fundChange.getChangeType().equalsIgnoreCase(EXPENSE))
            vTvFundChangeType.setTextColor(CUSTOM_DARK_RED);
        DecimalFormat df =new DecimalFormat("#0.00");
        vTvAmount.setText("Amount: " + df.format(fundChange.getAmount()));
        vTvCategory.setText("Category: " + fundChange.getChildCategoryId().getParentCategory().getParentCategoryName() + "-" + fundChange.getChildCategoryId().getChildCategoryName());
        vTvAccount.setText(fundChange.getAccountId().getAccountType());
        vTvLocation.setText(fundChange.getLocationName());
        if (fundChange.getDescription().isEmpty())
            vTvDescription.setText("No description");
        else
            vTvDescription.setText(fundChange.getDescription());

        final LatLng position = new LatLng(fundChange.getLocationLatitude(), fundChange.getLocationLongitude());

        //set map
        vMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                vMapboxMap = mapboxMap;
                vMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 11));
                addMarker(vMapboxMap, position);
            }
        });

        vBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForDelete(fundChange);
            }
        });
    }

    //resource: //resource: https://developer.mapquest.com/documentation/android-sdk/
    //license: https://developer.mapquest.com/legal
    @Override
    public void onResume()
    {
        super.onResume();
        vMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        vMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        vMapView.onSaveInstanceState(outState);
    }

    public void addMarker(MapboxMap mapboxMap, LatLng position)
    {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        mapboxMap.addMarker(markerOptions);
    }

    //show a dialog to ask user to confirm deleting
    private void askForDelete(FundChange fundChange) {
        final FundChange removeFundChange = fundChange;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove this record");
        builder.setMessage("Confirm to remove this record?");
        builder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        removeFundChange(removeFundChange);
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    //remove a fundchange
    private void removeFundChange(FundChange fundChange) {
        new AsyncTask<FundChange, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(FundChange... params) {
                return RestClient.deleteFundChange(params[0].getChangeId());
            }

            @Override
            protected void onPostExecute(Boolean isRemoved) {
                if (isRemoved) {
                    showText("The record has been removed");
                    Intent intent = getIntent();
                    setResult(REMOVE_CHILD_RESPONSE, intent);
                    finish();
                }
                else
                    showText("The record has not been removed. Please try again");
            }
        }.execute(fundChange);
    }

    //show a notification on screen
    private void showText(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
