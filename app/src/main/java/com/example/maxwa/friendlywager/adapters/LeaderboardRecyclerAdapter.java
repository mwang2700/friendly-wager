package com.example.maxwa.friendlywager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maxwa.friendlywager.R;
import com.example.maxwa.friendlywager.models.Friends;
import com.example.maxwa.friendlywager.models.Member;

import java.util.ArrayList;

public class LeaderboardRecyclerAdapter extends RecyclerView.Adapter<LeaderboardRecyclerAdapter.ViewHolder> {

    private ArrayList<Member> membersList = new ArrayList<>();

    public LeaderboardRecyclerAdapter(ArrayList<Member> list) {
        this.membersList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_leaderboard_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = membersList.get(position).getUsername() + ": " + Long.toString(membersList.get(position).getPoints()) + " points";
        holder.username.setText(text);
        holder.image.setImageResource(membersList.get(position).getImg());
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.friend_username);
            image = itemView.findViewById(R.id.placement_image);
        }
    }
}
