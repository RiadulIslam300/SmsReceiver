package com.example.smsreceiver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

public class FullMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_message);

        // Retrieve SMS message content and sender's address from intent extras
        String smsMessage=getIntent().getStringExtra("message");
        String smsAddress=getIntent().getStringExtra("address");

        // Find and set the SMS message content in a TextView
        TextView fullMessageTextView = findViewById(R.id.fullMessageTextView);
        fullMessageTextView.setText(smsMessage);

        // Set up the Toolbar with the sender's address as the title
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(smsAddress);

        // Handle back button click to navigate back to the MainActivity
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}