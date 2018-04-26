package com.mattjamesashworth.android.slidingacw.Class.Handlers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mattjamesashworth.android.slidingacw.Class.PuzzleDBContract;

/**
 * Created by MattJAshworth on 30/03/2018.
 * For Sliding Puzzle ACW.
 * Last updated by MattJAshworth on 26/04/2018, see git log for updates.
 */

public class PuzzleDBHandler extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Puzzles.db";

    public PuzzleDBHandler(Context pContext)
    {
        super(pContext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase pDb)
    {
        pDb.execSQL(PuzzleDBContract.SQL_CREATE_PUZZLE_TABLE);
        pDb.execSQL(PuzzleDBContract.SQL_CREATE_LAYOUT_TABLE);
    }

    public void onUpgrade(SQLiteDatabase pDb, int pOldVersion, int pNewVersion)
    {
    }
}
