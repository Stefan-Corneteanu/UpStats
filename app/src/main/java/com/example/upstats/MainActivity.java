package com.example.upstats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private SimpleDateFormat sdf;
    private final long addedMillis = 1000;
    private TextView foregroundTimer;
    private TextView backgroundTimer;
    private boolean isInBackground;
    private Handler handler;
    private Runnable timerThread; //separate thread for updating both timers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sdf = new SimpleDateFormat("HH:mm:ss"); //same format as shown in the activity_main.xml timers
        setContentView(R.layout.activity_main);
        foregroundTimer = (TextView) findViewById(R.id.ForegroundTime);
        backgroundTimer = (TextView) findViewById(R.id.BackgroundTime);
        handler = new Handler(Looper.getMainLooper());
        timerThread = new Runnable() {
            @Override
            public void run() {
                if (isInBackground) {
                    increaseTime(backgroundTimer);
                } else {
                    increaseTime(foregroundTimer);
                }

                handler.postDelayed(this,addedMillis); //send to handler's looper's MQ to rerun
                //the runnable object's run function
            }
        };

    }

    @Override
    protected void onResume() { //app becomes foreground process
        super.onResume();
        isInBackground = false;
        handler.removeCallbacks(timerThread); //remove previous callbacks from the MQ associated to the thread
        //else we will find ourselves with timers running too fast
        handler.postDelayed(timerThread,addedMillis); //send first call to the runnable object's run function
    }

    @Override
    protected void onPause() { //app becomes background process
        super.onPause();
        isInBackground = true;
        handler.removeCallbacks(timerThread); //remove previous callbacks from the MQ associated to the thread
        //else we will find ourselves with timers running too fast
        handler.postDelayed(timerThread,addedMillis); //send first call to the runnable object's run function
    }

    /**
     * increaseTime: function that increments by a specified time a timer, given as parameter
     * @param timer: a TextView showing to the user of the app the time elapsed
     * @note: to avoid continuously reinitializing, the SimpleDateFormat object used to turn
     * the TextView String into a time is declared as a class parameter.
     * @note: the time by which the timer will increase is declared outside of the function,
     * in the eventuality that the solution is implemented with threads, the thread must sleep
     * the same amount of time by which it increments the timer
     * */
    void increaseTime(@NonNull TextView timer){
        try{
            Date time = sdf.parse(timer.getText().toString());
            time.setTime(time.getTime()+addedMillis);
            timer.setText(sdf.format(time));
        }
        catch (ParseException ex){
            ex.printStackTrace();
        }

    }
}