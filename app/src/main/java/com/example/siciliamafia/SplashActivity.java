package com.example.siciliamafia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class SplashActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        getConnection();

    }

    private void getConnection() {
        if (isInternetConnection(getApplicationContext())) {
            getData();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    getConnection();
                }
            }, 4000);

        }
    }

    private void getData() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {

            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Intent intent = new Intent(SplashActivity.this, PlayerActivity.class);
                    intent.putExtra(PLAYER_ID_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Intent intent = new Intent(SplashActivity.this, StartActivity.class);
            startActivity(intent);
        }
    }
}
