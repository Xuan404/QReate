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

public class NotifViewDetailsFragment extends Fragment {
    private TextView notifName;
    private TextView notifDescription;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.attendee_notifs_view_details, container, false);
        db = FirebaseFirestore.getInstance();
        notifName = view.findViewById(R.id.notif_name);
        notifDescription = view.findViewById(R.id.notif_description);

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

    public static NotifViewDetailsFragment newInstance(String notifId) {
        NotifViewDetailsFragment fragment = new NotifViewDetailsFragment();
        Bundle args = new Bundle();
        args.putString("notifId", notifId);
        fragment.setArguments(args);
        return fragment;
    }
}

