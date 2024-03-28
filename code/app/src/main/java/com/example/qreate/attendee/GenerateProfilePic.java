package com.example.qreate.attendee;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class GenerateProfilePic {

    // generate a bitmap with initials drawn in
    public static Bitmap generateProfilePicture(String initials){
        int width = 160;
        int height = 160;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        //background circle
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#FCA311"));
        canvas.drawCircle(width/2f, height/2f, width / 2f, backgroundPaint);

        //Draw initials in the text
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(80);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // "/ 2f" will calculate center of bitmap
        Rect textBounds = new Rect();
        textPaint.getTextBounds(initials, 0, initials.length(), textBounds);
        float x = canvas.getWidth()/ 2f;
        float y = (canvas.getHeight()/ 2f) + (textBounds.height()/ 2f);
        canvas.drawText(initials, x, y, textPaint);

        return bitmap;
    }
}