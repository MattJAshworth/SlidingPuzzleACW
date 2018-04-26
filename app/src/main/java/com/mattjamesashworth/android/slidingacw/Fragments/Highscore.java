package com.mattjamesashworth.android.slidingacw.Fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mattjamesashworth.android.slidingacw.Class.Handlers.PuzzleDBHandler;
import com.mattjamesashworth.android.slidingacw.Class.Puzzle;
import com.mattjamesashworth.android.slidingacw.Class.PuzzleAdapter;
import com.mattjamesashworth.android.slidingacw.Class.PuzzleDBContract;
import com.mattjamesashworth.android.slidingacw.R;

import java.util.ArrayList;

/**
 * Created by MattJAshworth on 30/03/2018.
 * For Sliding Puzzle ACW.
 * Last updated by MattJAshworth on 26/04/2018, see git log for updates.
 */

public class Highscore extends Fragment {

    View rootView;


    public Highscore() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_highscores, container, false);
        SQLiteDatabase db = new PuzzleDBHandler(rootView.getContext()).getReadableDatabase();
        String[] projection = {
                PuzzleDBContract.PuzzleEntry._ID,
                PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME,
                PuzzleDBContract.PuzzleEntry.HIGHSCORE,
                PuzzleDBContract.PuzzleEntry.USERNAME
        };

        Cursor c = db.query(
                PuzzleDBContract.PuzzleEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList puzzleList = new ArrayList<Puzzle>();

        c.moveToFirst();

        String name = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME));
        int highScore = c.getInt(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.HIGHSCORE));
        int id = c.getInt(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry._ID));
        String username = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.USERNAME));

        String highScoreText = Integer.toString(highScore);
        Puzzle puzzle = new Puzzle(name, null, null, highScoreText, id, username);

        String formattedList = "Puzzle: " + puzzle.Name() + "\nScore: " + puzzle.Highscore() + "\nPlayer: " + puzzle.Username();
        puzzleList.add(formattedList);

        while (c.moveToNext())
        {
            name = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME));
            highScore = c.getInt(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.HIGHSCORE));
            id = c.getInt(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry._ID));
            username = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.USERNAME));
            if(highScore == 0)
            {
                continue;
            }
            highScoreText = Integer.toString(highScore);
            puzzle = new Puzzle(name, null, null, highScoreText, id, username);
            formattedList = "Puzzle: " + puzzle.Name() + "\nScore: " + puzzle.Highscore() + "\nPlayer: " + puzzle.Username();

            puzzleList.add(formattedList);
        }
        c.close();
        PuzzleAdapter adapter = new PuzzleAdapter(rootView.getContext(), android.R.layout.simple_list_item_1, puzzleList);
        final ListView listView = (ListView) rootView.findViewById(R.id.highscore);
        listView.setAdapter(adapter);


        return rootView;
    }

}
