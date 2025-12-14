package com.example.cs360projectjustinfaircloth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class signInPage extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_page);

        db = new AppDatabase(getApplicationContext());

        // Handles sign up button onclick event
        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = findViewById(R.id.usernameInput);
                String usernameInput = username.getText().toString();

                EditText password = findViewById(R.id.passwordInput);
                String passwordInput = password.getText().toString();

                long addResult = db.addUser(usernameInput, passwordInput);
                if(addResult == -1) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Account already exists!", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    int userId = db.getId(usernameInput);
                    getSharedPreferences("auth", MODE_PRIVATE).edit().putInt("user_id", userId).putString("username", usernameInput).apply();

                    startActivity(new Intent(signInPage.this, SMSPermission.class));
                };
            }
        });


        // Handles log in button onclick event
        Button logInButton = findViewById(R.id.logInButton);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText username = findViewById(R.id.usernameInput);
                String usernameInput = username.getText().toString();

                EditText password = findViewById(R.id.passwordInput);
                String passwordInput = password.getText().toString();

                if (db.validateLogIn(usernameInput, passwordInput)) {
                    int userId = db.getId(usernameInput);
                    getSharedPreferences("auth", MODE_PRIVATE).edit().putInt("user_id", userId).putString("username", usernameInput).apply();

                    startActivity(new Intent(signInPage.this, SMSPermission.class));
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Either username or password are incorrect. Please try again", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }
}
