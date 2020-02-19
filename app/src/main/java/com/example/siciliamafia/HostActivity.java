package com.example.siciliamafia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.siciliamafia.Fragments.FunGameFragment;
import com.example.siciliamafia.Fragments.PlayersFragment;
import com.example.siciliamafia.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.siciliamafia.StaticMethods.PLAYER_ID_KEY;
import static com.example.siciliamafia.StaticMethods.isInternetConnection;

public class HostActivity extends AppCompatActivity {

    private CircleImageView profile_image;
    private TextView username;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        setToolbar();
        init();
        if (isInternetConnection(getApplicationContext())) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            setEventListener();
            setTabs();
        } else
            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
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
                if (isInternetConnection(getApplicationContext())) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(HostActivity.this, StartActivity.class));
                    finish();
                } else
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                return true;
        }

        return false;
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HostActivity.this, PlayerActivity.class);
                intent.putExtra(PLAYER_ID_KEY, firebaseUser.getUid());
                startActivity(intent);
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    private void init() {
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }

    private void setEventListener() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.drawable.logo);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setTabs() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new PlayersFragment(), getString(R.string.players));
        viewPagerAdapter.addFragment(new FunGameFragment(), getString(R.string.fun_game));

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;
        private List<String> titles;

        ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }
    }

    @Override
    public void onBackPressed() {

    }
}
