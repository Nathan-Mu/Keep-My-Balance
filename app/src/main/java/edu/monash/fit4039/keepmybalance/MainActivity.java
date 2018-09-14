package edu.monash.fit4039.keepmybalance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import static edu.monash.fit4039.keepmybalance.KMBConstant.*;

public class MainActivity extends AppCompatActivity {

    private Button mBtnSignUp, mBtnLogin;
    private EditText mEtUserName, mEtPassword;
    private CheckBox mCbAutoLogin;
    private Boolean autoLogin = false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("Keep My Balance");

        //get sharedPreferences(spsKMB)
        //resource: https://developer.android.com/reference/android/content/SharedPreferences.html
        sharedPreferences = getSharedPreferences("spsKMB", Context.MODE_PRIVATE);
        //get editor of sharedPreferences
        editor = sharedPreferences.edit();
        //get auto login state
        autoLogin = sharedPreferences.getBoolean("autoLogin", false);

        //if auto login, read the auto login info
        if (autoLogin) {
            String[] array = readAutoLogin();
            processAutoLogin(array[0], array[1]);
        }

        mBtnSignUp = (Button) findViewById(R.id.mBtnSignUp);
        mBtnLogin = (Button) findViewById(R.id.mBtnLogin);
        mEtPassword = (EditText) findViewById(R.id.mEtPassword);
        mEtUserName = (EditText) findViewById(R.id.mEtUserName);
        mCbAutoLogin = (CheckBox) findViewById(R.id.mCbAutoLogin);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String userName = mEtUserName.getText().toString();
            String password = mEtPassword.getText().toString();
            //firstly check the input of username and password is not empty and blank
            if (isEditTextFilled(userName, password))
                // process normal login
                processNormalLogin(userName, Encryption.encryptToSHA(password));
            }
        });

        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(MainActivity.this, SignUpActivity.class);
                //start activity with a request code and wait for a response
                //resource: https://developer.android.com/training/basics/intents/result.html
                startActivityForResult(newIntent, CLICK_SIGN_UP_REQUEST);
            }
        });

        mCbAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autoLogin = isChecked;
            }
        });
    }

    //show a notification on screen
    private void showText(String string) {
        Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT).show();
    }

    //record auto login info when user checks auto login and successfully login
    private void recordAutoLogin(UserInfo user) {
        try {
            //record the auto login info in sharedPreferences
            editor.putInt("autoLoginUserId", user.getUserId());
            editor.putString("autoLoginPassword", user.getPassword());
            editor.putBoolean("autoLogin", true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            editor.commit();
        }
    }

    //process auto login by userId and encrypted password
    //resource: https://developer.android.com/reference/android/os/AsyncTask.html
    private void processAutoLogin(String userId, String password) {
        new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {
                return RestClient.getAutoLogin(params[0], params[1]);
            }

            @Override
            protected void onPostExecute(JSONObject json) {
                //create the user object
                UserInfo user = new UserInfo(json);
                //if user id > 0, it means user info is matched with server side
                if (user.getUserId() > 0) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    finish();
                } else {
                    //if user cannot login by the auto login info. That might mean user info has been updated by other devices.
                    //So, clean user auto login info and let user re-login
                    cleanAutoLogin();
                    showText("Your password has been updated. Please log in again.");
                }
            }
        }.execute(userId, password);
    }

    //clean auto login info from sharedPreferences
    private void cleanAutoLogin() {
        try {
            editor.putInt("autoLoginUserId", 0);
            editor.putString("autoLoginPassword", "");
            editor.putBoolean("autoLogin", false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            editor.commit();
        }
    }

    //read auto login info from sharedPreferences
    private String[] readAutoLogin() {
        String stId = String.valueOf(sharedPreferences.getInt("autoLoginUserId", 0));
        String password = sharedPreferences.getString("autoLoginPassword", "");
        return new String[] {stId, password};
    }

    //process normal login by user name and password which is entered by user
    //resource: https://developer.android.com/reference/android/os/AsyncTask.html
    private void processNormalLogin(String userName, String password) {
        new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {
                return RestClient.getLoginInfo(params[0], params[1]);
            }

            @Override
            protected void onPostExecute(JSONObject json) {
                //create the user object
                UserInfo user = new UserInfo(json);
                //if user id > 0, it means user info is matched with server side
                if (user.getUserId() > 0) {
                    //if user checked auto login, then record auto login info(user id and encrypted password)
                    if (autoLogin) {
                        recordAutoLogin(user);
                    }
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    finish();
                } else {
                    //if user login info is not matched with server side, let the user know and reset password EditText
                    showText("Username and password not matched.");
                    mEtPassword.setText("");
                }
            }
        }.execute(userName, password);
    }

    //check user username and password is not empty or null.
    //if user input is invalid, show a notification
    //return true if username and password is not empty or null
    private Boolean isEditTextFilled (String username, String password) {
        if (username.trim().isEmpty()) {
            showText("Username cannot be empty or blank");
            return false;
        } else if (password.trim().isEmpty()) {
            showText("Password cannot be empty or blank");
            return false;
        } else {
            return true;
        }
    }

    //when the child activity finish and go back with a response code, then check if the response and request is matched
    //if matched the requirement, that means user just sign up successfully. Auto fill the field.
    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        if (requestCode == CLICK_SIGN_UP_REQUEST && responseCode == SIGN_UP_RESPONSE) {
            showText("Sign up successfully. Please login");
            mEtUserName.setText(intent.getStringExtra("username"));
            mEtPassword.setText(intent.getStringExtra("password"));
        }
    }
}
