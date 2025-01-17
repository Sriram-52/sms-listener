package com.example.smslistener;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MessageBroadcastReceiver extends Service {
  BroadcastReceiver broadcastReceiver;

  public class MessageBroadcastReceiverImpl extends BroadcastReceiver {
    private String TAG = "MessageBroadcastReceiver";
    private final FirebaseFirestore db;

    public MessageBroadcastReceiverImpl() {
      db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
      Log.d(TAG, "Received SMS message");
      Bundle data = intent.getExtras();
      if (data != null) {
        try {
          Object[] pdus = (Object[]) data.get("pdus");
          SharedPreferences sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
          String receiver = sharedPref.getString("mobile_number", null);
          for (Object pdu : pdus) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            String sender = smsMessage.getDisplayOriginatingAddress();
            String messageBody = smsMessage.getMessageBody();
            long timestamp = smsMessage.getTimestampMillis();
            HashMap<String, Object> message = new HashMap<>();
            message.put("sender", sender);
            message.put("message_body", messageBody);
            message.put("timestamp", timestamp);
            message.put("receiver", receiver);
            Log.d(TAG, "SMS message: " + message);
            db.collection("messages")
              .add(message)
              .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                  @Override
                  public void onSuccess(DocumentReference documentReference) {
                      Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                  }
              })
              .addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      Log.w(TAG, "Error adding document", e);
                  }
              });
          }
        } catch (Exception e) {
          Log.e(TAG, "Error processing SMS message", e);
        }
      }
    }
  }

  @Override
  public void onCreate() {
    IntentFilter intentFilter = new IntentFilter();

    intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");

    broadcastReceiver = new MessageBroadcastReceiverImpl();
    registerReceiver(broadcastReceiver, intentFilter);
  }
}