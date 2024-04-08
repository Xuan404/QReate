package com.example.qreate.administrator;

/**
 * Represents an image associated with an administrator's dashboard, either related to a profile or an event.
 * The class encapsulates the properties of the image, including its name, URL, type, and an identifier.
 * It distinguishes between profile images and event images to facilitate appropriate handling within the application.
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
     * Constructs an instance of AdministratorImage with specified details.
     *
     * @param imageName         The reference name associated with the image, such as a profile name or event title.
     * @param imageUrl          The URL pointing to the image stored online.
     * @param generatedImageUrl The URL pointing to a generated or fallback image if the main image is unavailable.
     * @param id                The unique identifier associated with the image.
     * @param type              The type of the image, indicated by either {@code TYPE_PROFILE} or {@code TYPE_EVENT}.
     */
    public AdministratorImage(String imageName, String imageUrl, String generatedImageUrl, String id, int type) {
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.generatedImageUrl = generatedImageUrl;
        this.id = id;
        this.type = type;
    }

    /**
     * Retrieves the name associated with the image.
     *
     * @return The reference name of the image.
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * Retrieves the URL of the image.
     *
     * @return The URL pointing to the online storage of the image.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Retrieves the unique identifier of the image.
     *
     * @return The unique identifier for the image.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the URL of the generated or fallback image.
     *
     * @return The URL pointing to the generated or fallback image.
     */
    public String getGeneratedImageUrl() {
        return generatedImageUrl;
    }

    /**
     * Retrieves the type of the image.
     *
     * @return The type of the image, indicated by either {@code TYPE_PROFILE} or {@code TYPE_EVENT}.
     */
    public int getType() {
        return type;
    }
}
