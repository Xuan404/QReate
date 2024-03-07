package com.example.qreate.administrator;

public class AdministratorProfile {
    private String profileName;
    private int profileImage;

    public AdministratorProfile(String profileName, int profileImage) {
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
