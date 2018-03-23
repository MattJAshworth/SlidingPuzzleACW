package com.mattjamesashworth.android.slidingacw.Class;

import java.util.List;

/**
 * Created by mattjashworth on 21/03/2018.
 * Updated by mattjashworth on 23/03/2018, see git log for updates.
 */

public class Puzzle {
    public int puzzle;
    public String puzzle_PictureSet;
    public int puzzle_Rows;
    public List<Integer> puzzle_Layout;
    public String JSON_STRING = "";
    public int puzzle_High_Score = 0;

    public Puzzle(String pRawJSon, int puzz, String pPictureSet, int pRows, List<Integer> pLayout){
        JSON_STRING = pRawJSon;
        puzzle = puzz;
        puzzle_PictureSet = pPictureSet;
        puzzle_Rows = pRows;
        puzzle_Layout = pLayout;
    }
}
