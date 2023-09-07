package com.example.smsreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    public static final String SMS_BUNDLE="pdus";
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras=intent.getExtras();

        if(intentExtras!=null){
            // Extract SMS messages from the received broadcast
            Object[] sms=(Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr="";
            for(int i=0;i<sms.length;++i){
                SmsMessage smsMessage=SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody=smsMessage.getMessageBody().toString();
                String smsAdress=smsMessage.getOriginatingAddress();
                smsMessageStr+="SMS FROM "+smsAdress+"\n";
                smsMessageStr+=smsBody+"\n";
            }
            // Display a toast notification for the received SMS
            Toast.makeText(context,smsMessageStr,Toast.LENGTH_LONG).show();

            // Update the MainActivity's SMS list with the received message
            MainActivity inst=MainActivity.instance();
            inst.updateList(smsMessageStr);
        }

    }
}
