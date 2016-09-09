package com.example.steven.sleeptracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private static double longitude = -122.94;
    private static double latitude = 49.27;
    private Location mLastLocation;
    private double temp;
    private String weather_image_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createGoogleAPI();
        getUserLocation();

        try {
            getWeatherData();
        }
        catch (IOException e){

        }



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
        Log.i("debug_i", "getUserLocation ran");


        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        catch (SecurityException e){
            Log.i("debug_i", "security exception");
            return;
        }

        Log.i("debug_i", "got here");
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            Log.i("debug_i", Double.toString(latitude));
            Log.i("debug_i", Double.toString(longitude));
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
            Log.i("debug_i", (addresses.get(0).getLocality()));
        String BASE_URL = "http://api.wunderground.com/api/74170a3db8502e99/conditions/q/CA/burnaby.json";

        ConnectivityManager connMgr = (ConnectivityManager)
            getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new networkConnectionTask().execute(BASE_URL);
        } else {
            Log.i("debug_i", "No network connection available.");
        }

    }


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


    public class networkConnectionTask extends AsyncTask<String, Void, String> {
        @Override

        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // using url, get json object and return it as string
        public String downloadUrl(String myurl) throws IOException{
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("debug_i", "The response is: " + response);
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));



                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    sb.append(inputLine + "\n");
                in.close();

                JSONObject obj = new JSONObject(sb.toString());
                JSONObject newObj = obj.getJSONObject("current_observation");
                Double temp = newObj.getDouble("temp_c");
                String icon = newObj.getString("icon_url");

                TextView txt = (TextView) findViewById(R.id.weather_temp);
                txt.setText(Double.toString(temp) + "C");

                URL imgURL = new URL(icon);
                HttpURLConnection img_conn = (HttpURLConnection) imgURL.openConnection();
                img_conn.setDoInput(true);
                img_conn.connect();
                InputStream input = img_conn.getInputStream();
                Bitmap img_bm = BitmapFactory.decodeStream(input);
                ImageView img_v = (ImageView) findViewById(R.id.weather_icon);
                img_v.setImageBitmap(img_bm);


            }
            catch (JSONException e){
                Log.i("debug_i", "Something went wrong with json");
            }

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            finally {
                if (is != null) {
                    is.close();
                }
            }

            Log.i("debug_i", sb.toString());
            return sb.toString();

        }
    }

}
