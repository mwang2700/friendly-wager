package com.maxwa.friendlywager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maxwa.friendlywager.adapters.GroupsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupsFragment extends Fragment implements View.OnClickListener, GroupsAdapter.OnItemClickListener {

    private GroupsAdapter groupsAdapter;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    Button addGroupButton;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    String groupName = "";
    ArrayList<String> allGroups = new ArrayList<>();

    boolean checkAboveFour = true;

    public GroupsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GroupsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupsFragment newInstance() {
        GroupsFragment fragment = new GroupsFragment();
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
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState) ;

        addGroupButton = view.findViewById(R.id.addGroupButton);
        addGroupButton.setOnClickListener(this);
        recyclerView = view.findViewById(R.id.groupsRecycler);

        initializeRecycler();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("groups");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateList();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateList();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                updateList();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        groupsAdapter = new GroupsAdapter(allGroups, this);
        recyclerView.setAdapter(groupsAdapter);
    }

    private void updateList() {
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference().child("groups");
        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allGroups = new ArrayList<>();
                for (DataSnapshot subData: dataSnapshot.getChildren()) {
                    String groupName = subData.getKey();
                    for (DataSnapshot uidSnapshots: subData.getChildren()) {
                        if (uidSnapshots.getKey().equals(mAuth.getUid())) {
                            allGroups.add(groupName);
                        }
                    }
                }
                groupsAdapter = new GroupsAdapter(allGroups,GroupsFragment.this);
                recyclerView.setAdapter(groupsAdapter);
                if (allGroups.size() >= 4 && checkAboveFour) {
                    addGroupButton.setClickable(false);
                    checkAboveFour = false;
                } else if (!checkAboveFour && allGroups.size() < 4) {
                    addGroupButton.setClickable(true);
                    checkAboveFour = true;
                }
                groupsAdapter.setOnItemClickListener(GroupsFragment.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), NewGroupActivity.class);
        intent.putStringArrayListExtra("existingGroups", allGroups);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), GroupDashboardActivity.class);
        groupName = allGroups.get(position);
        intent.putExtra("groupName", groupName);
        startActivity(intent);
    }
}
