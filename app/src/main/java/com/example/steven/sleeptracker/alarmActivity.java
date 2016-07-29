package com.example.steven.sleeptracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.sql.Time;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class alarmActivity extends AppCompatActivity {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public void setAlarm(View view) {
        TimePicker alarm = (TimePicker) findViewById(R.id.timePicker);

        int hour;
        int min;

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int sleepAmount = sharedPref.getInt(getString(R.string.pref_sleep_hours), 8);

        //get time that user has selected as alarm
        if (Build.VERSION.SDK_INT >= 23) {
            hour = alarm.getHour();
            min = alarm.getMinute();
        } else {
            hour = alarm.getCurrentHour();
            min = alarm.getCurrentMinute();
        }

        //get suggested bedtime
        int sleepHour = (hour + 24 - sleepAmount) % 24;

        //convert to milliseconds to use with AlarmManager
        long wakeUpTime = TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(min);
        long sleepTime = TimeUnit.HOURS.toMillis(sleepHour) + TimeUnit.MINUTES.toMillis(min);

        //play wakeUpActivity on each alarm
        Intent intent = new Intent(this, wakeUpActivity.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        // initialize AlarmManager and set two alarms, update preferences with relevant information
        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, sleepTime, AlarmManager.INTERVAL_DAY, alarmIntent);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, wakeUpTime, AlarmManager.INTERVAL_DAY, alarmIntent);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.bed_hour), sleepHour);
        editor.putInt(getString(R.string.wakeup_hour), hour);
        editor.putInt(getString(R.string.wakeup_min), min);
        editor.commit();


        Intent backIntent = new Intent(this, MainActivity.class);
        startActivity(backIntent);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "alarm Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.steven.sleeptracker/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "alarm Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.steven.sleeptracker/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
