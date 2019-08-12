package com.example.maxwa.friendlywager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.maxwa.friendlywager.adapters.FriendsRecyclerAdapter;
import com.example.maxwa.friendlywager.adapters.RequestsPagerAdapter;
import com.example.maxwa.friendlywager.models.Friends;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class RequestsFragment extends Fragment implements View.OnClickListener {

    private RecyclerView requestsList;

    private ArrayList<Friends> friendsArrayList = new ArrayList<>();
    private FriendsRecyclerAdapter friendsRecyclerAdapter;

    View view;
    ViewPager viewPager;
    TabLayout tabLayout;

    ImageView checkboxImageView;
    ImageView declineImageView;
    String username;
    String email;

    public RequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RequestsFragment
     */
    // TODO: Rename and change types and number of parameters
    public static RequestsFragment newInstance() {
        RequestsFragment fragment = new RequestsFragment();
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
        view = inflater.inflate(R.layout.fragment_requests, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

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

        adapter.addFragment(new FriendRequestFragment(), "Friend Requests");
        adapter.addFragment(new GroupRequestFragment(), "Group Requests");

        viewPager.setAdapter(adapter);
    }

    //
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        private TextView email, commonGroups;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            email = itemView.findViewById(R.id.friend_email);
//            commonGroups = itemView.findViewById(R.id.friend_groupCount);
//        }
//    }
}
