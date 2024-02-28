package com.example.lifeshare;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;


public class searchFragment extends Fragment {
    ShimmerFrameLayout shimmerFrameLayout;
    RecyclerView rec;
    EditText searchText;
    ImageButton filters;

    private EditText searchEditText;
    private RecyclerView recyclerView;
    private SearchAdapter userAdapter;
    private List<SearchUser> userList;
    private FirebaseSearchHelper firebaseSearchHelper;





    public searchFragment() {
        // Required empty public constructor
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search, container, false);
        shimmerFrameLayout=view.findViewById(R.id.shimmer_search);
        rec=view.findViewById(R.id.recyclerSearch);
        searchText=view.findViewById(R.id.searchEdit);
        filters=view.findViewById(R.id.moreOptions);
        shimmerFrameLayout.setVisibility(View.GONE);
      //  shimmerFrameLayout.startShimmerAnimation();


        searchEditText = view.findViewById(R.id.searchEdit);
        recyclerView = view.findViewById(R.id.recyclerSearch);

        userList = new ArrayList<>();
        userAdapter = new SearchAdapter(userList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(userAdapter);

        firebaseSearchHelper = new FirebaseSearchHelper();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmerAnimation();
                recyclerView.setVisibility(View.GONE);
                // Get the search text
                String searchText = editable.toString().trim();

                // Perform the search
                performSearch(searchText);
            }
        });



        return view;
    }

    private void performSearch(final String searchText) {
        firebaseSearchHelper.getAllUsers(new FirebaseSearchHelper.OnUsersDataLoadedListener() {
            @Override
            public void onUsersDataLoaded(List<SearchUser> userList) {
                List<SearchUser> searchResults = new ArrayList<>();

                // Perform a local search based on the username
                for (SearchUser user : userList) {
                    if (user.getUserName().toLowerCase().contains(searchText.toLowerCase())) {
                        searchResults.add(user);
                    }
                }

                // Display the search results in your RecyclerView
                displaySearchResults(searchResults);
            }

            @Override
            public void onDataLoadError(String errorMessage) {
                // Handle the error
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void displaySearchResults(List<SearchUser> searchResults) {
        // Clear the existing list
        userList.clear();
        userAdapter.notifyDataSetChanged();

        // Add the search results to the list
        userList.addAll(searchResults);

        // Notify the adapter of the data set changes
        userAdapter.notifyDataSetChanged();

        shimmerFrameLayout.setVisibility(View.GONE);
        shimmerFrameLayout.stopShimmerAnimation();

        recyclerView.setVisibility(View.VISIBLE);
    }


}