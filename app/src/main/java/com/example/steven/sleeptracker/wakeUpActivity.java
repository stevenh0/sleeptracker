package com.example.steven.sleeptracker;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class wakeUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up);

        MediaPlayer mp = MediaPlayer.create(this, R.raw.dog);
        mp.setLooping(true);
        mp.start();

    }

    public void stopAlarm(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
