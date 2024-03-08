package com.example.qreate.administrator;

public class AdministratorImage {
    private String imageName;
    private String image;

    public AdministratorImage(String imageName, String image) {
        this.imageName = imageName;
        this.image = image;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImage() {
        return image;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
