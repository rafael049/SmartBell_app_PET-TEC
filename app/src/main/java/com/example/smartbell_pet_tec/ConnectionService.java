package com.example.smartbell_pet_tec;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.InputStream;
import java.net.Socket;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class ConnectionService  extends Service {

    Notification statusNotification;
    Notification alarmNotification;

    @Override
    public void onCreate() {
        statusNotification = new NotificationCompat.Builder(this, "status")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Smartbell esta rodando!")
                .setContentText("Assim que alguem apertar a campainha te avisaremos!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();

        alarmNotification = new NotificationCompat.Builder(this, "alarm")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("DING DONG!")
                .setContentText("Alguem está tocando sua campainha!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        // Get address and port of the ESP32 through the Intent
        final String esp32IP = intent.getStringExtra("esp32IP");
        final int esp32Port = intent.getIntExtra("esp32Port", 0);

        Toast.makeText(this, "Conexão com a campainha foi realizada com sucesso!", Toast.LENGTH_LONG).show();

        // Show status notification
        startForeground(1001, statusNotification);

        final ConnectionService thisContext = this;

        // Thread for the connection with the ESP32 server
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Connect to the ESP32 server
                    Socket socket = new Socket(esp32IP, esp32Port);

                    final int BUF_SIZE = 64;
                    InputStream in = socket.getInputStream();
                    final byte[] bufferData = new byte[BUF_SIZE];

                    // Wait for ESP32 send some msg
                    while(in.read(bufferData) != -1){
                        /*context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, new String(bufferData), Toast.LENGTH_SHORT).show();
                            }
                        });*/
                        // Show notification
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(thisContext);
                        notificationManager.notify(0, alarmNotification);
                    }
                }
                catch(Exception ex){
                    Log.i("info", "Erro ao se conectar ao server");
                    ex.printStackTrace();
                }

            }
        }).start();

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "O serviço da campainha parou!", Toast.LENGTH_LONG).show();
    }
}
