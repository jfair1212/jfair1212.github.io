package com.example.cs360projectjustinfaircloth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class SMSPermission extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_permission_request);

        Button denyButton = findViewById(R.id.denyPermissionButton);
        Button acceptButton = findViewById(R.id.acceptPermissionButton);

        // If deny button is pressed, denial toast pops up
        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SMSPermission.this, "Notification permission denied", Toast.LENGTH_SHORT).show();
                String after = getIntent().getStringExtra("after");
                if ("MainActivity".equals(after)) {
                    startActivity(new Intent(SMSPermission.this, MainActivity.class));
                }
                finish();
            }
        });

        // If accept has already been hit, toast shows. Else, request permission
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        SMSPermission.this,
                        Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            SMSPermission.this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                            PERMISSION_REQUEST_CODE
                    );
                } else {
                    Toast.makeText(SMSPermission.this, "Notifications already allowed", Toast.LENGTH_SHORT).show();
                    String after = getIntent().getStringExtra("after");
                    if ("MainActivity".equals(after)) {
                        startActivity(new Intent(SMSPermission.this, MainActivity.class));
                    }
                    finish();
                }
            }
        });
    }

    // Handles result of user's decision to accept or deny notifications
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(SMSPermission.this, "You have enabled notifications", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SMSPermission.this, "You have denied notifications", Toast.LENGTH_SHORT).show();
            }

            String after = getIntent().getStringExtra("after");
            if ("MainActivity".equals(after)) {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
        }
    }
}
