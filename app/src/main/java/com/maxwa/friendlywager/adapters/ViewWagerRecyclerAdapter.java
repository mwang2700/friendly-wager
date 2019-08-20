package com.maxwa.friendlywager.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maxwa.friendlywager.R;
import com.maxwa.friendlywager.models.ViewWager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class ViewWagerRecyclerAdapter extends RecyclerView.Adapter<ViewWagerRecyclerAdapter.ViewHolder> {

    private ArrayList<ViewWager> gamesList = new ArrayList<>();
    private OnItemClickListener listener;

    public ViewWagerRecyclerAdapter(ArrayList<ViewWager> list, OnItemClickListener onItemClickListener) {
        this.gamesList = list;
        this.listener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_view_list_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (gamesList.get(position).getTeam1().equals(gamesList.get(position).getSelectedTeam())) {
            String selectedText = "Selected: " + gamesList.get(position).getTeam1();
            holder.team1.setText(selectedText);
            holder.team2.setText(gamesList.get(position).getTeam2());
        } else {
            String selectedText = "Selected: " + gamesList.get(position).getTeam2();
            holder.team1.setText(gamesList.get(position).getTeam1());
            holder.team2.setText(selectedText);
        }
        String wagerText = "Wager: \n" + gamesList.get(position).getWagerAmount();
        holder.viewWagerAmount.setText(wagerText);
        String multText = "Multiplier: \n" + gamesList.get(position).getMultiplier();
        holder.viewMultiplier.setText(multText);
        long total = Math.round(gamesList.get(position).getWagerAmount() * gamesList.get(position).getMultiplier());
        String totalString = "Total: \n" + total;
        holder.total.setText(totalString);
        if (gamesList.get(position).getSport().equals("nba")) {
            holder.image.setImageResource(R.drawable.basketball);
        } else if (gamesList.get(position).getSport().equals("nfl")) {
            holder.image.setImageResource(R.drawable.football);
        } else {
            holder.image.setImageResource(R.drawable.baseball);
        }
        long commenceTime = gamesList.get(position).getCommenceTime();
        Date date = new Date(commenceTime*1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm a z");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String formattedTime = simpleDateFormat.format(date);
        holder.time.setText(formattedTime);
    }

    @Override
    public int getItemCount() {
        return gamesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView team1, team2, viewWagerAmount, viewMultiplier;
        private TextView time, total;
        private ImageView image;

        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            team1 = itemView.findViewById(R.id.viewTeam1);
            team2 = itemView.findViewById(R.id.viewTeam2);
            time = itemView.findViewById(R.id.viewTime);
            viewWagerAmount = itemView.findViewById(R.id.viewWagerAmount);
            viewMultiplier = itemView.findViewById(R.id.viewMultiplier);
            image = itemView.findViewById(R.id.sport_image);
            total = itemView.findViewById(R.id.viewTotal);
            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ViewWagerRecyclerAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
