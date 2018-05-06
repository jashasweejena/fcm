package com.jashaswee.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jashaswee.fcm.app.Config;
import com.jashaswee.fcm.utils.NotificationUtils;

public class MainActivity extends AppCompatActivity {


    private final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReciever;
    private TextView txtRegId, txtMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtRegId = findViewById(R.id.txt_reg_id);
        txtMessage = findViewById(R.id.txt_push_message);
        String token = FirebaseInstanceId.getInstance().getToken();
        Toast.makeText(this, token, Toast.LENGTH_LONG).show();
        Log.d(TAG, "Mainactivity: " + token);

        mRegistrationBroadcastReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    //gcm successfully registered
                    //Now subscrive to 'global' topic to recieve app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    //New push notification is recieved

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), " Push Notification: " + message, Toast.LENGTH_LONG);

                    txtMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReciever);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Register gcm cloud messaging 'registration complete' reciever
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReciever,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        //register new push message reciever
        //By this, the activity will be notified each time a push message is recieved

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReciever,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        //Clear the notification area when app is opened
        NotificationUtils.clearNotifications(getApplicationContext());

    }


    //Fetches reg id from shared preferences
        //And displays on screem

        private void displayFirebaseRegId()
        {
            SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
            String regId = pref.getString("regId", null);

            Log.e(TAG, "Firebase Registration Id" + regId);

            if(!TextUtils.isEmpty(regId))
            {
                txtRegId.setText(regId);
        }
        else
            {
                txtRegId.setText("Firebase Reg id not recieved");
            }

    }
}
