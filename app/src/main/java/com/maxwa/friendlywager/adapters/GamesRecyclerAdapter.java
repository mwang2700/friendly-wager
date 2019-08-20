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
import com.maxwa.friendlywager.models.Game;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class GamesRecyclerAdapter extends RecyclerView.Adapter<GamesRecyclerAdapter.ViewHolder> {

    private ArrayList<Game> gamesList = new ArrayList<>();
    private OnItemClickListener listener;

    public GamesRecyclerAdapter(ArrayList<Game> list, OnItemClickListener onItemClickListener) {
        this.gamesList = list;
        this.listener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bets_list_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.team1.setText(gamesList.get(position).getTeam1());
        holder.team2.setText(gamesList.get(position).getTeam2());
        holder.team1multiplier.setText(Double.toString(gamesList.get(position).getMultiplier1()));
        holder.team2multiplier.setText(Double.toString(gamesList.get(position).getMultiplier2()));
        if (gamesList.get(position).getSport().equals("nba")) {
            holder.image.setImageResource(R.drawable.basketball);
        } else if (gamesList.get(position).getSport().equals("nfl")) {
            holder.image.setImageResource(R.drawable.football);
        } else {
            holder.image.setImageResource(R.drawable.baseball);
        }
        long commenceTime = gamesList.get(position).getCommenceTime();
        Date date = new java.util.Date(commenceTime*1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("MM-dd-yyyy hh:mm a z");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String formattedTime = simpleDateFormat.format(date);
        holder.time.setText(formattedTime);
    }

    @Override
    public int getItemCount() {
        return gamesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView team1, team2, team1multiplier, team2multiplier;
        private TextView time;
        private ImageView image;

        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            team1 = itemView.findViewById(R.id.team1);
            team2 = itemView.findViewById(R.id.team2);
            time = itemView.findViewById(R.id.time);
            team1multiplier = itemView.findViewById(R.id.team1multiplier);
            team2multiplier = itemView.findViewById(R.id.team2multiplier);
            image = itemView.findViewById(R.id.sport_image);
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

    public void setOnItemClickListener(GamesRecyclerAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
