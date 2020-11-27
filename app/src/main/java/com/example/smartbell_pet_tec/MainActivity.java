package com.example.smartbell_pet_tec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.InputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    // Stuff for creating a notification
    private int notificationId;
    private Notification alarmNotification;

    // Intent for service
    private Intent connectServiceIntent;

    private String esp32IP;
    private int esp32Port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create Notification Channel
        createNotificationChannel("alarm");
        createNotificationChannel("status");

        // Define ESP32 IP and Port
        esp32IP = "192.168.0.101";
        esp32Port = 2525;

        // create Intent for service
        connectServiceIntent = new Intent(MainActivity.this, ConnectionService.class);
        connectServiceIntent.putExtra("esp32IP", esp32IP);
        connectServiceIntent.putExtra("esp32Port", esp32Port);
    }

    // Callback for the connection button
    public void onButtonClientCreate(View v){
        startService(connectServiceIntent);

    }

    private void createNotificationChannel(String id) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}