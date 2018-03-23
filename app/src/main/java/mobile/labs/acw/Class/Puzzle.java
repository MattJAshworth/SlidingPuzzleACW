package mobile.labs.acw.Class;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Alexander on 25/02/2017.
 */

public class Puzzle {
    public int puzzle_ID;
    public String puzzle_PictureSet = "apple";
    public int puzzle_Rows;
    public List<Integer> puzzle_Layout;
    public String JSON_STRING = "";
    public int puzzle_High_Score = 0;

    public Puzzle(String pRawJSon, int pID, String pPictureSet, int pRows, List<Integer> pLayout){
        JSON_STRING = pRawJSon;
        puzzle_ID = pID;
        puzzle_PictureSet = pPictureSet;
        puzzle_Rows = pRows;
        puzzle_Layout = pLayout;
    }
}
