package com.example.qreate.attendee;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A Fragment that displays notifications to the attendee. This class is responsible for
 * fetching notification data from Firestore and displaying it in a ListView. Each notification
 * consists of a message and details, encapsulated in a Notif object. The fragment uses a custom
 * ArrayAdapter (NotifArrayAdapter) to display the notifications in the ListView.
 *
 * Outstanding: Receiving from organizer from firebase
 *
 * @author Shraddha Mehta
 */

public class AttendeeNotificationsFragment extends Fragment {

    private ListView notificationsListView;
    private ArrayList<Notif> notificationsArrayList;
    private NotifArrayAdapter notifArrayAdapter;
    private FirebaseFirestore db;

    /**
     * This method inflates the layout for the notifications page and sets up the
     * ListView with a custom ArrayAdapter.
     * It also initiates fetching of notification data from Firestore.
     *
     * @param inflater LayoutInflater: The LayoutInflater object that can be used to inflate
     *                 any views in the fragment.
     * @param container ViewGroup: If non-null, this is the parent view that the fragment's
     *                 UI should be attached to.
     * @param savedInstanceState Bundle: If non-null, this fragment is being re-constructed
     *                 from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.attendee_notifications_page, container, false);

        //initialize data
        notificationsArrayList = new ArrayList<>();
        //set up adapter
        notifArrayAdapter = new NotifArrayAdapter(getContext(), notificationsArrayList);
        //set up the ListView
        notificationsListView = view.findViewById(R.id.notif_list_view);
        notificationsListView.setAdapter(notifArrayAdapter);


        //Fetch Data from firestore
        db = FirebaseFirestore.getInstance();
        fetchNotificationsFromFireStore();


        //set up item click listener
        notificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        return view;
    }

    /**
     * Fetches notification data from Firestore, parses the data into Notif objects, and adds
     * them to the notificationsArrayList. It then notifies the notifArrayAdapter of the data
     * change to refresh the ListView. If there is an error fetching data, it logs the error.
     */


    private void fetchNotificationsFromFireStore() {
        db.collection("notifications")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                            String notificationMessage = documentSnapshot.getString("message");
                            String notificationsDetails = documentSnapshot.getString("details");

                            Notif notification = new Notif(notificationMessage, notificationsDetails);
                            notificationsArrayList.add(notification);
                        }
                        notifArrayAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "error fetching notifications", e);
                    }
                });
    }

}
