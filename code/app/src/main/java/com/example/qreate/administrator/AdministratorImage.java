package com.example.qreate.administrator;

/**
 * This class defines an image (queried from the database) and is used to store it and its name.
 */
public class AdministratorImage {
    public static final int TYPE_PROFILE = 0;
    public static final int TYPE_EVENT = 1;
    private String imageName;
    private String imageUrl;
    private String generatedImageUrl;
    private String id;
    private int type; // "Profiles" or "Events"

    /**
     * This is a constructor for the AdministratorImage class
     * @param imageName the reference name of the image i.e. profile name image or event name poster
     * @param imageUrl the image in string form
     */
    public AdministratorImage(String imageName, String imageUrl, String generatedImageUrl, String id, int type) {
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.generatedImageUrl = generatedImageUrl;
        this.id = id;
        this.type = type;
    }

    /**
     * This method returns the reference name of the image
     * @return the reference name of the image
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * This method returns the string form of the image
     * @return the string form of the image
     */
    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getGeneratedImageUrl() {
        return generatedImageUrl;
    }

    public int getType() {
        return type;
    }
}
