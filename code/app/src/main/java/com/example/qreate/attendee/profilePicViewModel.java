package com.example.qreate.attendee;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Responsible for fetching generatedprofile pic from database and making it
 * available to fragments that need the image to have as profile pic
 */

public class profilePicViewModel extends ViewModel {
    private MutableLiveData<Bitmap> profileImage = new MutableLiveData<>();
    private MutableLiveData<String> profileImageUrl = new MutableLiveData<>();
    //uses generated pic as livedata so we can update automaticlly if user changes name
    /**
     * Gets the generated profile picture as a Bitmap wrapped in LiveData.
     * @return LiveData containing the Bitmap of the generated profile picture.
     */
    public LiveData<Bitmap> getGeneratedProfilePic(){
        return profileImage;
    }

    /**
     * Gets the URL of the profile picture as a String wrapped in LiveData.
     * @return LiveData containing the URL string of the profile picture.
     */
    public LiveData<String> getProfilePicUrl() {
        return profileImageUrl;
    }

    //gets generated pic from Firestore and updates live object
    //UserProfilePicManager provides pic
    /**
     * Fetches the generated profile picture from Firestore based on the user's ID and updates the LiveData.
     * The fetched picture is set using setGenProfileImage(Bitmap image).
     * @param context The Context in which the library is operating.
     * @param userId The unique ID of the user whose profile picture is being fetched.
     */
    public void fetchGeneratedPic(Context context, String userId){
        UserProfilePicManager.fetchProfilePicInfoFromDataBase(context, userId, this::setGenProfileImage);
    }


    //sets pic to Livedata
    /**
     * Sets the Bitmap image of the generated profile picture to the MutableLiveData.
     * @param image The Bitmap image to be set.
     */
    public void setGenProfileImage(Bitmap image) {
        profileImage.setValue(image);
    }

    /**
     * Sets the URL of the profile picture to the MutableLiveData.
     * @param url The URL string of the profile picture to be set.
     */
    public void setProfileImageUrl(String url) {
        profileImageUrl.setValue(url);
    }

    /**
     * Fetches the URL of the user's profile picture from Firestore based on the user's ID.
     * If successful, updates the LiveData with the fetched URL. If the fetch fails or no picture is found,
     * updates LiveData with null.
     * @param userId The unique ID of the user whose profile picture URL is being fetched.
     */
    public void fetchProfilePicUrl(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").whereEqualTo("device_id", userId).limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    String profilePicUrl = document.getString("profile_pic");
                    profileImageUrl.setValue(profilePicUrl);
                } else {
                    profileImageUrl.setValue(null);
                }
            } else {
                profileImageUrl.setValue(null);
            }
        });
    }
}
