package com.example.siciliamafia;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.siciliamafia.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.siciliamafia.StaticMethods.PLAYER_ID_KEY;
import static com.example.siciliamafia.StaticMethods.ROLE_ADMIN;
import static com.example.siciliamafia.StaticMethods.ROLE_HOST;
import static com.example.siciliamafia.StaticMethods.ROLE_PLAYER;
import static com.example.siciliamafia.StaticMethods.ROLE_PRESIDENT;
import static com.example.siciliamafia.StaticMethods.isInternetConnection;

public class PlayerActivity extends AppCompatActivity {

    private CircleImageView civProfilePhoto, civProfilePhotoCurrentPlayer;
    private TextView txtNickname, txtProfileCity, txtBalance, txtCountOfBalance, currentUsername, copyCard;
    private Switch swhSetHost, swhSetAdmin, swhSetPresident;
    private Button btnSetToZero, btnAdd, btnWithdraw;
    private FloatingActionButton fabPhone, fabPlayers, fabRules;
    private EditText editAddCount;
    private LinearLayout layoutPresident;
    private LinearLayout layoutHost;
    private Toolbar toolbar;
    private Intent intent;
    private String playerID;
    private String myID;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private User userPlayer;
    private User userCurrent;
    private int countOfBalance;
    private int changeBalance;
    private int resultBalance;

    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    StorageTask<UploadTask.TaskSnapshot> uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        setToolbar();
        init();
        changeData();
    }

    private void init() {
        civProfilePhoto = findViewById(R.id.profile_photo);
        civProfilePhotoCurrentPlayer = findViewById(R.id.current_profile_image);
        txtNickname = findViewById(R.id.nickname);
        txtProfileCity = findViewById(R.id.profile_city);
        txtBalance = findViewById(R.id.txt_balance);
        txtBalance.setText(getString(R.string.balance));
        txtCountOfBalance = findViewById(R.id.balance);
        swhSetHost = findViewById(R.id.sw_set_host);
        swhSetAdmin = findViewById(R.id.sw_set_admin);
        swhSetPresident = findViewById(R.id.sw_set_president);
        btnSetToZero = findViewById(R.id.button_set_to_zero);
        fabPhone = findViewById(R.id.fab_phone);
        fabPlayers = findViewById(R.id.fab_list_players);
        fabRules = findViewById(R.id.fab_rules);
        editAddCount = findViewById(R.id.et_add_count);
        btnAdd = findViewById(R.id.btn_add_to_balance);
        btnWithdraw = findViewById(R.id.btn_withdraw);
        layoutPresident = findViewById(R.id.linear_president);
        layoutHost = findViewById(R.id.linear_host);
        currentUsername = findViewById(R.id.username);
        copyCard = findViewById(R.id.copy_card_number);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

    }

    private void changeData() {

        intent = getIntent();
        playerID = intent.getStringExtra(PLAYER_ID_KEY);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        myID = firebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userCurrent = dataSnapshot.getValue(User.class);

                assert userCurrent != null;
                if (!firebaseUser.getUid().equals(playerID)) {
                    if (userCurrent.getImageURL().equals("default")) {
                        civProfilePhotoCurrentPlayer.setImageResource(R.drawable.logo);
                    } else {
                        Glide.with(getApplicationContext()).load(userCurrent.getImageURL()).into(civProfilePhotoCurrentPlayer);
                    }
                    currentUsername.setText(userCurrent.getUsername());
                    toolbar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(PlayerActivity.this, PlayerActivity.class);
                            intent.putExtra(PLAYER_ID_KEY, firebaseUser.getUid());
                            startActivity(intent);
                        }
                    });
                } else {
                    currentUsername.setText("Sicilia Mafia");
                    civProfilePhotoCurrentPlayer.setVisibility(View.GONE);

                }

                switch (userCurrent.getRole()) {
                    case ROLE_PLAYER:
                        layoutHost.setVisibility(View.GONE);
                        layoutPresident.setVisibility(View.GONE);
                        fabPlayers.hide();
                        fabPhone.hide();
                        swhSetHost.setChecked(false);
                        swhSetAdmin.setChecked(false);
                        swhSetPresident.setChecked(false);
                        break;
                    case ROLE_HOST:
                        layoutHost.setVisibility(View.VISIBLE);
                        layoutPresident.setVisibility(View.GONE);
                        btnAdd.setVisibility(View.GONE);
                        btnSetToZero.setVisibility(View.GONE);
                        swhSetHost.setChecked(true);
                        swhSetAdmin.setChecked(false);
                        swhSetPresident.setChecked(false);
                        fabPlayers.show();
                        if (firebaseUser.getUid().equals(playerID)) {
                            fabPhone.hide();
                        }
                        break;
                    case ROLE_ADMIN:
                        layoutPresident.setVisibility(View.GONE);
                        layoutHost.setVisibility(View.VISIBLE);
                        swhSetHost.setChecked(true);
                        swhSetAdmin.setChecked(true);
                        fabPlayers.show();
                        swhSetPresident.setChecked(false);
                        if (firebaseUser.getUid().equals(playerID)) {
                            fabPhone.hide();
                        }
                        break;
                    case ROLE_PRESIDENT:
                        layoutHost.setVisibility(View.VISIBLE);
                        layoutPresident.setVisibility(View.VISIBLE);
                        swhSetHost.setChecked(true);
                        swhSetAdmin.setChecked(true);
                        fabPlayers.show();
                        swhSetPresident.setChecked(true);
                        if (firebaseUser.getUid().equals(playerID)) {
                            fabPhone.hide();
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (!firebaseUser.getUid().equals(playerID)) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(playerID);
        } else {
            currentUsername.setText("Sicilia Mafia");
            civProfilePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isInternetConnection(getApplicationContext())) {
                        openImage();
                    } else
                        Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                }
            });
        }


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userPlayer = dataSnapshot.getValue(User.class);
                assert userPlayer != null;
                txtNickname.setText(userPlayer.getUsername());
                txtProfileCity.setText(userPlayer.getCity());
                txtCountOfBalance.setText(String.valueOf(userPlayer.getBalance()));
                changeBackgroundDependingOnTheBalance();
                if (userPlayer.getImageURL().equals("default")) {
                    civProfilePhoto.setImageResource(R.drawable.logo);
                } else {
                    Glide.with(getApplicationContext()).load(userPlayer.getImageURL()).into(civProfilePhoto);
                }

                switch (userPlayer.getRole()) {
                    case ROLE_PLAYER:
                        swhSetHost.setChecked(false);
                        swhSetAdmin.setChecked(false);
                        swhSetPresident.setChecked(false);
                        break;
                    case ROLE_HOST:
                        swhSetHost.setChecked(true);
                        swhSetAdmin.setChecked(false);
                        swhSetPresident.setChecked(false);
                        break;
                    case ROLE_ADMIN:
                        swhSetHost.setChecked(true);
                        swhSetAdmin.setChecked(true);
                        swhSetPresident.setChecked(false);
                        break;
                    case ROLE_PRESIDENT:
                        swhSetHost.setChecked(true);
                        swhSetAdmin.setChecked(true);
                        swhSetPresident.setChecked(true);
                        break;
                }


                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isInternetConnection(getApplicationContext())) {
                            if (!editAddCount.getText().toString().equals("")) {
                                countOfBalance = userPlayer.getBalance();
                                changeBalance = Integer.parseInt(editAddCount.getText().toString());
                                resultBalance = countOfBalance + changeBalance;
                                userPlayer.setBalance(resultBalance);
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("balance", resultBalance);
                                databaseReference.updateChildren(hashMap);
                                txtCountOfBalance.setText(String.valueOf(resultBalance));
                                editAddCount.setText("");
                                changeBackgroundDependingOnTheBalance();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.enter_correct_amount), Toast.LENGTH_SHORT).show();
                            }
                        } else
                            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                });

                btnWithdraw.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isInternetConnection(getApplicationContext())) {
                            if (!editAddCount.getText().toString().equals("")) {
                                countOfBalance = userPlayer.getBalance();
                                changeBalance = Integer.parseInt(editAddCount.getText().toString());
                                resultBalance = countOfBalance - changeBalance;
                                userPlayer.setBalance(resultBalance);
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("balance", resultBalance);
                                databaseReference.updateChildren(hashMap);
                                txtCountOfBalance.setText(String.valueOf(resultBalance));
                                editAddCount.setText("");
                                changeBackgroundDependingOnTheBalance();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.enter_correct_amount), Toast.LENGTH_SHORT).show();
                            }
                        } else
                            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                });

                btnSetToZero.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isInternetConnection(getApplicationContext())) {
                            userPlayer.setBalance(0);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("balance", 0);
                            databaseReference.updateChildren(hashMap);
                            txtCountOfBalance.setText("0");
                            txtCountOfBalance.setBackground(getDrawable(R.drawable.oval_balance_button_green));

                        } else
                            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                });


                swhSetHost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (isInternetConnection(getApplicationContext())) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            if (b) {
                                hashMap.put("role", ROLE_HOST);
                            } else {
                                hashMap.put("role", ROLE_PLAYER);


                            }
                            databaseReference.updateChildren(hashMap);
                        } else
                            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                    }
                });


                swhSetAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (isInternetConnection(getApplicationContext())) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            if (b) {
                                hashMap.put("role", ROLE_ADMIN);

                            } else {
                                hashMap.put("role", ROLE_HOST);

                                swhSetPresident.setChecked(false);
                            }
                            databaseReference.updateChildren(hashMap);
                        } else
                            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                    }
                });

                swhSetPresident.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (isInternetConnection(getApplicationContext())) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            if (b) {
                                hashMap.put("role", ROLE_PRESIDENT);

                            } else {
                                hashMap.put("role", ROLE_ADMIN);
                            }
                            databaseReference.updateChildren(hashMap);
                        } else
                            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                });

                fabPhone.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        if (isInternetConnection(getApplicationContext())) {
                            Uri call = Uri.parse("tel:" + userPlayer.getPhoneNumber());
                            Intent surf = new Intent(Intent.ACTION_CALL, call);
                            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            startActivity(surf);
                        } else
                            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                });

                fabPlayers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isInternetConnection(getApplicationContext())) {
                            Intent intent = new Intent(PlayerActivity.this, HostActivity.class);
                            startActivity(intent);
                            finish();
                        } else
                            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                    }
                });

                fabRules.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isInternetConnection(getApplicationContext())) {
                            Intent intent = new Intent(PlayerActivity.this, RulesActivity.class);
                            intent.putExtra(PLAYER_ID_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());
                            startActivity(intent);
                        } else
                            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                    }
                });

                copyCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("", "5168757353345970");
                        assert clipboard != null;
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), getString(R.string.card_number), Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DB", "ERROR");
            }
        });

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
                startActivity(new Intent(PlayerActivity.this, StartActivity.class));
                finish();
                return true;
        }

        return false;
    }

    private void changeBackgroundDependingOnTheBalance() {
        if (userPlayer.getBalance() >= 0) {
            txtCountOfBalance.setBackground(getDrawable(R.drawable.oval_balance_button_green));
        } else {
            txtCountOfBalance.setBackground(getDrawable(R.drawable.oval_balance_button_red));
        }
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {

    }

    private void openImage() {
        if (isInternetConnection(getApplicationContext())) {
            Intent intentStore = new Intent();
            intentStore.setType("image/*");
            intentStore.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intentStore, IMAGE_REQUEST);
        } else
            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.uploading));
        pd.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", "" + mUri);
                        databaseReference.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getApplicationContext(), getString(R.string.upload_in_preogress), Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }
}
