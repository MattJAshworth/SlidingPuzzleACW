package com.mattjamesashworth.android.slidingacw.Class;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.mattjamesashworth.android.slidingacw.Class.Puzzle;

import java.util.ArrayList;

/**
 * Created by MattJAshworth on 30/03/2018.
 * For Sliding Puzzle ACW.
 * Last updated by MattJAshworth on 26/04/2018, see git log for updates.
 */

public class PuzzleAdapter extends ArrayAdapter<Puzzle>
{
    public PuzzleAdapter(Context pContext, int pTextViewResourceId, ArrayList<Puzzle> pItems)
    {
        super(pContext, pTextViewResourceId, pItems);
    }
}
