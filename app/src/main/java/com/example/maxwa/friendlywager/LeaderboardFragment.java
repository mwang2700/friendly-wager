package com.example.maxwa.friendlywager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.maxwa.friendlywager.adapters.LeaderboardRecyclerAdapter;
import com.example.maxwa.friendlywager.models.Member;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class LeaderboardFragment extends Fragment {

    private RecyclerView leaderboardList;
    private LeaderboardRecyclerAdapter leaderboardAdapter;

    ArrayList<Member> membersList = new ArrayList<>();

    String groupName;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    public static LeaderboardFragment newInstance() {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        leaderboardList = view.findViewById(R.id.leaderboardList);
        groupName = getActivity().getIntent().getStringExtra("groupName");

        initializeRecycler();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(groupName);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateLeaderboard();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateLeaderboard();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                updateLeaderboard();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateLeaderboard();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        leaderboardList.setHasFixedSize(true);
        leaderboardList.setLayoutManager(linearLayoutManager);
        leaderboardAdapter = new LeaderboardRecyclerAdapter(membersList);
        leaderboardList.setAdapter(leaderboardAdapter);
    }

    private void updateLeaderboard() {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child(groupName);
        groupRef.orderByChild("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                membersList = new ArrayList<>();
                int img;
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    img = R.drawable.ic_person_outline_black_24dp;
                    String username = data.getKey();
                    long points = (long) data.child("points").getValue();
                    Member member = new Member(img, username, points);
                    membersList.add(member);
                }
                Collections.reverse(membersList);
                if (membersList.size() > 0) {
                    membersList.get(0).setImg(R.drawable.first_place);
                    if (membersList.size() > 1) {
                        membersList.get(1).setImg(R.drawable.second_place);
                        if (membersList.size() > 2) {
                            membersList.get(2).setImg(R.drawable.third_place);
                        }
                    }
                }
                leaderboardAdapter = new LeaderboardRecyclerAdapter(membersList);
                leaderboardList.setAdapter(leaderboardAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to get leaderboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
