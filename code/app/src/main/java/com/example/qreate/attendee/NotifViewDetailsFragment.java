package com.example.qreate.attendee;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * NotifViewDetailsFragment is a Fragment subclass used to display the details of a specific notification.
 * It fetches and shows the notification's title and description based on the notification ID passed through its arguments.
 * The class also manages navigation by allowing users to return to the previous screen.
 */
public class NotifViewDetailsFragment extends Fragment {
    private TextView notifName;
    private TextView notifDescription;
    private FirebaseFirestore db;

    /**
     * Inflates the layout for this fragment, initializes Firestore and UI components, and sets up a listener
     * for the back button. It retrieves notification details from Firestore based on the provided notification ID.
     *
     * @param inflater           LayoutInflater object to inflate views in the fragment
     * @param container          If non-null, this is the parent view to which the fragment's UI should be attached
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state
     * @return view              Returns the View for the fragment's UI, or null
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.attendee_notifs_view_details, container, false);
        db = FirebaseFirestore.getInstance();
        notifName = view.findViewById(R.id.notif_name);
        notifDescription = view.findViewById(R.id.notif_description);

        Button backButton = view.findViewById(R.id.event_details_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomNavigationBar();
                // Pop the current fragment off the stack to return to the previous one
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                }
            }
        });

        // Retrieve the event ID passed from the previous fragment
        Bundle args = getArguments();
        String notifId;
        if (args != null) {
            notifId = args.getString("notifId");
        } else {
            notifId = null;
        }

        if (notifId != null) {
            db.collection("Announcements").document(notifId)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Extract event details from the document and update the UI
                                notifName.setText(document.getString("title"));
                                notifDescription.setText(document.getString("description"));
                            } else {
                                Log.d("Firestore", "Error getting documents: ", task.getException());
                            }
                        } else {
                            Log.d("Firestore", "Task Failure: ", task.getException());
                        }
                    });
        }

        return view;

    }

    /**
     * Shows the bottom navigation bar by calling a method in the parent Activity. This method is
     * typically called when navigating away from the current fragment.
     */
    private void showBottomNavigationBar() {
        // Find the BottomNavigationView and set its visibility to GONE
        ((AttendeeActivity)getActivity()).showBottomNavigationBar();
    }

    /**
     * Creates a new instance of NotifViewDetailsFragment with the provided notification ID as an argument.
     *
     * @param notifId The ID of the notification whose details are to be displayed
     * @return A new instance of NotifViewDetailsFragment
     */
    public static NotifViewDetailsFragment newInstance(String notifId) {
        NotifViewDetailsFragment fragment = new NotifViewDetailsFragment();
        Bundle args = new Bundle();
        args.putString("notifId", notifId);
        fragment.setArguments(args);
        return fragment;
    }
}

