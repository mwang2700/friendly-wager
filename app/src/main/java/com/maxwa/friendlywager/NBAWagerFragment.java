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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maxwa.friendlywager.adapters.GamesRecyclerAdapter;
import com.maxwa.friendlywager.models.Game;
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

public class NBAWagerFragment extends Fragment implements View.OnClickListener,  GamesRecyclerAdapter.OnItemClickListener {

    private RecyclerView gamesRecyclerView;
    private GamesRecyclerAdapter gamesAdapter;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ArrayList<Game> gamesList = new ArrayList<>();

    String username;
    String groupName;
    String teamSelected;

    double multiplierSelected = 0.00;
    long points = 0;

    public NBAWagerFragment() {
        // Required empty public constructor
    }

    public static NBAWagerFragment newInstance() {
        NBAWagerFragment fragment = new NBAWagerFragment();
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
        return inflater.inflate(R.layout.fragment_nba, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gamesRecyclerView = view.findViewById(R.id.nbaWagerList);
        groupName = getActivity().getIntent().getStringExtra("groupName");

        initializeRecycler();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("odds").child("nba");
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
        DatabaseReference usernameRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());
        usernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = (String) dataSnapshot.child("username").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        gamesRecyclerView.setHasFixedSize(true);
        gamesRecyclerView.setLayoutManager(linearLayoutManager);
        gamesAdapter = new GamesRecyclerAdapter(gamesList, this);
        gamesRecyclerView.setAdapter(gamesAdapter);
    }

    private void updateWagers() {
        DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference().child("odds").child("nba");
        gameRef.orderByChild("time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gamesList = new ArrayList<>();
                long commenceTime = 0;
                double multiplier1 = 0;
                double multiplier2 = 0;
                String team1 = "";
                String team2 = "";
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    if (!data.getKey().equals("Placeholder MatchPlaceholder Matches")) {
                        for (DataSnapshot subData : data.getChildren()) {
                            if (subData.getKey().equals("time")) {
                                commenceTime = (long) subData.getValue();
                            } else if (data.getKey().substring(0, subData.getKey().length()).equals(subData.getKey())) {
                                team1 = subData.getKey();
                                multiplier1 = (double) subData.child("multiplier").getValue();
                            } else {
                                team2 = subData.getKey();
                                multiplier2 = (double) subData.child("multiplier").getValue();
                            }
                        }
                        Game game = new Game(team1, team2, "nba", commenceTime, multiplier1, multiplier2);
                        gamesList.add(game);
                    }
                }
                gamesAdapter = new GamesRecyclerAdapter(gamesList, NBAWagerFragment.this);
                gamesRecyclerView.setAdapter(gamesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to get games", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(int position) {
        final int pos = position;
        DatabaseReference existing = FirebaseDatabase.getInstance().getReference()
                .child("bets").child("nba")
                .child(groupName).child(username)
                .child(gamesList.get(position).getTeam1() + gamesList.get(position).getTeam2());
        existing.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
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
                    LinearLayout layout = new LinearLayout(getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final TextView spacerText = new TextView(getContext());
                    spacerText.setText("");

                    final TextView bottomSpacerText = new TextView(getContext());
                    bottomSpacerText.setText("");

                    final EditText multiplierDisplay = new EditText(getContext());
                    multiplierDisplay.setText("");
                    multiplierDisplay.setEnabled(false);
                    multiplierDisplay.setTextColor(Color.BLACK);

                    final Spinner teamChoice = new Spinner(getContext());
                    String[] items = new String[2];
                    items[0] = gamesList.get(pos).getTeam1();
                    items[1] = gamesList.get(pos).getTeam2();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    teamChoice.setAdapter(adapter);
                    teamChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if (i == 0) {
                                String text = "Multiplier: " + Double.toString(gamesList.get(pos).getMultiplier1());
                                teamSelected = gamesList.get(pos).getTeam1();
                                multiplierSelected = gamesList.get(pos).getMultiplier1();
                                multiplierDisplay.setText(text);
                            } else {
                                String text = "Multiplier: " + Double.toString(gamesList.get(pos).getMultiplier2());
                                teamSelected = gamesList.get(pos).getTeam2();
                                multiplierSelected = gamesList.get(pos).getMultiplier2();
                                multiplierDisplay.setText(text);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                            multiplierDisplay.setText("");
                        }
                    });

                    final EditText input = new EditText(getActivity());
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    input.setRawInputType(Configuration.KEYBOARD_12KEY);
                    input.setHint("Wager Amount");
                    input.setText("0");

                    layout.addView(spacerText);
                    layout.addView(teamChoice);
                    layout.addView(input);
                    layout.addView(multiplierDisplay);
                    layout.addView(bottomSpacerText);

                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("Create Wager");
                    alert.setView(layout);
                    alert.setPositiveButton("Place Wager", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            DatabaseReference betRef = FirebaseDatabase.getInstance().getReference()
                                    .child("bets").child("nba")
                                    .child(groupName).child(username)
                                    .child(gamesList.get(pos).getTeam1() + gamesList.get(pos).getTeam2());
                            betRef.child(teamSelected).child("amount").setValue(Long.parseLong(input.getText().toString()));
                            betRef.child(teamSelected).child("multiplier").setValue(multiplierSelected);
                            betRef.child("time").setValue(gamesList.get(pos).getCommenceTime());
                            DatabaseReference pointsRef = FirebaseDatabase.getInstance().getReference().child(groupName).child(username).child("points");
                            pointsRef.runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                    long value = 0;
                                    if (mutableData.getValue() != null) {
                                        value = (long) mutableData.getValue();
                                    }
                                    value -= Long.parseLong(input.getText().toString());
                                    mutableData.setValue(value);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                    Toast.makeText(getActivity(), "Successfully created wager", Toast.LENGTH_SHORT).show();
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
                            if (input.getText().toString().equals("") || Long.parseLong(input.getText().toString()) <= 0 || Long.parseLong(input.getText().toString()) > points) {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            } else {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            }
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
