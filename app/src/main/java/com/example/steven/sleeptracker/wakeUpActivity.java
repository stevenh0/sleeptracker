package com.example.steven.sleeptracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.Preference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class wakeUpActivity extends AppCompatActivity {

    private MediaPlayer mp;

    private Button goalButton;
    private Button alarmButton;

    private EditText goal1;
    private EditText goal2;
    private EditText goal3;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up);

        Intent info = getIntent();



        goalButton = (Button) findViewById(R.id.goalButton);
        alarmButton = (Button) findViewById(R.id.alarmButton);

        goal1 = (EditText) findViewById(R.id.goal1);
        goal2 = (EditText) findViewById(R.id.goal2);
        goal3 = (EditText) findViewById(R.id.goal3);

        prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        goalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGoals();
            }
        });
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
            }
        });

        Boolean playAlarm = info.getBooleanExtra(getString(R.string.intent_playAlarm), false);
        if(playAlarm) {
            mp = MediaPlayer.create(this, R.raw.dog);
            mp.setLooping(true);
            mp.start();
        }

        String todo = info.getStringExtra(getString(R.string.intent_task));
        if(todo.equals(getString(R.string.intent_sleep)))
            setupLayoutSleep();

        else //if(todo.equals(getString(R.string.intent_wakeup)))
            setupLayoutWakeup();



    }


    // if there is a media player active and playing, stop it
    public void stopAlarm(){
        if(mp==null)
            return;

        if(mp.isPlaying())
            mp.stop();
    }
    //check for missing goals, if all are completed, proceed
    public void setGoals(){

        // if not all 3 goal boxes have something in them, alert user
        if(isEmpty(goal1) || isEmpty(goal2) || isEmpty(goal3)){
            Toast toast = Toast.makeText(getApplicationContext(), "Not all goals were filled!", Toast.LENGTH_SHORT);
            toast.show();
        }

        // once all goals have been set, save them to preferences file and then show button to allow
        // user to stop the alarm
        else{
            SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(getString(R.string.pref_goal1), goal1.getText().toString());
            edit.putString(getString(R.string.pref_goal2), goal2.getText().toString());
            edit.putString(getString(R.string.pref_goal3), goal3.getText().toString());

            goalButton.setVisibility(View.INVISIBLE);
            alarmButton.setVisibility(View.VISIBLE);
        }
    }

    public boolean isEmpty(EditText et){
        if(et.getText().toString().matches(""))
            return true;
        else
            return false;
    }

    private void setupLayoutSleep(){
        TextView tv = (TextView) findViewById(R.id.goal_intro_msg);
        tv.setText("You have to set 3 goals for tomorrow before you'll be allowed to sleep!");

        goalButton.setVisibility(View.VISIBLE);
        alarmButton.setVisibility(View.INVISIBLE);

    }

    // set reminder alarms which are delayed if user checks that a goal has been completed
    private void setupLayoutWakeup(){
        TextView tv = (TextView) findViewById(R.id.goal_intro_msg);
        tv.setText("Here are today's goals");

        goal1.setText(prefs.getString(getString(R.string.pref_goal1), "Goal 1 not set"));
        goal2.setText(prefs.getString(getString(R.string.pref_goal2), "Goal 2 not set"));
        goal3.setText(prefs.getString(getString(R.string.pref_goal3), "Goal 3 not set"));

        setReminderAlarms();

        goalButton.setVisibility(View.INVISIBLE);
        alarmButton.setVisibility(View.VISIBLE);

}
    public void setReminderAlarms(){

        Intent intent = new Intent(this, reminderActivity.class);
        PendingIntent send = PendingIntent.getBroadcast(this, 0 , intent, 0);

        // set an alarm for 2 hours from now to remind user to do goals - if user indicates that a
        // goal has been completed using checkmark, delay this arlam. set another alarm upon this
        // alarm going off
        // goal should probably be a fragment that can be reused

        long curTime = System.currentTimeMillis();
        long alarm = curTime + (2 * 60 * 60 * 1000);
        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, alarm ,send);
    }

    public void stopAlarm(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
