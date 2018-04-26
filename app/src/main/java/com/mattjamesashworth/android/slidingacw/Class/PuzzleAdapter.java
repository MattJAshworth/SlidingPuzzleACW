package com.mattjamesashworth.android.slidingacw.Class;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.mattjamesashworth.android.slidingacw.Class.Puzzle;

import java.util.ArrayList;

/**
 * Created by robda on 14/03/2018.
 */

public class PuzzleAdapter extends ArrayAdapter<Puzzle>
{
    public PuzzleAdapter(Context pContext, int pTextViewResourceId, ArrayList<Puzzle> pItems)
    {
        super(pContext, pTextViewResourceId, pItems);
    }
}
