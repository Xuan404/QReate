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
    public LiveData<Bitmap> getGeneratedProfilePic(){
        return profileImage;
    }

    public LiveData<String> getProfilePicUrl() {
        return profileImageUrl;
    }

    //gets generated pic from Firestore and updates live object
    //UserProfilePicManager provides pic
    public void fetchGeneratedPic(Context context, String userId){
        UserProfilePicManager.fetchProfilePicInfoFromDataBase(context, userId, this::setGenProfileImage);
    }


    //sets pic to Livedata
    public void setGenProfileImage(Bitmap image) {
        profileImage.setValue(image);
    }

    public void setProfileImageUrl(String url) {
        profileImageUrl.setValue(url);
    }

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
