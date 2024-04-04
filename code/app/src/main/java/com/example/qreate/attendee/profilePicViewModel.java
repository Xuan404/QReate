package com.example.qreate.attendee;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Responsible for fetching generatedprofile pic from database and making it
 * available to fragments that need the image to have as profile pic
 */

public class profilePicViewModel extends ViewModel {
    private MutableLiveData<Bitmap> profileImage = new MutableLiveData<>();
    //uses generated pic as livedata so we can update automaticlly if user changes name
    public LiveData<Bitmap> getGeneratedProfilePic(){
        return profileImage;
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
}
