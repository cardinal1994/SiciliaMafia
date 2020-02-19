package com.example.siciliamafia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.example.siciliamafia.StaticMethods.PLAYER_ID_KEY;
import static com.example.siciliamafia.StaticMethods.isInternetConnection;

public class RegisterActivity extends AppCompatActivity {

    private EditText username, phoneNumber, email, password, city;
    private Button btnRegister;

    private FirebaseAuth auth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.bar_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!isInternetConnection(getApplicationContext())) Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        init();
        auth = FirebaseAuth.getInstance();
    }

    private void register(final String username, final String phoneNumber, final String email, final String password, final String city) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userId = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            Map<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("phoneNumber", phoneNumber);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");
                            hashMap.put("search", username.toLowerCase());
                            hashMap.put("city", city);
                            hashMap.put("role", "Player");
                            hashMap.put("balance", 0);
                            hashMap.put("email", email);
                            hashMap.put("password", password);

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(RegisterActivity.this, PlayerActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra(PLAYER_ID_KEY, auth.getCurrentUser().getUid());
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, R.string.you_cant_register, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void init() {
        username = findViewById(R.id.username);
        phoneNumber = findViewById(R.id.usernumber);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        city = findViewById(R.id.city);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetConnection(getApplicationContext())) {
                    String txt_username = username.getText().toString();
                    String txt_number = phoneNumber.getText().toString();
                    String txt_email = email.getText().toString();
                    String txt_password = password.getText().toString();
                    String txt_city = city.getText().toString();

                    if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_number) || TextUtils.isEmpty(txt_email)
                            || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_city)) {
                        Toast.makeText(RegisterActivity.this, R.string.all_fields_are_required, Toast.LENGTH_SHORT).show();
                    } else if (txt_password.length() < 6) {
                        Toast.makeText(RegisterActivity.this, R.string.password_must_be, Toast.LENGTH_SHORT).show();
                    } else {
                        register(txt_username, txt_number, txt_email, txt_password, txt_city);
                    }
                } else
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }


}