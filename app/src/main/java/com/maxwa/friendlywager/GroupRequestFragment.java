package com.maxwa.friendlywager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maxwa.friendlywager.adapters.GroupRequestRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupRequestFragment extends Fragment implements GroupRequestRecyclerAdapter.OnItemClickListener{

    private RecyclerView groupRequestList;
    private GroupRequestRecyclerAdapter groupRecyclerAdapter;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ArrayList<String> groupRequests = new ArrayList<>();

    String username;
    private String email;

    public GroupRequestFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static GroupRequestFragment newInstance() {
        GroupRequestFragment fragment = new GroupRequestFragment();
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
        return inflater.inflate(R.layout.fragment_group_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupRequestList = view.findViewById(R.id.groupRequestList);
        initializeRecycler();

        DatabaseReference usernameRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());
        usernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = (String) dataSnapshot.child("username").getValue();
                email = (String) dataSnapshot.child("email").getValue();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("grouprequests").child(username);
                ref.addChildEventListener(new ChildEventListener() {
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateList() {
        groupRequests = new ArrayList<>();
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("grouprequests").child(username);
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot subData: dataSnapshot.getChildren()) {
                    String groupRequestName = subData.getKey();
                    groupRequests.add(groupRequestName);
                }
                groupRecyclerAdapter = new GroupRequestRecyclerAdapter(groupRequests, GroupRequestFragment.this);
                groupRequestList.setAdapter(groupRecyclerAdapter);
                groupRecyclerAdapter.setOnItemClickListener(GroupRequestFragment.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        groupRequestList.setHasFixedSize(true);
        groupRequestList.setLayoutManager(linearLayoutManager);
        groupRecyclerAdapter = new GroupRequestRecyclerAdapter(groupRequests, this);
        groupRequestList.setAdapter(groupRecyclerAdapter);
    }

    @Override
    public void onItemClick(int position) {
        final String groupToJoin = groupRequests.get(position);

        new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_addbell)
                .setTitle("Confirm Group Request")
                .setMessage("Would you like to join " + groupToJoin + "?")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("grouprequests")
                                .child(username).child(groupToJoin).removeValue();
                        FirebaseDatabase.getInstance().getReference().child(groupToJoin)
                                .child(username).child("points").setValue(100);
                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupToJoin)
                                .child(mAuth.getUid()).child("email").setValue(email);
                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupToJoin)
                                .child(mAuth.getUid()).child("username").setValue(username);
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("grouprequests")
                                .child(username).child(groupToJoin).removeValue();
                    }
                })
                .show();
    }
}
