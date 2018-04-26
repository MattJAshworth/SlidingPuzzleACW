package com.mattjamesashworth.android.slidingacw.Class;

import android.provider.BaseColumns;

/**
 * Created by robda on 14/03/2018.
 */

public final class PuzzleDBContract
{

    private PuzzleDBContract()
    {

    }

    public static abstract class PuzzleEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "Puzzle";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_PICTURE_SET_DEFINITION = "PictureSet";
        public static final String COLUMN_LAYOUT_DEFINITION = "LayoutDef";
        public static final String HIGHSCORE = "Highscore";
        public static final String USERNAME = "username";
    }

    public static abstract class LayoutEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "Layout";
        public static final String COLUMN_NAME_NAME = "Name";
        //Row 1
        public static final String row1Col1 = "X1Y1";
        public static final String row1Col2 = "X2Y1";
        public static final String row1Col3 = "X3Y1";
        public static final String row1Col4 = "X4Y1";

        //Row 2
        public static final String row2Col1 = "X1Y2";
        public static final String row2Col2 = "X2Y2";
        public static final String row2Col3 = "X3Y2";
        public static final String row2Col4 = "X4Y2";

        //Row 3
        public static final String row3Col1 = "X1Y3";
        public static final String row3Col2 = "X2Y3";
        public static final String row3Col3 = "X3Y3";
        public static final String row3Col4 = "X4Y3";

        //Row 4
        public static final String row4Col1 = "X1Y4";
        public static final String row4Col2 = "X2Y4";
        public static final String row4Col3 = "X3Y4";
        public static final String row4Col4 = "X4Y4";
    }


    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_PUZZLE_TABLE = "CREATE TABLE " +
            PuzzleEntry.TABLE_NAME + " (" + PuzzleEntry._ID + " INTEGER PRIMARY KEY" +
            COMMA_SEP + PuzzleEntry.COLUMN_NAME_NAME +
            COMMA_SEP + PuzzleEntry.COLUMN_PICTURE_SET_DEFINITION +
            COMMA_SEP + PuzzleEntry.COLUMN_LAYOUT_DEFINITION +
            COMMA_SEP + PuzzleEntry.HIGHSCORE +
            COMMA_SEP + PuzzleEntry.USERNAME + " )";

    public static final String SQL_CREATE_LAYOUT_TABLE = "CREATE TABLE " +
            LayoutEntry.TABLE_NAME + " (" + LayoutEntry._ID + " INTEGER PRIMARY KEY" +
            COMMA_SEP + LayoutEntry.COLUMN_NAME_NAME +
            COMMA_SEP + LayoutEntry.row1Col1 + COMMA_SEP + LayoutEntry.row1Col2 +
            COMMA_SEP + LayoutEntry.row1Col3 + COMMA_SEP + LayoutEntry.row1Col4 +
            COMMA_SEP + LayoutEntry.row2Col1 + COMMA_SEP + LayoutEntry.row2Col2 +
            COMMA_SEP + LayoutEntry.row2Col3 + COMMA_SEP + LayoutEntry.row2Col4 +
            COMMA_SEP + LayoutEntry.row3Col1 + COMMA_SEP + LayoutEntry.row3Col2 +
            COMMA_SEP + LayoutEntry.row3Col3 + COMMA_SEP + LayoutEntry.row3Col4 +
            COMMA_SEP + LayoutEntry.row4Col1 + COMMA_SEP + LayoutEntry.row4Col2 +
            COMMA_SEP + LayoutEntry.row4Col3 + COMMA_SEP + LayoutEntry.row4Col4 + " )";


    public static final String SQL_DELETE_PUZZLE_TABLE = "DROP TABLE IF EXISTS " + PuzzleEntry.TABLE_NAME;
}
