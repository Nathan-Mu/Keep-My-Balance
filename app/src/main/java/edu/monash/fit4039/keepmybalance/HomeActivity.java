package edu.monash.fit4039.keepmybalance;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

//resource: https://developer.android.com/training/implementing-navigation/nav-drawer.html
//resource: http://moodle.vle.monash.edu/pluginfile.php/5062692/mod_resource/content/7/FIT5046-AndroidTute6-NavigationDrawer-2017.pdf
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //get SharedPreferences
        //resource: https://developer.android.com/reference/android/content/SharedPreferences.html
        sharedPreferences = getSharedPreferences("spsKMB", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks. The action bar wilL automatically handle clicks on the Home/Up button, so long as it can specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        //inflate the fragment, when user choose the fragment in menu
        Fragment nextFragment = null;
        switch (id) {
            case R.id.nav_logout:
                cleanAutoLogin();
                Intent newIntent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(newIntent);
                finish();
                break;
            case R.id.nav_account:
                nextFragment = new AccountFragment();
                break;
            case R.id.nav_bill:
                nextFragment = new BillFragment();
                break;
            case R.id.nav_home:
                nextFragment = new HomeFragment();
                break;
            case R.id.nav_about:
                nextFragment = new AboutFragment();
                break;
            case R.id.nav_me:
                nextFragment = new MeFragment();
                break;
        }
        if (id != R.id.nav_logout) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, nextFragment).commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    //if user choose logout, it will clean the auto login info
    public void cleanAutoLogin() {
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
