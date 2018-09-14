package edu.monash.fit4039.keepmybalance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Manifest;

import static edu.monash.fit4039.keepmybalance.JSONReader.*;
import static edu.monash.fit4039.keepmybalance.KMBConstant.ADD_NEW_RECORD_RESPONSE;
import static edu.monash.fit4039.keepmybalance.KMBConstant.EXPENSE;
import static edu.monash.fit4039.keepmybalance.KMBConstant.INCOME_ARRAY;
import static edu.monash.fit4039.keepmybalance.Time.*;

public class AddNewRecordActivity extends AppCompatActivity {
    private Spinner aSpFundChangeType, aSpParentCategory, aSpChildCategory, aSpAccount;
    private Button aBtnChooseDate, aBtnConfirm;
    private EditText aEtAmount, aEtLocationName, aEtDescription;
    private UserInfo user;
    private List<ParentCategory> parentCategories = new ArrayList<>();
    private List<ChildCategory> childCategories = new ArrayList<>();
    private List<Account> accounts = new ArrayList<>();
    private static final String[] initChildCategories = {"----Please choose----"};
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_record);

        this.setTitle("Add Record");

        //get user and currentLocation from intent
        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        currentLocation = intent.getParcelableExtra("location");

        aSpFundChangeType = (Spinner) findViewById(R.id.aSpFundChangeType);
        aSpParentCategory = (Spinner) findViewById(R.id.aSpParentCategory);
        aSpChildCategory = (Spinner) findViewById(R.id.aSpChildCategory);
        aSpAccount = (Spinner) findViewById(R.id.aSpAccount);
        aBtnChooseDate = (Button) findViewById(R.id.aBtnChooseDate);
        aBtnConfirm = (Button) findViewById(R.id.aBtnConfirm);
        aEtAmount = (EditText) findViewById(R.id.aEtAmount);
        aEtLocationName = (EditText) findViewById(R.id.aEtLocationName);
        aEtDescription = (EditText) findViewById(R.id.aEtDescription);

        //load account data
        AccountData accountData = new AccountData();
        accountData.execute(new Integer[] {user.getUserId()});

        //if location is not null, then find the address of this location
        if (currentLocation != null) {
            LocationData locationData = new LocationData();
            locationData.execute(currentLocation);
        }

        aSpFundChangeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if position is 0, that means user choose expense, if position is 1, that means user choose income
                //if user choose expense, show all parent categories expect income
                //if user choose income, show income as parent category
                if (position == 0) {
                    ExpenseParentCategoryData parentCategoryData = new ExpenseParentCategoryData();
                    parentCategoryData.execute(new Integer[]{user.getUserId()});
                } else if (position == 1) {
                    IncomeParentCategoryData incomeParentCategoryData = new IncomeParentCategoryData();
                    incomeParentCategoryData.execute(new Integer[] {user.getUserId()});
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        aBtnChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = getCalendarYear();
                int month = getCalendarMonth();
                int day = getCalendarDay();
                //show a date picker dialog
                new DatePickerDialog(AddNewRecordActivity.this, dateListener, year, month, day).show();
            }
        });

        aBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FundChange fundChange = createFundChange();
                if (fundChange != null) {
                    postToServer(fundChange);
                }
            }
        });

        aSpParentCategory.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if expense, show the child categories based on the selected parent category
                //if income, show the child categories of income
                if (aSpFundChangeType.getSelectedItem().toString().equalsIgnoreCase(EXPENSE)) {
                    if (position == 0) {
                        //if user does not choose a parent category show init child categories (nothing in it, only a prompt message)
                        loadSpinnerData(aSpChildCategory, initChildCategories);
                    } else {
                        //if user has chosen a parent category, show the child categories of this parent category
                        ChildCategoryData childCategoryData = new ChildCategoryData();
                        childCategoryData.execute(new Integer[]{user.getUserId(), parentCategories.get(position - 1).getParentCategoryId()});
                    }
                } else {
                    //load child categories of income
                    ChildCategoryData childCategoryData = new ChildCategoryData();
                    childCategoryData.execute(new Integer[]{user.getUserId(), parentCategories.get(0).getParentCategoryId()});
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    //create fund change in server
    //resources: https://developer.android.com/reference/android/os/AsyncTask.html
    private void postToServer(FundChange fundChange) {
        new AsyncTask<FundChange, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(FundChange... params) {
                return RestClient.createFundChange(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean isCreated) {
                if (isCreated) {
                    //if fund change is created, then set result and back to parent activity
                    Intent intent = getIntent();
                    setResult(ADD_NEW_RECORD_RESPONSE, intent);
                    finish();
                } else {
                    //something unexpected happens
                    showText("Error: Disconnection to server");
                }
            }
        }.execute(fundChange);
    }

    //load date to spinner using ArrayAdapter
    //resource: https://stackoverflow.com/questions/2784081/android-create-spinner-programmatically-from-array
    private void loadSpinnerData(Spinner spinner, String[] array) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    //get and load parent categories belonged to expense
    //resources: http://moodle.vle.monash.edu/pluginfile.php/5474351/mod_resource/content/1/FIT5046-AndroidTute7-AsyncTask-2017.pdf
    private class ExpenseParentCategoryData extends AsyncTask<Integer, Void, List<JSONObject>> {
        @Override
        protected List<JSONObject> doInBackground(Integer... params) {
            return RestClient.getExpenseParentCategories(params[0]);
        }

        @Override
        protected void onPostExecute(List<JSONObject> jsons) {
            String[] parentCategoryArray = new String[jsons.size() + 1];
            parentCategories = new ArrayList<>();
            parentCategoryArray[0] = "----Please choose----";
            //set value in array which is the value of items in spinner
            for (int i = 0; i < jsons.size(); i++) {
                ParentCategory parentCategory = new ParentCategory(jsons.get(i));
                parentCategories.add(parentCategory);
                parentCategoryArray[i + 1] = parentCategory.getParentCategoryName();
            }
            //load data to spinner
            loadSpinnerData(aSpParentCategory, parentCategoryArray);
        }
    }

    //get and load parent category (income) belonged to income
    //resources: http://moodle.vle.monash.edu/pluginfile.php/5474351/mod_resource/content/1/FIT5046-AndroidTute7-AsyncTask-2017.pdf
    private class IncomeParentCategoryData extends AsyncTask<Integer, Void, List<JSONObject>> {
        @Override
        protected List<JSONObject> doInBackground(Integer... params) {
            return RestClient.getIncomeParentCategories(params[0]);
        }

        @Override
        protected void onPostExecute(List<JSONObject> jsons) {
            ParentCategory parentCategory = new ParentCategory(jsons.get(0));
            parentCategories = new ArrayList<>();
            parentCategories.add(parentCategory);
            String[] parentCategoryArray = new String[] {parentCategory.getParentCategoryName()};
            loadSpinnerData(aSpParentCategory, parentCategoryArray);
        }
    }

    //get and load child categories belonged to a parent category
    //resources: http://moodle.vle.monash.edu/pluginfile.php/5474351/mod_resource/content/1/FIT5046-AndroidTute7-AsyncTask-2017.pdf
    private class ChildCategoryData extends AsyncTask<Integer, Void, List<JSONObject>> {
        @Override
        protected List<JSONObject> doInBackground(Integer... params) {
            return RestClient.getAllChildCategoriesFromOneParentCategory(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(List<JSONObject> jsons) {
            String[] childCategoryArray = new String[jsons.size() + 1];
            childCategoryArray[0] = "----Please choose----";
            //set value in array which is the value of items in spinner
            for (int i = 0; i < jsons.size(); i++) {
                ChildCategory childCategory = new ChildCategory(jsons.get(i));
                childCategories.add(childCategory);
                childCategoryArray[i + 1] = childCategory.getChildCategoryName();
            }
            //load data to spinner
            loadSpinnerData(aSpChildCategory, childCategoryArray);
        }
    }

    //get and load accounts belonged to the user
    //resources: http://moodle.vle.monash.edu/pluginfile.php/5474351/mod_resource/content/1/FIT5046-AndroidTute7-AsyncTask-2017.pdf
    private class AccountData extends AsyncTask<Integer, Void, List<JSONObject>> {
        @Override
        protected List<JSONObject> doInBackground(Integer... params) {
            return RestClient.getAllAccounts(params[0]);
        }

        @Override
        protected void onPostExecute(List<JSONObject> jsons) {
            String[] accountArray = new String[jsons.size() + 1];
            accountArray[0] = "----Please choose----";
            //set value in array which is the value of items in spinner
            for (int i = 0; i < jsons.size(); i++) {
                Account account = new Account(jsons.get(i));
                accounts.add(account);
                accountArray[i + 1] = account.getAccountType();
            }
            //load data to spinner
            loadSpinnerData(aSpAccount, accountArray);
        }
    }

    //get the address of a location
    private class LocationData extends AsyncTask<Location, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Location... params) {
            return SearchService.getAddress(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            JSONArray results = getJSONArrayFromJSONObject(jsonObject, "results");
            JSONObject firstResult = getFirstFromArray(results);
            String formattedAddress = getStringFromJSONObject(firstResult, "formatted_address");
            String[] components = formattedAddress.split(",");
            String address = components[0];
            aEtLocationName.setText(address);
        }
    }

    //show a date picker dialog
    //resources: https://developer.android.com/reference/android/widget/DatePicker.html
    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int newYear, int newMonth, int newDay) {
            String datePick = new DecimalFormat("#00").format(newDay) + "/" + new DecimalFormat("#00").format(newMonth + 1) + "/" + newYear;
            Date temp = Time.toDate(datePick, "dd/MM/yyyy");
            if (temp.after(Time.getCurrentDate())) {
                showText("You cannot choose a day in future.");
                aBtnChooseDate.setText("CHOOSE DATE");
            } else {
                aBtnChooseDate.setText(datePick);
            }
        }
    };

    //show a notification on screen
    public void showText(String string) {
        Toast.makeText(AddNewRecordActivity.this, string, Toast.LENGTH_SHORT).show();
    }

    //create fund change object (by gathering the user input)
    private FundChange createFundChange() {
        String changeType = aSpFundChangeType.getSelectedItem().toString();
        double amount = 0.0;
        try {
            amount = Double.valueOf(aEtAmount.getText().toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String date = aBtnChooseDate.getText().toString();
        String locationName = aEtLocationName.getText().toString();
        double latitude = 0.0;
        double longitude = 0.0;
        if (currentLocation != null) {
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();
        }
        String description = aEtDescription.getText().toString();

        //validate user input
        if (validateUserInput(amount, locationName, description, aSpChildCategory.getSelectedItemPosition(), aSpAccount.getSelectedItemPosition(), date)) {
            //if user input is valid, create the object
            int childCategoryIndex = aSpChildCategory.getSelectedItemPosition() - 1;
            ChildCategory childCategory = childCategories.get(childCategoryIndex);
            int accountCategoryIndex = aSpAccount.getSelectedItemPosition() - 1;
            Account account = accounts.get(accountCategoryIndex);
            return new FundChange(changeType, amount, childCategory, account, toDate(date, "dd/MM/yyyy"), locationName, latitude, longitude, description);
        } else
            return null;
    }

    //validate the user input
    private boolean validateUserInput(double amount, String locationName, String description, int childCategoryPosition, int accountPosition, String textDate) {
        if (amount <= 0) {
            //amount cannot be 0 and empty (if it is empty, it will return 0)
            aEtAmount.setError("Invalid amount input");
            return false;
        } else if (childCategoryPosition < 1) {
            //child category spinner must be chosen. So the parent category spinner is chosen
            showText("Please choose a category");
            return false;
        } else if (accountPosition < 1){
            //account spinner must be chosen
            showText("Please choose an account");
            return false;
        } else if (textDate.equalsIgnoreCase("CHOOSE DATE")) {
            //date must be picked
            showText("Please choose a date");
            return false;
        } else if (locationName.trim().isEmpty()) {
            //location cannot be empty or blank
            aEtLocationName.setError("Location name cannot be empty");
            return false;
        } else if (description.length() > 30) {
            //description should be shorter than 30-character
            aEtDescription.setError("Only a brief description. No longer than 30 characters.");
            return false;
        } else {
            return true;
        }
    }
}