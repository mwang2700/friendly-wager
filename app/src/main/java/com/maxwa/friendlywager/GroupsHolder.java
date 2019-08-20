package com.maxwa.friendlywager;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maxwa.friendlywager.R;

public class GroupsHolder extends RecyclerView.ViewHolder{

    public ImageView imageView;
    public TextView nameTextView;
    public TextView membersTextView;

    public GroupsHolder(@NonNull View itemView) {
        super(itemView);

        this.imageView = itemView.findViewById(R.id.imageView);
        this.nameTextView = itemView.findViewById(R.id.nameTextView);
    }
}
