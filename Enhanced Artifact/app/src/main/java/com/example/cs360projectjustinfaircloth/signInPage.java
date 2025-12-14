package com.example.cs360projectjustinfaircloth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class signInPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_page);

        // Handles sign up button onclick event
        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText username = findViewById(R.id.usernameInput);
                String usernameInput = username.getText().toString().trim();

                EditText password = findViewById(R.id.passwordInput);
                String passwordInput = password.getText().toString().trim();

                if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                    Toast.makeText(signInPage.this, "Please fill in both fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                usernameInput = usernameInput + "@myapp.com";

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(usernameInput, passwordInput)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = user.getUid();

                                getSharedPreferences("auth", MODE_PRIVATE).edit().putString("uid", uid).putString("username", username.getText().toString()).apply();

                                Intent i = new Intent(signInPage.this, SMSPermission.class);
                                i.putExtra("after", "MainActivity");
                                startActivity(i);

                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), "Account creation failed: " + task.getException().getMessage(), Toast.LENGTH_LONG);
                                toast.show();
                            }
                        });
            }
        });


        // Handles log in button onclick event
        Button logInButton = findViewById(R.id.logInButton);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText username = findViewById(R.id.usernameInput);
                String usernameInput = username.getText().toString().trim();

                EditText password = findViewById(R.id.passwordInput);
                String passwordInput = password.getText().toString().trim();

                if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                    Toast.makeText(signInPage.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Adds .com ending to for firebase auth
                usernameInput = usernameInput + "@myapp.com";

                FirebaseAuth.getInstance().signInWithEmailAndPassword(usernameInput, passwordInput)
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = user.getUid();

                                getSharedPreferences("auth", MODE_PRIVATE).edit().putString("uid", uid).putString("username", username.getText().toString()).apply();

                                Intent i = new Intent(signInPage.this, SMSPermission.class);
                                i.putExtra("after", "MainActivity");
                                startActivity(i);

                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), "Either username or password are incorrect. Please try again", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        });
            }
        });
    }
}