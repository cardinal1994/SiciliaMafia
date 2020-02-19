package com.example.siciliamafia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
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

import static com.example.siciliamafia.StaticMethods.PLAYER_ID_KEY;
import static com.example.siciliamafia.StaticMethods.isInternetConnection;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button btn_login;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.login_tv);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

        init();
    }

    private void init() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetConnection(getApplicationContext())) {
                    String txt_email = email.getText().toString();
                    String txt_password = password.getText().toString();

                    if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                        Toast.makeText(LoginActivity.this, R.string.all_fields_are_required, Toast.LENGTH_SHORT).show();
                    } else {
                        auth.signInWithEmailAndPassword(txt_email, txt_password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(LoginActivity.this, PlayerActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra(PLAYER_ID_KEY, auth.getCurrentUser().getUid());
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
