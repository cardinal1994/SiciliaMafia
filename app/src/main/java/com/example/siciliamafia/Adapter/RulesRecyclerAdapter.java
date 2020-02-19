package com.example.siciliamafia.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.siciliamafia.Model.Rule;
import com.example.siciliamafia.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RulesRecyclerAdapter extends RecyclerView.Adapter<RulesRecyclerAdapter.RulesViewHolder> {

    private Context context;
    private List<Rule> rules;
    private DatabaseReference reference;
    boolean isOpened = false;

    public RulesRecyclerAdapter(Context context, List<Rule> rules) {
        this.context = context;
        this.rules = rules;
    }


    @NonNull
    @Override
    public RulesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rules_item, parent, false);
        return new RulesViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final RulesViewHolder holder, int position) {
        final Rule rule = rules.get(position);


        holder.tvSubtext.setVisibility(View.GONE);
        holder.btnToggleText.setBackgroundResource(R.drawable.ic_toggle_closed_24dp);

        reference = FirebaseDatabase.getInstance().getReference("Rules");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                holder.tvHeading.setText(rule.getHeading());
                holder.tvSubtext.setText(rule.getSubtext());

                holder.relativeRule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isOpened) {
                            holder.tvSubtext.setVisibility(View.GONE);
                            holder.btnToggleText.setBackgroundResource(R.drawable.ic_toggle_closed_24dp);
                            isOpened = false;
                        } else {
                            holder.tvSubtext.setVisibility(View.VISIBLE);
                            holder.btnToggleText.setBackgroundResource(R.drawable.ic_toggle_opened_24dp);
                            isOpened = true;
                        }
                    }
                });

                holder.btnToggleText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isOpened) {
                            holder.tvSubtext.setVisibility(View.GONE);
                            holder.btnToggleText.setBackgroundResource(R.drawable.ic_toggle_closed_24dp);
                            isOpened = false;
                        } else {
                            holder.tvSubtext.setVisibility(View.VISIBLE);
                            holder.btnToggleText.setBackgroundResource(R.drawable.ic_toggle_opened_24dp);
                            isOpened = true;
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.tvSubtext.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String ruleID = rule.getId();
                FirebaseDatabase.getInstance().getReference()
                        .child("Rules").child(ruleID).removeValue();
                return true;
            }
        });


    }


    @Override
    public int getItemCount() {
        return rules.size();
    }

    class RulesViewHolder extends RecyclerView.ViewHolder {

        TextView tvHeading, tvSubtext;
        ImageButton btnToggleText;
        RelativeLayout relativeRule;


        RulesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeading = itemView.findViewById(R.id.text_heading);
            tvSubtext = itemView.findViewById(R.id.text_subtext);
            btnToggleText = itemView.findViewById(R.id.btn_show_text);
            relativeRule = itemView.findViewById(R.id.relative_item);
        }
    }
}
