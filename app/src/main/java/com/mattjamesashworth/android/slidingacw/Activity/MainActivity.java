package com.mattjamesashworth.android.slidingacw.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mattjamesashworth.android.slidingacw.Class.Handlers.PuzzleDBHandler;
import com.mattjamesashworth.android.slidingacw.Class.PuzzleDBContract;
import com.mattjamesashworth.android.slidingacw.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity
{
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

    PuzzleDBHandler m_DBHelper = new PuzzleDBHandler(this);
    PuzzleDBHandler m_DBHelperRead = new PuzzleDBHandler(this);
    ContentValues values = new ContentValues();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClickDownloadPuzzles(View view)
    {
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info == null)
        {
            Toast.makeText(MainActivity.this, "No Network Connection", Toast.LENGTH_SHORT).show();
        }
        else
        {
            new downloadJSON().execute(getString(R.string.puzzleIndexURL));
            Toast.makeText(this, (getString(R.string.downloadedPuzzles)), Toast.LENGTH_SHORT).show();

        }
    }


    public void onClickStoredPuzzles(View view)
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
            Toast.makeText(this, (getString(R.string.noPuzzlesFound)), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(this, SelectPuzzleActivity.class);
            startActivity(intent);
        }
    }
    public void onClickHighscore(View view) {
        SQLiteDatabase dbread = m_DBHelperRead.getReadableDatabase();
        String[] projection = {
                PuzzleDBContract.PuzzleEntry._ID,
                PuzzleDBContract.PuzzleEntry.HIGHSCORE
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
            Toast.makeText(this, (getString(R.string.noHighscores)), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(this, Highscores.class);
            startActivity(intent);
        }

    }

}
