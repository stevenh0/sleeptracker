package com.example.steven.sleeptracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class settingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


    }

    public void updateSleepPreferences(View view){
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        EditText slpHours = (EditText) findViewById(R.id.slpHrsEditText);

        TextView error = (TextView) findViewById(R.id.errorMessage);
        try {
            int hrs = Integer.parseInt(slpHours.getText().toString());
            if(1 <= hrs && hrs <= 23) {
                editor.putInt(getString(R.string.pref_sleep_hours), hrs);
                editor.commit();
                TextView success = (TextView) findViewById((R.id.successMessage));
                success.setText("Successfully updated sleep preferences");
            }
            else
                error.setText("Invalid number of hours!");

        }
        catch (NumberFormatException e){
            error.setText("Invalid input format!");
        }



    }
}
