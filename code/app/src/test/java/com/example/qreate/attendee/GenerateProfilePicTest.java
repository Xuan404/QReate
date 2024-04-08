package com.example.qreate.attendee;

import static org.junit.Assert.*;

import android.graphics.Bitmap;

import com.google.zxing.WriterException;

import org.junit.Test;

public class GenerateProfilePicTest {

    @Test
    public void generateProfilePicture() {
        Bitmap profilePicTest = GenerateProfilePic.generateProfilePicture("TS");
        assertEquals(160, profilePicTest.getWidth());
        assertEquals(160, profilePicTest.getHeight());
    }
}