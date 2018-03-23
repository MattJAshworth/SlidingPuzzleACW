package com.mattjamesashworth.android.slidingacw.Class;

import android.graphics.Bitmap;

/**
 * Created by mattjashworth on 21/03/2018.
 */

public class SlidingUtils {

    public static String StripPuzzleDownToID(String puzzleName, int extensionLength){
        String puzzleFullName = puzzleName.substring("puzzle".length(), puzzleName.length()); // remove "puzzle" from string
        return puzzleFullName.substring(0, puzzleFullName.length()-extensionLength);
    }

    // SOURCE: http://stackoverflow.com/questions/8471226/how-to-resize-image-bitmap-to-a-given-size
    // original
    public static Bitmap scaleDown(Bitmap realImage, float dimensions,
                                   boolean filter) {
        float ratio = Math.min(
                (float) dimensions / realImage.getWidth(),
                (float) dimensions / realImage.getHeight());
        int newWidth = Math.round((float) ratio * realImage.getWidth());
        int newHeight = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, newWidth,
                newHeight, filter);
        return newBitmap;
    }

    // adapted to take in width and height as params
    public static Bitmap scaleDown(Bitmap realImage, float width, float height,
                                   boolean filter) {
        float ratio = Math.min(
                (float) width / realImage.getWidth(),
                (float) height / realImage.getHeight());
        int newWidth = Math.round((float) ratio * realImage.getWidth());
        int newHeight = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, newWidth,
                newHeight, filter);
        return newBitmap;
    }


    public static String clipExtension(String s, int extensionLength){
        return s.substring(0, s.length()-extensionLength);
    }
}
