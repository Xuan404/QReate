package com.example.qreate.administrator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
//SWAP TO intellij and it should work
public class AdministratorImageTest {
    String name = "test image name";
    String image = "test imageUrl";
    String image_generated = "test generatedUrl";
    String id = "test id";
    int type;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getImageName() {
        AdministratorImage mockImage = new AdministratorImage(name, image, image_generated, id, type);
        assertEquals(name, mockImage.getImageName());
    }

    @Test
    public void getImage() {
        AdministratorImage mockImage = new AdministratorImage(name, image, image_generated, id, type);
        assertEquals(image, mockImage.getImageUrl());
    }

    @Test
    public void getId() {
        AdministratorImage mockImage = new AdministratorImage(name, image, image_generated, id, type);
        assertEquals(id, mockImage.getId());
    }

    @Test
    public void getImageType() {
        AdministratorImage mockImage = new AdministratorImage(name, image, image_generated, id, type);
        assertEquals(type, mockImage.getType());
    }
}