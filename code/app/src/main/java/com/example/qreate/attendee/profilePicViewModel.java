package com.example.qreate.attendee;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class profilePicViewModel extends ViewModel {
    private MutableLiveData<Bitmap> profileImage = new MutableLiveData<>();
    public LiveData<Bitmap> getGeneratedProfilePic(){
        return profileImage;
    }

    public void fetchGeneratedPic(Context context, String userId){
        UserProfilePicManager.fetchProfilePicInfoFromDataBase(context, userId, this::setGenProfileImage);
    }



    public void setGenProfileImage(Bitmap image) {
        profileImage.setValue(image);
    }
}
