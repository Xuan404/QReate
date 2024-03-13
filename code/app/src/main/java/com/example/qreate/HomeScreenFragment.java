package com.example.qreate;


import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * This is the homescreen fragment that inflates after the User has updated his information
 * on the edit profile page.
 * If the User has previously logged in and used the app, then the homescreen will
 * automatically become the default landing page for the User
 *
 * @author Akib Zaman Choudhury
 */
public class HomeScreenFragment extends Fragment {

    /**
     * Creates the view and inflates the home_screen layout
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_screen, container, false);
        retrieveAndSetUserInfo(view);
        return view;
    }


    private void retrieveAndSetUserInfo(View view){

        String device_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .whereEqualTo("device_id", device_id)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {

                                // Since the unique ID is unique, we only expect one result
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                                String name = document.getString("name");
                                String phoneNumber = document.getString("phone_number");
                                String email = document.getString("email");

                                TextView nameText = view.findViewById(R.id.home_screen_welcome_user_text);
                                TextView phoneNumberText = view.findViewById(R.id.home_screen_phone_number_text);
                                TextView emailText = view.findViewById(R.id.home_screen_email_text);

                                nameText.setText(name);
                                phoneNumberText.setText(phoneNumber);
                                emailText.setText(email);

                            } else {
                                Log.d("Firestore", "No such document");
                            }
                        } else {
                            Log.d("Firestore", "get failed with ", task.getException());
                        }
                    }
                });
    }

}
