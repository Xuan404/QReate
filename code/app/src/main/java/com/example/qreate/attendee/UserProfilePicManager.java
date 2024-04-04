package com.example.qreate.attendee;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class UserProfilePicManager {
    public interface OnProfileImageFetchListener {
        void onImageFetched(Bitmap image);
    }

    
    /**
     * Fetch info about user information specifically their profile pic stored on firebase.
     * @param context
     * @param device_id
     * @param listener
     */
    public static void fetchProfilePicInfoFromDataBase(Context context, String device_id, OnProfileImageFetchListener listener){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .whereEqualTo("device_id", device_id)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        QuerySnapshot querySnapshot = task.getResult();
                        if(querySnapshot != null && !querySnapshot.isEmpty()){
                            DocumentSnapshot documentSnap = querySnapshot.getDocuments().get(0);
                            String generatedProfilePicBase64 = documentSnap.getString("generated_pic");
                            if(generatedProfilePicBase64 != null){
                                //decode and then set
                                Bitmap profileBitmap = decodeBase64(generatedProfilePicBase64);
                                listener.onImageFetched(profileBitmap);

                            }
                        }
                    }else {
                        Log.e("FetchPicInfoFromUser", "Error fetching pic from firestore", task.getException());
                    }
                });


    }

    /**
     * Returns a bitmap image from a generated profile pic stored in Base64 on Firebase
     *
     * @param generatedProfilePicBase64
     * @return bitmap of generated profile pic
     */
    private static Bitmap decodeBase64(String generatedProfilePicBase64) {
        byte[] bytes = android.util.Base64.decode(generatedProfilePicBase64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);

    }



}
