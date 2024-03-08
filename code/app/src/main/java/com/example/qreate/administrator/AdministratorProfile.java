package com.example.qreate.administrator;

public class AdministratorProfile {
    private String profileName;
    private String profileImage;

    public AdministratorProfile(String profileName, String profileImage) {
        this.profileName = profileName;
        this.profileImage = profileImage;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
