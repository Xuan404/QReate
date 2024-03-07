package com.example.qreate.administrator;

public class Image {
    private String imageName;
    private int image;

    public Image(String imageName, int image) {
        this.imageName = imageName;
        this.image = image;
    }

    public String getImageName() {
        return imageName;
    }

    public int getImage() {
        return image;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
