package com.maxwa.friendlywager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maxwa.friendlywager.adapters.ViewWagerRecyclerAdapter;
import com.maxwa.friendlywager.models.ViewWager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewNBAFragment extends Fragment implements View.OnClickListener, ViewWagerRecyclerAdapter.OnItemClickListener {

    private RecyclerView viewRecyclerView;
    private ViewWagerRecyclerAdapter viewWagerAdapter;

    ArrayList<ViewWager> wagerList = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    String username;
    String groupName;

    long points = 0;
    long currentAmount = 0;

    public ViewNBAFragment() {
        // Required empty public constructor
    }

    public static ViewNBAFragment newInstance() {
        ViewNBAFragment fragment = new ViewNBAFragment();
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
        return inflater.inflate(R.layout.fragment_view_nba, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewRecyclerView = view.findViewById(R.id.nbaViewList);
        groupName = getActivity().getIntent().getStringExtra("groupName");


        initializeRecycler();

        DatabaseReference usernameRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());
        usernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = (String) dataSnapshot.child("username").getValue();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("bets").child("nba").child(groupName).child(username);
                ref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        updateWagers();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        updateWagers();
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        updateWagers();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        updateWagers();
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

    private void initializeRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        viewRecyclerView.setHasFixedSize(true);
        viewRecyclerView.setLayoutManager(linearLayoutManager);
        viewWagerAdapter = new ViewWagerRecyclerAdapter(wagerList, this);
        viewRecyclerView.setAdapter(viewWagerAdapter);
    }

    private void updateWagers() {
        DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference().child("bets").child("nba").child(groupName).child(username);
        gameRef.orderByChild("time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wagerList = new ArrayList<>();
                long commenceTime = 0;
                long amount = 0;
                double multiplier = 0;
                String team1 = "";
                String team2 = "";
                String selectedTeam = "";
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    String combinedTeams = data.getKey();
                    if (!combinedTeams.equals("Placeholder MatchPlaceholder Matches")) {
                        for (DataSnapshot subData : data.getChildren()) {
                            if (!subData.getKey().equals("time")) {
                                selectedTeam = subData.getKey();
                                if (subData.child("multiplier").getValue() instanceof Long) {
                                    Long val = (long) subData.child("multiplier").getValue();
                                    multiplier = val.doubleValue();
                                } else {
                                    multiplier = (double) subData.child("multiplier").getValue();
                                }
                                amount = (long) subData.child("amount").getValue();
                            }
                        }
                        commenceTime = (long) data.child("commenceTime").getValue();
                        int result = combinedTeams.indexOf(selectedTeam);
                        if (result == 0) {
                            team1 = selectedTeam;
                            team2 = combinedTeams.substring(selectedTeam.length());
                        } else {
                            team1 = combinedTeams.substring(0, result);
                            team2 = combinedTeams.substring(result);
                        }
                        ViewWager viewWager = new ViewWager(team1, team2, selectedTeam, "nba", commenceTime, amount, multiplier);
                        wagerList.add(viewWager);
                    }
                }
                viewWagerAdapter = new ViewWagerRecyclerAdapter(wagerList, ViewNBAFragment.this);
                viewRecyclerView.setAdapter(viewWagerAdapter);
                viewWagerAdapter.setOnItemClickListener(ViewNBAFragment.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to get wagers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(final int position) {
        if (wagerList.get(position).getCommenceTime() > System.currentTimeMillis() / 1000L) {
            currentAmount = wagerList.get(position).getWagerAmount();
            DatabaseReference pointsRef = FirebaseDatabase.getInstance().getReference().child(groupName).child(username).child("points");
            pointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    points = (long) dataSnapshot.getValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            final DatabaseReference betLocation = FirebaseDatabase.getInstance().getReference()
                    .child("bets").child("nba")
                    .child(groupName).child(username)
                    .child(wagerList.get(position).getTeam1() + wagerList.get(position).getTeam2())
                    .child(wagerList.get(position).getSelectedTeam());
            final DatabaseReference pointsLocation = FirebaseDatabase.getInstance().getReference()
                    .child(groupName).child(username).child("points");

            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);

            final TextView spacerText = new TextView(getContext());
            spacerText.setText("");

            final TextView bottomSpacerText = new TextView(getContext());
            bottomSpacerText.setText("");

            final EditText teamDisplay = new EditText(getContext());
            teamDisplay.setText(wagerList.get(position).getSelectedTeam());
            teamDisplay.setEnabled(false);
            teamDisplay.setTextColor(Color.BLACK);

            final EditText multiplierDisplay = new EditText(getContext());
            multiplierDisplay.setText(Double.toString(wagerList.get(position).getMultiplier()));
            multiplierDisplay.setEnabled(false);
            multiplierDisplay.setTextColor(Color.BLACK);

            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setRawInputType(Configuration.KEYBOARD_12KEY);
            input.setHint("Wager Amount");
            input.setText(Long.toString(currentAmount));

            layout.addView(spacerText);
            layout.addView(teamDisplay);
            layout.addView(input);
            layout.addView(multiplierDisplay);
            layout.addView(bottomSpacerText);

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("Update Wager");
            alert.setView(layout);
            alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    DatabaseReference betRef = betLocation.child("amount");
                    betRef.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            mutableData.setValue(Long.valueOf(input.getText().toString()));
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                            pointsLocation.runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                    long difference = 0;
                                    long value = 0;
                                    if (mutableData.getValue() != null) {
                                        value = (long) mutableData.getValue();
                                        difference = Long.valueOf(input.getText().toString()) - currentAmount;
                                    }
                                    value -= difference;
                                    mutableData.setValue(value);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                                }
                            });
                        }
                    });
                }
            });
            alert.setNegativeButton("Cancel", null);
            final AlertDialog dialog = alert.create();
            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (input.getText().toString().equals("") || Long.parseLong(input.getText().toString()) <= wagerList.get(position).getWagerAmount() ||
                            Long.parseLong(input.getText().toString()) > points) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    } else {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                }
            });


        }
    }

}
