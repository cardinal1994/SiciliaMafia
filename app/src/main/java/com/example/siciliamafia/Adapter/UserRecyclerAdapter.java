package com.example.siciliamafia.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.siciliamafia.Fragments.PlayersFragment;
import com.example.siciliamafia.Model.User;
import com.example.siciliamafia.PlayerActivity;
import com.example.siciliamafia.R;
import com.example.siciliamafia.StaticMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.siciliamafia.StaticMethods.PLAYER_ID_KEY;

import java.util.HashMap;
import java.util.List;

import static com.example.siciliamafia.StaticMethods.*;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.UserViewHolder> {

    private Context context;
    private List<User> users;
    private String currentUserRole;
    private EditText searchUsers;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    String searchString;


    public UserRecyclerAdapter(Context context, List<User> users, String currentUserRole, EditText searchUsers, String searchString) {
        this.context = context;
        this.users = users;
        this.currentUserRole = currentUserRole;
        this.searchUsers = searchUsers;
        this.searchString = searchString;

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);

        return new UserRecyclerAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, final int position) {

        final User user = users.get(position);
        holder.username.setText(user.getUsername());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getId());
        databaseReference.addValueEventListener(new ValueEventListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                final User databaseUser = dataSnapshot.getValue(User.class);
                assert databaseUser != null;
                holder.balance.setText(context.getString(R.string.balance) + databaseUser.getBalance());

                if (currentUserRole.equals(ROLE_PLAYER) || currentUserRole.equals(ROLE_HOST)) {
                    holder.btnSetToZero.setVisibility(View.GONE);
                } else {
                    holder.btnSetToZero.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("balance", 0);
                            dataSnapshot.getRef().updateChildren(hashMap);
                            if (!searchUsers.getText().toString().equals("")) {
                                searchString = "";
                                searchUsers.setText("");
                            }

                        }
                    });

                }

                holder.btnMinus15.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("balance", databaseUser.getBalance() - 15);
                        dataSnapshot.getRef().updateChildren(hashMap);
                        if (!searchUsers.getText().toString().equals("")) {
                            searchString = "";
                            searchUsers.setText("");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (user.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.drawable.logo);
        } else {
            Glide.with(context).load(user.getImageURL()).into(holder.profile_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetConnection(context)) {
                    Intent intent = new Intent(context, PlayerActivity.class);
                    intent.putExtra(PLAYER_ID_KEY, user.getId());
                    context.startActivity(intent);
                } else
                    Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        TextView balance;
        ImageView profile_image;
        Button btnMinus15;
        Button btnSetToZero;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            balance = itemView.findViewById(R.id.balance);
            profile_image = itemView.findViewById(R.id.profile_image);
            btnMinus15 = itemView.findViewById(R.id.btn_minus_15);
            btnSetToZero = itemView.findViewById(R.id.btn_set_to_zero);
        }
    }
}