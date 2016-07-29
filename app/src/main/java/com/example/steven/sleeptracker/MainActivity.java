package com.example.steven.sleeptracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.*;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static GoogleApiClient mGoogleApiClient;
    private static double longitude;
    private static double latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        createGoogleAPI();
        getUserLocation();
        try {
            getWeatherData();
        }
        catch (IOException e){

        }

*/
        TextView layout = (TextView) findViewById(R.id.todaysDate);
        SimpleDateFormat today = new SimpleDateFormat("MMMM dd, yyyy");
        Calendar c = Calendar.getInstance();
        String date = today.format(c.getTime());
        layout.setText(date);


        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        prefs.getInt(getString(R.string.bed_hour), -1);


    }

    public void helpPopup(View view){
        Intent intent = new Intent(this, helpActivity.class);
        startActivity(intent);

    }

    public void setAlarm(View view){
        Intent intent = new Intent(this, alarmActivity.class);
        startActivity(intent);
    }

    public void adjustSettings(View view){
        Intent intent = new Intent(this, settingsActivity.class);
        startActivity(intent);
    }


// WIP / unused location/weather information
/*
    private void createGoogleAPI(){
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this,
                    this)
                    .addApi(LocationServices.API)
                    .build();

        }
        mGoogleApiClient.connect();
    }

    private void getUserLocation(){
        if(mGoogleApiClient == null){
            createGoogleAPI();
        }

        if ( Build.VERSION.SDK_INT >= 23 &&
 //               ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }
    }

    protected void onStop() {
        if(mGoogleApiClient!=null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void getWeatherData() throws IOException{
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
        if (addresses.size() > 0)
            System.out.println(addresses.get(0).getLocality());
        try {
            String BASE_URL = "http://api.wunderground.com/api/74170a3db8502e99/conditions/q/CA/burnaby/";
            HttpURLConnection con = (HttpURLConnection) (new URL(BASE_URL)).openConnection();
            con.connect();

        }
        catch(MalformedURLException e){

        }
        catch(IOException e){

        }

    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onConnectionFailed(ConnectionResult con){
        System.exit(-1);
    }
}
