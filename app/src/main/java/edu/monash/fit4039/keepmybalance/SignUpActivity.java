package edu.monash.fit4039.keepmybalance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static edu.monash.fit4039.keepmybalance.KMBConstant.SIGN_UP_RESPONSE;
import static edu.monash.fit4039.keepmybalance.Validation.*;

public class SignUpActivity extends AppCompatActivity {

    private EditText sEtUsername, sEtPassword, sEtConfirmPassword;
    private Button sBtnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle("Sign Up");

        sEtUsername = (EditText) findViewById(R.id.sEtUserName);
        sEtPassword = (EditText) findViewById(R.id.sEtPassword);
        sEtConfirmPassword = (EditText) findViewById(R.id.sEtConfirmPassword);
        sBtnSignUp = (Button) findViewById(R.id.sBtnSignUp);

        sBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = sEtUsername.getText().toString();
                String password = sEtPassword.getText().toString();
                String confirmPassword = sEtConfirmPassword.getText().toString();

                //validate user input
                if (isValid(userName, password, confirmPassword)) {
                    UserInfo user = new UserInfo(userName, Encryption.encryptToSHA(password));
                    createUser(user);
                }
            }
        });
    }

    //this method will check the user input
    //user name cannot be empty and should follow the user name format
    //(1.only contains character or number 2.length: 1-20)
    //password cannot be empty and should follow the password format
    //(1.only contains character and number 2.contains at least one uppercase, one lower case and one number 3.length: 8-20)
    //confirming password should be same as password
    //if the input is invalid, the system will show an error and corresponding widget
    public boolean isValid(String userName, String password, String confirmPassword) {
        if (userName.isEmpty()) {
            sEtUsername.setError("Please enter username");
            return false;
        } else if (!isNumericOrChar(userName)) {
            sEtUsername.setError("User name format is invalid");
            return false;
        } else if (password.isEmpty()) {
            sEtPassword.setError("Please enter password");
            return false;
        } else if (isLowerUpperAndNumeric(password) && password.length() < 8 && password.length() > 20) {
            sEtPassword.setError("Password format is invalid");
            return false;
        } else if (confirmPassword.isEmpty()) {
            sEtConfirmPassword.setError("Please confirm password");
            return false;
        } else if (!confirmPassword.equalsIgnoreCase(password)) {
            sEtConfirmPassword.setError("Two passwords are not same");
            return false;
        } else {
            return true;
        }
    }

    //show a notification on the screen
    public void showText(String string) {
        Toast.makeText(SignUpActivity.this, string, Toast.LENGTH_SHORT).show();
    }

    //create user (post to server) by AsyncTask
    //resource: https://developer.android.com/reference/android/os/AsyncTask.html
    public void createUser(UserInfo user) {
        new AsyncTask<UserInfo, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(UserInfo... params) {
                //post to server and get response
                return RestClient.createUser(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean isCreated) {
                if (isCreated) {
                    //if user is created successfully, send the user name and origin password back
                    Intent intent = getIntent();
                    intent.putExtra("username", sEtUsername.getText().toString());
                    intent.putExtra("password", sEtPassword.getText().toString());
                    //set result and go back to the parent activity(or fragment) with a response code
                    setResult(SIGN_UP_RESPONSE, intent);
                    finish();
                } else {
                    showText("Username exist");
                }
            }
        }.execute(user);
    }
}
