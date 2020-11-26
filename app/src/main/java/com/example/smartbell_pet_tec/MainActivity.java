package com.example.smartbell_pet_tec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
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
    private static final String CHANNEL_ID = "teste";
    private int notificationId;
    private NotificationCompat.Builder builder;

    private String esp32IP;
    private int esp32Port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create Notification Channel
        createNotificationChannel();
        // Create Notification
        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background) // Icon
                .setContentTitle("Notificação teste") // Title
                .setContentText("Texto da notificação") // Content
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); // Priority

        // Define ESP32 IP and Port
        esp32IP = "192.168.0.102";
        esp32Port = 2525;
    }

    // Callback for the connection button
    public void onButtonClientCreate(View v){

        final MainActivity mainActivity = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Connect to the ESP32 server
                    Socket socket = new Socket(mainActivity.esp32IP, mainActivity.esp32Port);

                    final int BUF_SIZE = 64;
                    InputStream in = socket.getInputStream();
                    final byte[] bufferData = new byte[BUF_SIZE];

                    // Wait for ESP32 send some msg
                    while(in.read(bufferData) != -1){
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mainActivity, new String(bufferData), Toast.LENGTH_SHORT).show();
                            }
                        });
                        // Show notification
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mainActivity);
                        notificationManager.notify(0, builder.build());
                    }
                }
                catch(Exception ex){
                    Log.i("info", "Erro ao se conectar ao server");
                    ex.printStackTrace();
                }

            }
        }).start();

    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}