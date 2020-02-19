package com.example.siciliamafia.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.siciliamafia.R;

import java.util.List;

import static com.example.siciliamafia.StaticMethods.listToString;

public class GameRecyclerAdapter extends RecyclerView.Adapter<GameRecyclerAdapter.PlayerViewHolder> {

    private Context context;
    private TextView tvVoting;
    private List<Integer> voting;

    public GameRecyclerAdapter(Context context, TextView tvVoting, List<Integer> voting) {
        this.context = context;
        this.tvVoting = tvVoting;
        this.voting = voting;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.game_item, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, final int position) {
        String numberOfPlayer = String.valueOf(position + 1);
        holder.playerNumber.setText(numberOfPlayer);
        holder.playerNumber.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                int votePlayer = position + 1;
                if (!voting.contains(votePlayer))
                    voting.add(votePlayer);
                tvVoting.setText(context.getString(R.string.voting).concat(" ").concat(listToString(voting)));
            }
        });
        tvVoting.setText(context.getString(R.string.voting));
    }


    @Override
    public int getItemCount() {
        return 10;
    }

    class PlayerViewHolder extends RecyclerView.ViewHolder {

        TextView playerNumber;
        CheckBox foul1, foul2, foul3, foul4;
        EditText nickname;


        PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerNumber = itemView.findViewById(R.id.tv_player_number);
            foul1 = itemView.findViewById(R.id.cb_foull);
            foul2 = itemView.findViewById(R.id.cb_foul2);
            foul3 = itemView.findViewById(R.id.cb_foul3);
            foul4 = itemView.findViewById(R.id.cb_foul4);
            nickname = itemView.findViewById(R.id.nickname);
        }
    }

}
