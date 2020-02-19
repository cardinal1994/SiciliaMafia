package com.example.siciliamafia.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.siciliamafia.Adapter.GameRecyclerAdapter;
import com.example.siciliamafia.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class FunGameFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView clock, voting;
    private Button btn_clear;
    private List<Integer> votingList;
    private GameRecyclerAdapter gameRecyclerAdapter;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fun_game, container, false);

        voting = view.findViewById(R.id.tv_voting);
        btn_clear = view.findViewById(R.id.btn_clear);
        clock = view.findViewById(R.id.tv_clock);

        setChronometer();
        votingList = new ArrayList<>();
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                votingList.clear();
                gameRecyclerAdapter.notifyDataSetChanged();

            }
        });

        recyclerView = view.findViewById(R.id.recycler_view_game);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        gameRecyclerAdapter = new GameRecyclerAdapter(getContext(), voting, votingList);
        recyclerView.setAdapter(gameRecyclerAdapter);
        return view;
    }

    private void setChronometer() {
        clock.setClickable(true);
        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
            }
        });

    }

    private Runnable updateTimerThread = new Runnable() {
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;

            clock.setText("" + mins + ":" + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        customHandler.removeCallbacks(updateTimerThread);
    }
}
