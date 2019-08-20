package com.maxwa.friendlywager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maxwa.friendlywager.R;

import java.util.ArrayList;

public class GroupRequestRecyclerAdapter extends RecyclerView.Adapter<GroupRequestRecyclerAdapter.ViewHolder> {

    private ArrayList<String> groupsList = new ArrayList<>();
    private OnItemClickListener listener;

    public GroupRequestRecyclerAdapter(ArrayList<String> list, OnItemClickListener onItemClickListener) {
        this.groupsList = list;
        this.listener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_requests_list_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.groupname.setText(groupsList.get(position));
    }

    @Override
    public int getItemCount() {
        return groupsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView groupname;
        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            groupname = itemView.findViewById(R.id.request_groupname);

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
