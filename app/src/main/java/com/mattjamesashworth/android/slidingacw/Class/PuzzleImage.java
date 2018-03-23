package com.mattjamesashworth.android.slidingacw.Class;

import android.graphics.Bitmap;

/**
 * Created by mattjashworth on 21/03/2018.
 */

public class PuzzleImage{
    private String fileName;
    private String imageName;
    private Bitmap image;

    public String getFileName(){
        return fileName;
    }
    public String getImageName(){
        return imageName;
    }
    public Bitmap getImage(){
        return image;
    }

    public PuzzleImage(String pFileName, String pImageName, Bitmap pImage){
        fileName = pFileName;
        imageName = pImageName;
        image = pImage;
    }
}