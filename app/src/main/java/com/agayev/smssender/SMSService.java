package com.agayev.smssender;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class SMSService extends Service {
    private Handler handler = new Handler();
    private Runnable runnable;
    private long countdownTime = 24 * 60 * 60;
    private BroadcastReceiver smsReceiver;
    private static final int NOTIFICATION_ID = 1;

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SmsHandler handler = new SmsHandler();
                handler.handleSms(context, intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTimer();
        return START_STICKY;
    }

    private void startTimer() {
        runnable = new Runnable() {
            @Override
            public void run() {
                updateTimer();
                sendTimerUpdateBroadcast();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    private void updateTimer() {
        if (countdownTime > 0) {
            countdownTime--;
        }
    }

    private void sendTimerUpdateBroadcast() {
        Intent intent = new Intent("timer-update");
        intent.putExtra("countdownTime", countdownTime);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
    }

    private Notification buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id").setSmallIcon(R.drawable.ic_launcher_background).setContentTitle("SMSService").setContentText("Служба обработки SMS активирована.").setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder.build();
    }

    private void createNotificationChannel() {
        CharSequence name = "SMSServiceChannel";
        String description = "Канал уведомлений для SMSService";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
