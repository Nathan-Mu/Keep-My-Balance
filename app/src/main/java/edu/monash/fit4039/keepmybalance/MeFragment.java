package edu.monash.fit4039.keepmybalance;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static edu.monash.fit4039.keepmybalance.KMBConstant.*;

import org.w3c.dom.Text;

/**
 * Created by nathan on 17/5/17.
 */

public class MeFragment extends Fragment {
    View vMe;
    private TextView mTvWelcome;
    private Button mBtnWeb, mBtnUpdatePassword, mBtnManageCategories;
    private UserInfo user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vMe = inflater.inflate(R.layout.fragment_me, container, false);

        Intent intent = this.getActivity().getIntent();
        user = intent.getParcelableExtra("user");

        mTvWelcome = (TextView) vMe.findViewById(R.id.mTvWelcome);
        mBtnWeb = (Button) vMe.findViewById(R.id.mBtnWeb);
        mBtnUpdatePassword = (Button) vMe.findViewById(R.id.mBtnUpdatePassword);
        mBtnManageCategories = (Button) vMe.findViewById(R.id.mBtnManageCategories);

        //open website
        mBtnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWeb();
            }
        });

        //update password
        mBtnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(vMe.getContext(), UpdatePasswordActivity.class);
                newIntent.putExtra("user", user);
                startActivityForResult(newIntent, CLICK_UPDATE_PASSWORD_REQUEST);
            }
        });

        //manage categories
        mBtnManageCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(vMe.getContext(), ManageCategoriesActivity.class);
                newIntent.putExtra("user", user);
                startActivity(newIntent);
            }
        });

        initUI();

        return vMe;
    }

    //set welcome text
    private void initUI() {
        mTvWelcome.setText("Hello, " + user.getUsername());
    }

    //open website (https://www.unicef.org.au/)
    private void openWeb() {
        Uri uri = Uri.parse("https://www.unicef.org.au/");
        Intent newIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(newIntent);
    }

    //get response code of child activity
    //if user just updated the password, he should re-login
    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        if (requestCode == CLICK_UPDATE_PASSWORD_REQUEST && responseCode == UPDATE_PASSWORD_RESPONSE) {
            Intent newIntent = new Intent(vMe.getContext(), MainActivity.class);
            startActivity(newIntent);
            Toast.makeText(vMe.getContext(), "Please re-login", Toast.LENGTH_LONG).show();
            this.getActivity().finish();
        }
    }
}
