package com.example.siciliamafia;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.siciliamafia.Adapter.RulesRecyclerAdapter;
import com.example.siciliamafia.Model.Rule;
import com.example.siciliamafia.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.siciliamafia.StaticMethods.PLAYER_ID_KEY;
import static com.example.siciliamafia.StaticMethods.ROLE_PRESIDENT;
import static com.example.siciliamafia.StaticMethods.isInternetConnection;

public class RulesActivity extends AppCompatActivity {

    private CircleImageView civPhoto;
    private Toolbar toolbar;
    private TextView nickname;
    private EditText textHeading, textSubtext;
    private ImageButton btnSend;
    private List<Rule> rules;
    private RelativeLayout linearBottom;


    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    User currentUser;

    RecyclerView recyclerView;
    RulesRecyclerAdapter rulesrecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        setToolbar();
        init();
        changeData();
    }

    private void changeData() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                assert currentUser != null;
                if (currentUser.getImageURL().equals("default")) {
                    civPhoto.setImageResource(R.drawable.logo);
                } else {
                    Glide.with(getApplicationContext()).load(currentUser.getImageURL()).into(civPhoto);
                }
                nickname.setText(currentUser.getUsername());

                toolbar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(RulesActivity.this, PlayerActivity.class);
                        intent.putExtra(PLAYER_ID_KEY, firebaseUser.getUid());
                        startActivity(intent);
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Rules");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rules.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Rule rule = snapshot.getValue(Rule.class);
                    rules.add(rule);

                }
                rulesrecyclerAdapter = new RulesRecyclerAdapter(getApplicationContext(), rules);
                recyclerView.setAdapter(rulesrecyclerAdapter);


                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isInternetConnection(getApplicationContext())) {
                            String strHeading = textHeading.getText().toString();
                            String strSubtext = textSubtext.getText().toString();
                            String generatedID = UUID.randomUUID().toString();
                            Map<String, String> hashmap = new HashMap<>();
                            if (!strHeading.equals("") && !strSubtext.equals("")) {
                                hashmap.put("heading", strHeading);
                                hashmap.put("subtext", strSubtext);
                                hashmap.put("id", generatedID);
                                databaseReference = FirebaseDatabase.getInstance().getReference("Rules").child(generatedID);
                                databaseReference.setValue(hashmap);
                                textHeading.setText("");
                                textSubtext.setText("");
                            } else
                                Toast.makeText(getApplicationContext(), getString(R.string.both_fields), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                        }

                    }
                });

                if (currentUser.getRole().equals(ROLE_PRESIDENT)) {
                    linearBottom.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void init() {
        civPhoto = findViewById(R.id.current_profile_image);
        nickname = findViewById(R.id.username);
        textHeading = findViewById(R.id.edittext_text);
        textSubtext = findViewById(R.id.edittext_subtext);
        btnSend = findViewById(R.id.btn_send);
        linearBottom = findViewById(R.id.bottom);
        rules = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_rules);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar_rules);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(RulesActivity.this, StartActivity.class));
                finish();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return false;
    }


}
