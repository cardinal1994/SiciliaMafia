package com.example.siciliamafia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.siciliamafia.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.siciliamafia.StaticMethods.PLAYER_ID_KEY;
import static com.example.siciliamafia.StaticMethods.isInternetConnection;

public class StartActivity extends AppCompatActivity {


    private Button login, register;

    private FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Intent intent = new Intent(StartActivity.this, PlayerActivity.class);
            intent.putExtra(PLAYER_ID_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        init();
    }



    private void init() {

        login = findViewById(R.id.activitystart__login);
        register = findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetConnection(getApplicationContext())) {
                    startActivity(new Intent(StartActivity.this, LoginActivity.class));
                } else
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetConnection(getApplicationContext())) {
                    startActivity(new Intent(StartActivity.this, RegisterActivity.class));
                } else
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

}