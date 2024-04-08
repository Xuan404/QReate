package com.example.qreate.administrator;

/**
 * Represents a user profile within the administrative section of the application.
 * This class encapsulates the essential attributes of a profile such as the owner's name,
 * the profile picture URL, a generated picture URL as a fallback, and a unique identifier.
 * Additional details and functionalities for profiles are planned for future implementation.
 */
public class AdministratorProfile {
    private String profileName;
    private String profileImage;
    private String generatedPic;
    private String id;

     /**
     * Constructs an AdministratorProfile with specified details.
     *
     * @param profileName  The name of the profile owner.
     * @param profileImage The URL to the profile's main image.
     * @param generatedPic The URL to a generated or fallback image if the main image is unavailable.
     * @param id           A unique document identifier for the profile in the Users collection.
     */
    public AdministratorProfile(String profileName, String profileImage,String generatedPic, String id) {
        this.profileName = profileName;
        this.profileImage = profileImage;
        this.generatedPic = generatedPic;
        this.id = id;
    }

    /**
     * Retrieves the name of the profile owner.
     *
     * @return The name of the profile owner.
     */
    public String getProfileName() {
        return profileName;
    }

    /**
     * Retrieves the URL of the profile's main image.
     *
     * @return The URL to the profile's main image.
     */
    public String getProfileImage() {
        return profileImage;
    }

    /**
     * Retrieves the unique identifier of the profile.
     *
     * @return The unique document identifier for the profile in the Users collection.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the URL of the generated or fallback image for the profile.
     *
     * @return The URL to the generated or fallback image.
     */
    public String getGeneratedPic() {
        return generatedPic;
    }
}
