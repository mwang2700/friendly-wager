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

import com.maxwa.friendlywager.adapters.GroupsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AddMembersFragment extends Fragment implements GroupsAdapter.OnItemClickListener {

    private RecyclerView friendRequestList;
    private GroupsAdapter groupsAdapter;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ArrayList<String> userList = new ArrayList<>();
    Set<String> noDupe = new HashSet<>(userList);
    DatabaseReference databaseReference;

    String username;
    String friendUser;
    String groupName;

    public AddMembersFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AddMembersFragment newInstance() {
        AddMembersFragment fragment = new AddMembersFragment();
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
        return inflater.inflate(R.layout.fragment_add_members, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        friendRequestList = view.findViewById(R.id.friendRequestList);

        groupName = getActivity().getIntent().getStringExtra("groupName");
        DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());
        final DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference().child("grouprequests");
        final DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference().child("groups").child(groupName);

        initializeRecycler();

        DatabaseReference usernameRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());
        usernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = (String) dataSnapshot.child("username").getValue();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("friends").child(username);
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
                DatabaseReference groupMembersRef = FirebaseDatabase.getInstance().getReference().child("groupsrequests");
                groupMembersRef.addChildEventListener(new ChildEventListener() {
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
        userList = new ArrayList<>();
        noDupe.clear();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("friends").child(username);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot subData: dataSnapshot.getChildren()) {
                    friendUser = (String) subData.child("username").getValue();
                    noDupe.add(friendUser);
                }
                userList.clear();
                userList.addAll(noDupe);
                clearExisting();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void clearExisting() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(groupName);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (String friend: userList) {
                    for (DataSnapshot userSnapshots: dataSnapshot.getChildren()) {
                        String existingUser = userSnapshots.getKey();
                        if (friend.equals(existingUser)) {
                            noDupe.remove(friend);
                        }
                    }
                }
                userList.clear();
                userList.addAll(noDupe);
                groupsAdapter = new GroupsAdapter(userList, AddMembersFragment.this);
                friendRequestList.setAdapter(groupsAdapter);
                groupsAdapter.setOnItemClickListener(AddMembersFragment.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initializeRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        friendRequestList.setHasFixedSize(true);
        friendRequestList.setLayoutManager(linearLayoutManager);
        groupsAdapter = new GroupsAdapter(userList, this);
        friendRequestList.setAdapter(groupsAdapter);
    }

    @Override
    public void onItemClick(int position) {
        final String friendUsername = userList.get(position);
        final int thePosition = position;

        DatabaseReference checkClickRef = FirebaseDatabase.getInstance().getReference().child("grouprequests").child(friendUsername).child(groupName).child("invitedBy");
        checkClickRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("groups");
                    dbr.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int numGroups = 0;
                            boolean greaterThanFour = false;
                            for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                for (DataSnapshot user: ds.getChildren()) {
                                    if (user.child("username").getValue().equals(friendUsername)) {
                                        numGroups++;
                                    }
                                }
                                if (numGroups == 4) {
                                    greaterThanFour = true;
                                }
                            }
                            if (!greaterThanFour) {
                                new AlertDialog.Builder(getActivity())
                                        .setIcon(R.drawable.ic_addbell)
                                        .setTitle("Confirm Member")
                                        .setMessage("Would you like to invite " + friendUsername + " to your group?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("grouprequests")
                                                        .child(friendUsername)
                                                        .child(groupName)
                                                        .child("invitedBy").setValue(username);
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
