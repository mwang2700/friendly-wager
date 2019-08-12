package com.example.maxwa.friendlywager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maxwa.friendlywager.adapters.FriendsRecyclerAdapter;
import com.example.maxwa.friendlywager.models.Friends;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsFragment extends Fragment implements View.OnClickListener {

    private RecyclerView friendsList;
    private FriendsRecyclerAdapter friendsRecyclerAdapter;

    ArrayList<Friends> friendsArrayList = new ArrayList<>();
    String username;
    String email;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Button addFriendButton;
    EditText usernameEditText;
//    FirebaseRecyclerAdapter<Friends,ViewHolder> firebaseRecyclerAdapter;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();
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
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        friendsList = view.findViewById(R.id.friendsList);
        addFriendButton = view.findViewById(R.id.addFriendButton);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        addFriendButton.setOnClickListener(this);

        initializeRecycler();
        // Get username
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
                //               Log.i("Value of username", username);
                if (username != null) {
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("friends").child(username);

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            friendsArrayList = new ArrayList<>();
                            for (DataSnapshot data: dataSnapshot.getChildren()) {
                                Friends friend = data.getValue(Friends.class);
                                friendsArrayList.add(friend);
                            }
                            friendsRecyclerAdapter = new FriendsRecyclerAdapter(friendsArrayList);
                            friendsList.setAdapter(friendsRecyclerAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error getting Friends List", Toast.LENGTH_SHORT).show();
            }
        });

//        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
//                .setQuery(query, Friends.class)
//                .build();
//       firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<String, ViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Friends friends) {
//                Log.i("CURRENTLY HERE", "IN LINE 85");
//                viewHolder.email.setText(friends.getEmail());
//            }
//
//            @NonNull
//            @Override
//            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                Log.i("CURRENTLY HERE", "IN LINE 92");
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friends_list_item, parent, false);
//                return new ViewHolder(view);
//            }
//        };
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        friendsList.setLayoutManager(linearLayoutManager);
//        friendsList.setHasFixedSize(true);
//        friendsList.setAdapter(firebaseRecyclerAdapter);
//        firebaseRecyclerAdapter.startListening();
//        initializeRecycler();
//        insertFakeFriends();
    }

    private void initializeRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        friendsList.setHasFixedSize(true);
        friendsList.setLayoutManager(linearLayoutManager);
        friendsRecyclerAdapter = new FriendsRecyclerAdapter(friendsArrayList);
        friendsList.setAdapter(friendsRecyclerAdapter);
    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        private TextView email;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            email = itemView.findViewById(R.id.friend_email);
//        }
//    }
//    private void addFriend(View view) {
//        if (usernameEditText.getText().toString().equals("")) {
//            Toast.makeText(getContext(), "Please enter an Email Address", Toast.LENGTH_SHORT).show();
//        } else if (!usernameEditText.getText().toString().contains("@")) {
//            Toast.makeText(getContext(), "Please enter a valid Email Address", Toast.LENGTH_SHORT).show();
//        } else {
//            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
//            ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    boolean sent = false;
//                    ArrayList<Friends> userArrayList = new ArrayList<>();
//                    for (DataSnapshot data: dataSnapshot.getChildren()) {
//                        Friends friend = data.getValue(Friends.class);
//                        userArrayList.add(friend);
//                        if (friend != null && friend.username.equals(usernameEditText.getText().toString())) {
//                            usernameEditText.setText("");
//                            sent = true;
//                            Toast.makeText(getContext(), "Friend request sent!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }

    @Override
    public void onClick(View view) {
        boolean alreadyFriend = false;
        boolean alreadySent = false;
        for (Friends f: friendsArrayList) {
            if (f.getUsername().equals(usernameEditText.getText().toString())) {
                Toast.makeText(getContext(), f.getUsername() +" is already your friend", Toast.LENGTH_SHORT).show();
                alreadyFriend = true;
                break;
            }
        }
        if (!alreadyFriend) {
            if (usernameEditText.getText().toString().equals("")) {
                Toast.makeText(getContext(), "Please enter a Username", Toast.LENGTH_SHORT).show();
            } else {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean sent = false;
                        ArrayList<Friends> userArrayList = new ArrayList<>();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Friends friend = data.getValue(Friends.class);
                            userArrayList.add(friend);
                            if (friend != null && friend.username.equals(usernameEditText.getText().toString())) {
                                usernameEditText.setText("");
                                sent = true;
                                Toast.makeText(getContext(), "Friend request sent!", Toast.LENGTH_SHORT).show();
                                FirebaseDatabase.getInstance().getReference()
                                        .child("requests")
                                        .child(friend.getUsername())
                                        .child(mAuth.getUid())
                                        .child("email").setValue(email);
                                FirebaseDatabase.getInstance().getReference()
                                        .child("requests")
                                        .child(friend.getUsername())
                                        .child(mAuth.getUid())
                                        .child("username").setValue(username);
                            }
                        }
                        if (!sent) {
                            Toast.makeText(getContext(), "Could not find user " + usernameEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
