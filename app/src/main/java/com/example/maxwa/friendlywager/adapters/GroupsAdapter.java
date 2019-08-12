package com.example.maxwa.friendlywager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maxwa.friendlywager.models.GroupsModel;
import com.example.maxwa.friendlywager.R;

import java.util.ArrayList;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder> {

    ArrayList<String> groups;
    private OnItemClickListener listener;

    public GroupsAdapter(ArrayList<String> groups, OnItemClickListener onItemClickListener) {
        this.groups = groups;
        this.listener = onItemClickListener;
    }

    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_groups_list_item, null);

        return new GroupsViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsViewHolder holder, int position) {
        holder.nameTextView.setText(groups.get(position));
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class GroupsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nameTextView;

        OnItemClickListener onItemClickListener;

        public GroupsViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            this.nameTextView = itemView.findViewById(R.id.nameTextView);

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

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
