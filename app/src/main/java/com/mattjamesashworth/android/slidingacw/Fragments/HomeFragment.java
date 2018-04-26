package com.mattjamesashworth.android.slidingacw.Fragments;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.mattjamesashworth.android.slidingacw.Activity.PlayActivity;
import com.mattjamesashworth.android.slidingacw.Class.Handlers.PuzzleDBHandler;
import com.mattjamesashworth.android.slidingacw.Class.PuzzleDBContract;
import com.mattjamesashworth.android.slidingacw.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by MattJAshworth on 30/03/2018.
 * For Sliding Puzzle ACW.
 * Last updated by MattJAshworth on 26/04/2018, see git log for updates.
 */

public class HomeFragment extends Fragment {


    View rootView;

    public HomeFragment() {
        // Required empty public constructor
    }

    PuzzleDBHandler m_DBHelperRead;
    PuzzleDBHandler m_DBHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.activity_main, container, false);

        Button downloadPuzzles = (Button) rootView.findViewById(R.id.btn_Download);
        Button localPuzzles = (Button) rootView.findViewById(R.id.btn_Local);

        downloadPuzzles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ondownloadPuzzles();
            }
        });

        localPuzzles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLocalPuzzles();
            }
        });

        m_DBHelperRead = new PuzzleDBHandler(rootView.getContext());
        m_DBHelper = new PuzzleDBHandler(rootView.getContext());


        RadioButton tap = (RadioButton) rootView.findViewById(R.id.radioButton);
        RadioButton slide = (RadioButton) rootView.findViewById(R.id.radioButton2);

        tap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), getString(R.string.changeMode), Toast.LENGTH_LONG).show();
            }
        });

        slide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), getString(R.string.changeMode), Toast.LENGTH_LONG).show();
            }
        });


        return rootView;
    }


    private class downloadJSON extends AsyncTask<String, String, String>
    {
        protected String doInBackground(String... args)
        {
            String formattedResult = "";
            try
            {
                ContentValues values = new ContentValues();
                SQLiteDatabase db = m_DBHelper.getWritableDatabase();
                SQLiteDatabase dbread = m_DBHelperRead.getReadableDatabase();
                String[] projection = {
                        PuzzleDBContract.PuzzleEntry._ID,
                        PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME
                };

                Cursor c = dbread.query(
                        PuzzleDBContract.PuzzleEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                String result = "";

                InputStream stream = (InputStream) new URL(args[0]).getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line = "";
                while (line != null)
                {
                    line = reader.readLine();
                    result += line;
                }

                JSONObject json = new JSONObject(result);
                formattedResult = "Puzzles";
                JSONArray puzzles = json.getJSONArray("PuzzleIndex");

                //c.moveToFirst();
                // String name = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME));

                if (c.getCount() <= 0)
                {
                    for (int i = 0; i < puzzles.length(); i++)
                    {
                        values.put(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME, puzzles.getString(i));
                        db.insert(PuzzleDBContract.PuzzleEntry.TABLE_NAME, null, values);
                        Log.i("Database", "Inserted in new database " + puzzles.getString(i));
                    }
                }
                else
                {
                    c.moveToFirst();
                    do
                    {
                        for (int i = 0; i < puzzles.length(); i++)
                        {
                            String name = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME));
                            if (name.equals(puzzles.get(i).toString()))
                            {
                                c.moveToNext();
                                continue;
                            }
                            else
                            {
                                values.put(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME, puzzles.getString(i));
                                db.insert(PuzzleDBContract.PuzzleEntry.TABLE_NAME, null, values);
                            }
                        }
                    } while (c.moveToNext());
                }
                c.close();
            } catch (Exception e)
            {
                //Toast.makeText(MainActivity.this, getString(R.string.noNetwork), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return formattedResult;
        }
    }


    ContentValues values = new ContentValues();

    public void ondownloadPuzzles()
    {
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
                rootView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info == null)
        {
            Toast.makeText(rootView.getContext(), getString(R.string.noNetwork), Toast.LENGTH_SHORT).show();
        }
        else
        {
            new HomeFragment.downloadJSON().execute(getString(R.string.puzzlesIndexURI));
            Toast.makeText(rootView.getContext(), (getString(R.string.downloadedPuzzles)), Toast.LENGTH_SHORT).show();

        }
    }


    public void onLocalPuzzles()
    {
        SQLiteDatabase dbread = m_DBHelperRead.getReadableDatabase();
        String[] projection = {
                PuzzleDBContract.PuzzleEntry._ID,
                PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME
        };

        Cursor c = dbread.query(
                PuzzleDBContract.PuzzleEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        if (c.getCount() <= 0)
        {
            Toast.makeText(rootView.getContext(), (getString(R.string.noPuzzlesFound)), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(rootView.getContext(), PlayActivity.class);
            startActivity(intent);
        }
    }


}
