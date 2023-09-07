package com.example.smsreceiver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static MainActivity inst;
    ArrayList<String> smsList=new ArrayList<>();
    ListView smsListView;
    ArrayAdapter arrayAdapter;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;

    // Singleton pattern to maintain a single instance of MainActivity
    public static MainActivity instance(){
        return inst;
    }

    @Override
    protected void onStart() {
        super.onStart();
        inst=this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        smsListView=(ListView) findViewById(R.id.smsList);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsList);
        smsListView.setAdapter(arrayAdapter);
        smsListView.setOnItemClickListener(this);

        // Request READ_SMS permission if not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {   // If permission is granted, refresh the SMS inbox
            refreshSmsInbox();
        }

    }

    public void getPermissionToReadSMS() {   // Request permission to read SMS messages
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(
                        android.Manifest.permission.READ_SMS)) {
                    Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.READ_SMS},
                        READ_SMS_PERMISSIONS_REQUEST);
            }
        }
    }

    @Override  // Handle the result of permission requests
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show();
                refreshSmsInbox();
            } else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Refresh the SMS inbox by querying SMS messages
    public void refreshSmsInbox() {
        ContentResolver contentResolver=getContentResolver();
        Cursor smsInboxCursor=contentResolver.query(Uri.parse("content://sms/inbox"),null,null,null,null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");

        // Check if there are SMS messages and update the list
        if(indexBody<0||!smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        do{
            String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
            arrayAdapter.add(str);
        }while (smsInboxCursor.moveToNext());

    }

    // Update the list with a new SMS message
    public void updateList(final String smsMessage) {
        arrayAdapter.insert(smsMessage, 0);
        arrayAdapter.notifyDataSetChanged();
    }

    // Handle item click in the ListView
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
         try{
             // Parse and display the selected SMS message in the FullMessageActivity
             String[] smsMessages = smsList.get(pos).split("\n");
             String address = smsMessages[0];
             String smsMessage = "";
             for (int i = 1; i < smsMessages.length; ++i) {
                 smsMessage += smsMessages[i];
             }

             // Create an intent to start the FullMessageActivity
             Intent intent=new Intent(this,FullMessageActivity.class);
             intent.putExtra("address",address);
             intent.putExtra("message",smsMessage);
             startActivity(intent);
            // Toast.makeText(this, smsMessageStr, Toast.LENGTH_SHORT).show();
         }

         catch (Exception e){
             e.printStackTrace();
         }
    }
}