package com.maxwa.friendlywager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.maxwa.friendlywager.adapters.FriendsRecyclerAdapter;
import com.maxwa.friendlywager.adapters.RequestsPagerAdapter;
import com.maxwa.friendlywager.models.Friends;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class AddWagerFragment extends Fragment implements View.OnClickListener {
    private RecyclerView requestsList;

    private ArrayList<Friends> friendsArrayList = new ArrayList<>();
    private FriendsRecyclerAdapter friendsRecyclerAdapter;

    View view;
    ViewPager viewPager;
    TabLayout tabLayout;

    String username;

    public AddWagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RequestsFragment
     */
    // TODO: Rename and change types and number of parameters
    public static AddWagerFragment newInstance() {
        AddWagerFragment fragment = new AddWagerFragment();
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
        view = inflater.inflate(R.layout.fragment_add_wager, container, false);

        viewPager = view.findViewById(R.id.wagerViewPager);
        tabLayout = view.findViewById(R.id.wagerTabLayout);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpViewPager(ViewPager viewPager) {
        RequestsPagerAdapter adapter = new RequestsPagerAdapter(getChildFragmentManager());

        adapter.addFragment(new MLBWagerFragment(), "MLB");
        adapter.addFragment(new NBAWagerFragment(), "NBA");
        adapter.addFragment(new NFLWagerFragment(), "NFL");

        viewPager.setAdapter(adapter);
    }

}
