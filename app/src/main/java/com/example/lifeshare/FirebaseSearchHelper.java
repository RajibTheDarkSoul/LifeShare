package com.example.lifeshare;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FirebaseSearchHelper {
    private DatabaseReference usersReference;

    public FirebaseSearchHelper() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersReference = database.getReference("Users"); // Adjust the reference path based on your Firebase structure
    }

    public void getAllUsers(final OnUsersDataLoadedListener listener) {
        final List<SearchUser> userList = new ArrayList<>();

        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    SearchUser user = userSnapshot.getValue(SearchUser.class);
                    if (user != null) {
                        SearchUser searchUser = new SearchUser(
                                userId,
                                user.getUserName(),
                                user.getBloodType(),
                                user.getAddress(),
                                user.getLastDonated(),
                                user.getDpImage()
                        );

                        userList.add(searchUser);
                    }
                }

                // Notify the listener with the loaded user data
                if (listener != null) {
                    listener.onUsersDataLoaded(userList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
                if (listener != null) {
                    listener.onDataLoadError(databaseError.getMessage());
                }
            }
        });
    }

    public interface OnUsersDataLoadedListener {
        void onUsersDataLoaded(List<SearchUser> userList);

        void onDataLoadError(String errorMessage);
    }
}
