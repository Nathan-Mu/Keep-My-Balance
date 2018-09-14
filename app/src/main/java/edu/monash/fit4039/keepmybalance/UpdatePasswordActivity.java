package edu.monash.fit4039.keepmybalance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static edu.monash.fit4039.keepmybalance.Encryption.encryptToSHA;
import static edu.monash.fit4039.keepmybalance.KMBConstant.UPDATE_PASSWORD_RESPONSE;
import static edu.monash.fit4039.keepmybalance.Validation.isLowerUpperAndNumeric;

public class UpdatePasswordActivity extends AppCompatActivity {
    private EditText uEtOldPassword, uEtNewPassword, uEtConfirmPassword;
    private Button uBtnUpdate;
    private UserInfo user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        setTitle("Update Password");

        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");

        uEtOldPassword = (EditText) findViewById(R.id.uEtOldPassword);
        uEtNewPassword = (EditText) findViewById(R.id.uEtNewPassword);
        uEtConfirmPassword = (EditText) findViewById(R.id.uEtConfirmPassword);
        uBtnUpdate = (Button) findViewById(R.id.uBtnUpdate);

        uBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (isInputValid()) {
                updatePassword(uEtNewPassword.getText().toString().trim());
            }
            }
        });
    }

    //check if user input is valid
    //1. old password, new password, confirm password cannot be empty
    //2. new password format (at least one number, one uppercase, one lowercase, only contains character and number, length(8-20))
    //3. confirm password should be same as new password
    private boolean isInputValid() {
        String oldPassword = uEtOldPassword.getText().toString().trim();
        if (oldPassword.isEmpty()) {
            uEtOldPassword.setError("Please enter old password");
            return false;
        } else if (!encryptToSHA(oldPassword).equals(user.getPassword())) {
            uEtOldPassword.setError("Old password not matched");
            return false;
        }

        String newPassword = uEtNewPassword.getText().toString().trim();
        if (newPassword.isEmpty()) {
            uEtNewPassword.setError("Please enter new password");
            return false;
        } else if (isLowerUpperAndNumeric(newPassword) && newPassword.length() < 8 && newPassword.length() > 20) {
            uEtNewPassword.setError("Password format is invalid");
            return false;
        }
        else if (newPassword.equals(oldPassword)) {
            uEtNewPassword.setError("New password cannot be same as old password");
            return false;
        }

        String confirmPassword = uEtConfirmPassword.getText().toString().trim();
        if (confirmPassword.isEmpty()) {
            uEtConfirmPassword.setError("Please confirm new password");
            return false;
        } else if (!confirmPassword.equals(newPassword)) {
            uEtConfirmPassword.setError("Password not matched");
            return false;
        }
        return true;
    }

    //update password on server side
    private void updatePassword(String newPassword) {
        UserInfo updatedUser = user;
        updatedUser.setPassword(encryptToSHA(newPassword));
        new AsyncTask<UserInfo, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(UserInfo... params) {
                return RestClient.updateUser(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean isUpdated) {
                if (isUpdated) {
                    cleanAutoLogin();
                    setResult(UPDATE_PASSWORD_RESPONSE, getIntent());
                    finish();
                } else {
                    showText("Password has not been updated");
                }
            }
        }.execute(updatedUser);
    }

    //show a notification on screen
    public void showText(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    //clean auto login
    public void cleanAutoLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("spsKMB", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
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

}
