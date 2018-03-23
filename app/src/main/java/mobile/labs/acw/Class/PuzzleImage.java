package mobile.labs.acw.Class;

import android.graphics.Bitmap;

/**
 * Created by Alexander on 26/02/2017.
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