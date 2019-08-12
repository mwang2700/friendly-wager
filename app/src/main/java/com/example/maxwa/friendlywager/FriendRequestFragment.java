package com.example.maxwa.friendlywager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.maxwa.friendlywager.adapters.RequestRecyclerAdapter;
import com.example.maxwa.friendlywager.models.Friends;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class FriendRequestFragment extends Fragment implements RequestRecyclerAdapter.OnItemClickListener {

    private RecyclerView friendRequestList;
    private RequestRecyclerAdapter requestRecyclerAdapter;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ArrayList<Friends> requestList = new ArrayList<>();
    DatabaseReference databaseReference;

    String username;
    String email;

    public FriendRequestFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FriendRequestFragment newInstance() {
        FriendRequestFragment fragment = new FriendRequestFragment();
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
        return inflater.inflate(R.layout.fragment_friend_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        friendRequestList = view.findViewById(R.id.friendRequestList);

        initializeRecycler();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    String val = (String) data.getValue();
                    if (!val.contains("@")) {
                        username = val;
                    } else {
                        email = val;
                    }
                }
                if (username != null) {
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("requests").child(username);

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            requestList = new ArrayList<>();
                            for (DataSnapshot data: dataSnapshot.getChildren()) {
                                Friends friend = data.getValue(Friends.class);
                                requestList.add(friend);
                            }
                            requestRecyclerAdapter = new RequestRecyclerAdapter(requestList, FriendRequestFragment.this);
                            friendRequestList.setAdapter(requestRecyclerAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

                    requestRecyclerAdapter.setOnItemClickListener(FriendRequestFragment.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error getting Friends List", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initializeRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        friendRequestList.setHasFixedSize(true);
        friendRequestList.setLayoutManager(linearLayoutManager);
        requestRecyclerAdapter = new RequestRecyclerAdapter(requestList, this);
        friendRequestList.setAdapter(requestRecyclerAdapter);
    }

    @Override
    public void onItemClick(int position) {
        Friends friend = requestList.get(position);
        final String friendUsername = friend.getUsername();
        final String friendEmail = friend.getEmail();
        final ArrayList<String> friendsUID = new ArrayList<>();

        new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_addbell)
                .setTitle("Confirm Friend Request")
                .setMessage("Would you like to accept " + friendUsername + "'s friend request?")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query userQuery = ref.child("requests").child(username).orderByChild("username").equalTo(friendUsername);

                        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                    String link = userSnapshot.getRef().toString();
                                    userSnapshot.getRef().removeValue();
                                    String[] parts = link.split("requests" + Pattern.quote("/") + username + Pattern.quote("/"));
                                    friendsUID.add(parts[1]);
                                }
                                // Update new friend's friends list
                                FirebaseDatabase.getInstance().getReference()
                                        .child("friends")
                                        .child(friendUsername)
                                        .child(mAuth.getUid())
                                        .child("email").setValue(email);
                                FirebaseDatabase.getInstance().getReference()
                                        .child("friends")
                                        .child(friendUsername)
                                        .child(mAuth.getUid())
                                        .child("username").setValue(username);
                                // Update current user's friends list
                                FirebaseDatabase.getInstance().getReference()
                                        .child("friends")
                                        .child(username)
                                        .child(friendsUID.get(0))
                                        .child("email").setValue(friendEmail);
                                FirebaseDatabase.getInstance().getReference()
                                        .child("friends")
                                        .child(username)
                                        .child(friendsUID.get(0))
                                        .child("username").setValue(friendUsername);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getActivity(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query userQuery = ref.child("requests").child(username).orderByChild("username").equalTo(friendUsername);

                        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                    userSnapshot.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getActivity(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .show();
    }
}
