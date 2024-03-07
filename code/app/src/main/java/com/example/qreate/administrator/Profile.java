package com.example.qreate.administrator;

public class Profile {
    private String profileName;
    private int profileImage;

    public Profile(String profileName, int profileImage) {
        this.profileName = profileName;
        this.profileImage = profileImage;
    }

    public String getProfileName() {
        return profileName;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void setProfileImage(int profileImage) {
        this.profileImage = profileImage;
    }
}
