package com.mattjamesashworth.android.slidingacw.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mattjamesashworth.android.slidingacw.Class.Handlers.PuzzleDBHandler;
import com.mattjamesashworth.android.slidingacw.Class.Puzzle;
import com.mattjamesashworth.android.slidingacw.Class.PuzzleAdapter;
import com.mattjamesashworth.android.slidingacw.Class.PuzzleDBContract;
import com.mattjamesashworth.android.slidingacw.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by MattJAshworth on 30/03/2018.
 * For Sliding Puzzle ACW.
 * Last updated by MattJAshworth on 26/04/2018, see git log for updates.
 */

public class PlayActivity extends AppCompatActivity
{

    PuzzleDBHandler m_DBHelper = new PuzzleDBHandler(this);
    PuzzleDBHandler m_DBHelperRead = new PuzzleDBHandler(this);
    Boolean downloadingInProgress = true;
    String layout = "";
    String picture = "";
    String[] formattedLayoutRow1 = null, formattedLayoutRow2 = null,
            formattedLayoutRow3 = null, formattedLayoutRow4 = null;
    String[] fullLayoutArray = null;
    String puzzle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_puzzle);

        SQLiteDatabase db = new PuzzleDBHandler(this).getReadableDatabase();
        String[] projection = {
                PuzzleDBContract.PuzzleEntry._ID,
                PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME,
                PuzzleDBContract.PuzzleEntry.HIGHSCORE
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
        int id = c.getInt(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry._ID));
        puzzleList.add(new Puzzle(name, null, null, null, id, null));
        while (c.moveToNext())
        {
            name = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME));
            id = c.getInt(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry._ID));
            puzzleList.add(new Puzzle(name, null, null, null, id, null));
        }
        c.close();

        PuzzleAdapter adapter = new PuzzleAdapter(this, android.R.layout.simple_list_item_1, puzzleList);
        final ListView puzzleListView = (ListView) findViewById(R.id.PirateListView);
        puzzleListView.setAdapter(adapter);


        puzzleListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {

                position++;
                NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
                        getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
                if (info == null)
                {
                    Toast.makeText(PlayActivity.this, "No Network Connection", Toast.LENGTH_SHORT).show();
                    getStoredPuzzle(position);
                }
                else
                {
                    new downloadPuzzleDefinition().execute((getString(R.string.puzzleDefinitionURI)) + position + ".json");
                }

            }
        });
    }

    protected void getStoredPuzzle(int position)
    {
        SQLiteDatabase dbRead = m_DBHelperRead.getReadableDatabase();

        String[] projection = {
                PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME,
                PuzzleDBContract.PuzzleEntry.COLUMN_PICTURE_SET_DEFINITION,
                PuzzleDBContract.PuzzleEntry.COLUMN_LAYOUT_DEFINITION
        };
        Cursor c = dbRead.query(
                PuzzleDBContract.PuzzleEntry.TABLE_NAME,
                projection,
                null, null, null, null, null
        );
        c.moveToFirst();
        do
        {
            String name = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME));
            String layout = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.COLUMN_LAYOUT_DEFINITION));
            String picture = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.COLUMN_PICTURE_SET_DEFINITION));
            if (name.equals("puzzle" + position + ".json"))
            {
                Log.i("info", "Found");
                String[] layoutProjection = {
                        PuzzleDBContract.LayoutEntry.COLUMN_NAME_NAME,
                        PuzzleDBContract.LayoutEntry.row1Col1,
                        PuzzleDBContract.LayoutEntry.row1Col2,
                        PuzzleDBContract.LayoutEntry.row1Col3,
                        PuzzleDBContract.LayoutEntry.row1Col4,
                        PuzzleDBContract.LayoutEntry.row2Col1,
                        PuzzleDBContract.LayoutEntry.row2Col2,
                        PuzzleDBContract.LayoutEntry.row2Col3,
                        PuzzleDBContract.LayoutEntry.row2Col4,
                        PuzzleDBContract.LayoutEntry.row3Col1,
                        PuzzleDBContract.LayoutEntry.row3Col2,
                        PuzzleDBContract.LayoutEntry.row3Col3,
                        PuzzleDBContract.LayoutEntry.row3Col4,
                        PuzzleDBContract.LayoutEntry.row4Col1,
                        PuzzleDBContract.LayoutEntry.row4Col2,
                        PuzzleDBContract.LayoutEntry.row4Col3,
                        PuzzleDBContract.LayoutEntry.row4Col4,
                };
                Cursor d = dbRead.query(
                        PuzzleDBContract.LayoutEntry.TABLE_NAME,
                        layoutProjection,
                        null, null, null, null, null
                );

                d.moveToFirst();
                do
                {
                    String layoutName = d.getString(d.getColumnIndexOrThrow(PuzzleDBContract.LayoutEntry.COLUMN_NAME_NAME));
                    if (layoutName.equals(layout))
                    {
                        String row1Col1 = d.getString(d.getColumnIndexOrThrow(PuzzleDBContract.LayoutEntry.row1Col1));
                        String row1Col2 = d.getString(d.getColumnIndexOrThrow(PuzzleDBContract.LayoutEntry.row1Col2));
                        String row1Col3 = d.getString(d.getColumnIndexOrThrow(PuzzleDBContract.LayoutEntry.row1Col3));
                        String row1Col4 = d.getString(d.getColumnIndexOrThrow(PuzzleDBContract.LayoutEntry.row1Col4));

                        String row2Col1 = d.getString(d.getColumnIndexOrThrow(PuzzleDBContract.LayoutEntry.row2Col1));
                        String row2Col2 = d.getString(d.getColumnIndexOrThrow(PuzzleDBContract.LayoutEntry.row2Col2));
                        String row2Col3 = d.getString(d.getColumnIndexOrThrow(PuzzleDBContract.LayoutEntry.row2Col3));
                        String row2Col4 = d.getString(d.getColumnIndexOrThrow(PuzzleDBContract.LayoutEntry.row2Col4));

                        String row3Col1 = d.getString(d.getColumnIndexOrThrow(PuzzleDBContract.LayoutEntry.row3Col1));
                        String row3Col2 = d.getString(d.getColumnIndexOrThrow(PuzzleDBContract.LayoutEntry.row3Col2));
                        String row3Col3 = d.getString(d.getColumnIndexOrThrow(PuzzleDBContract.LayoutEntry.row3Col3));
                        String row3Col4 = d.getString(d.getColumnIndexOrThrow(PuzzleDBContract.LayoutEntry.row3Col4));

                        String row4Col1 = d.getString(d.getColumnIndexOrThrow(PuzzleDBContract.LayoutEntry.row4Col1));
                        String row4Col2 = d.getString(d.getColumnIndexOrThrow(PuzzleDBContract.LayoutEntry.row4Col2));
                        String row4Col3 = d.getString(d.getColumnIndexOrThrow(PuzzleDBContract.LayoutEntry.row4Col3));
                        String row4Col4 = d.getString(d.getColumnIndexOrThrow(PuzzleDBContract.LayoutEntry.row4Col4));

                        Log.i("Info", "Found Layout");

                        String[] puzzleLayout1 = {row1Col1, row1Col2, row1Col3, row1Col4};
                        String[] puzzleLayout2 = {row2Col1, row2Col2, row2Col3, row2Col4};
                        String[] puzzleLayout3 = {row3Col1, row3Col2, row3Col3, row3Col4};
                        String[] puzzleLayout4 = {row4Col1, row4Col2, row4Col3, row4Col4};

                        String[] fullLayout = {row1Col1, row1Col2, row1Col3, row1Col4,
                                row2Col1, row2Col2, row2Col3, row2Col4,
                                row3Col1, row3Col2, row3Col3, row3Col4,
                                row4Col1, row4Col2, row4Col3, row4Col4};

                        Log.i("infor", "" + puzzleLayout4.length);
                        Intent intent = new Intent(getApplicationContext(), InGameActivity.class);

                        intent.putExtra((getString(R.string.puzzleLayoutCombo1)), puzzleLayout1);
                        intent.putExtra((getString(R.string.puzzleLayoutCombo2)), puzzleLayout2);
                        intent.putExtra((getString(R.string.puzzleLayoutCombo3)), puzzleLayout3);
                        intent.putExtra((getString(R.string.puzzleLayoutCombo4)), puzzleLayout4);
                        intent.putExtra((getString(R.string.fullPuzzleLayout)), fullLayout);
                        intent.putExtra((getString(R.string.puzzleImage)), picture);
                        intent.putExtra("puzzle", puzzle);
                        startActivity(intent);
                        break;
                    }
                } while (d.moveToNext());
                d.close();
            }
        } while (c.moveToNext());
        c.close();
    }

    public void show3x3(View view)
    {

    }


    private class downloadPuzzleDefinition extends AsyncTask<String, String, String>
    {

        protected String doInBackground(String... args)
        {

            downloadingInProgress = true;
            try
            {
                ContentValues values = new ContentValues();
                SQLiteDatabase db = m_DBHelper.getWritableDatabase();
                SQLiteDatabase dbRead = m_DBHelperRead.getReadableDatabase();

                InputStream stream = (InputStream) new URL(args[0]).getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                puzzle = args[0].substring(args[0].lastIndexOf('/') + 1);

                String line = "";
                String result = "";
                while (line != null)
                {
                    line = reader.readLine();
                    result += line;
                }

                JSONObject json = new JSONObject(result);
                picture = json.getString("PictureSet");
                Log.i("picture", picture);
                layout = json.getString("layout");
                Log.i("Layout", layout);

                String[] projection = {
                        //PuzzleDBContract.PuzzleEntry.COLUMN_PICTURE_SET_DEFINITION,
                        PuzzleDBContract.PuzzleEntry.COLUMN_LAYOUT_DEFINITION
                };
                Cursor c = dbRead.query(
                        PuzzleDBContract.PuzzleEntry.TABLE_NAME,
                        projection,
                        null, null, null, null, null
                );
                c.close();

                values.put(PuzzleDBContract.PuzzleEntry.COLUMN_LAYOUT_DEFINITION, layout);
                values.put(PuzzleDBContract.PuzzleEntry.COLUMN_PICTURE_SET_DEFINITION, picture);
                db.update(PuzzleDBContract.PuzzleEntry.TABLE_NAME, values, "Name =\"" + puzzle + "\"", null);

                ///Downloads the layout and stores it in a new table in database.
                ///Downloads the pictures and stores them on the device.
                String url = (getString(R.string.puzzlesLayoutURI)) + layout;
                stream = (InputStream) new URL(url).getContent();
                reader = new BufferedReader(new InputStreamReader(stream));
                line = "";
                String resultLayout = "";
                while (line != null)
                {
                    line = reader.readLine();
                    resultLayout += line;
                }
                Log.i("layout", "" + resultLayout);


                JSONObject jsonLayout = new JSONObject(resultLayout);
                JSONArray layoutArray = jsonLayout.getJSONArray("layout");

                formattedLayoutRow1 = null;
                formattedLayoutRow2 = null;
                formattedLayoutRow3 = null;
                formattedLayoutRow4 = null;
                String fullLayout = "";

                for (int i = 0; i < layoutArray.length(); ++i)
                {

                    String completeLayout = layoutArray.getString(i);

                    completeLayout = completeLayout.replaceAll((getString(R.string.regexReplaceMisc)), "");
                    //completeLayout = completeLayout.substring(0, completeLayout.length() - 1);
                    completeLayout = completeLayout.replace("]", ",");
                    fullLayout += completeLayout;
                    switch (i)
                    {
                        case 0:
                            formattedLayoutRow1 = completeLayout.split(",");
                            break;

                        case 1:
                            formattedLayoutRow2 = completeLayout.split(",");
                            break;

                        case 2:
                            formattedLayoutRow3 = completeLayout.split(",");
                            break;

                        case 3:
                            formattedLayoutRow4 = completeLayout.split(",");
                            break;
                    }
                }
                fullLayoutArray = fullLayout.split(",");

                values.clear();
                values.put(PuzzleDBContract.LayoutEntry.COLUMN_NAME_NAME, layout);
                db.insert(PuzzleDBContract.LayoutEntry.TABLE_NAME, null, values);

                for (int i = 0; i < formattedLayoutRow1.length; ++i)
                {
                    switch (i)
                    {
                        case 0:
                            values.put(PuzzleDBContract.LayoutEntry.row1Col1, formattedLayoutRow1[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 1:
                            values.put(PuzzleDBContract.LayoutEntry.row1Col2, formattedLayoutRow1[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 2:
                            values.put(PuzzleDBContract.LayoutEntry.row1Col3, formattedLayoutRow1[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 3:
                            values.put(PuzzleDBContract.LayoutEntry.row1Col4, formattedLayoutRow1[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;
                    }
                }
                for (int i = 0; i < formattedLayoutRow2.length; ++i)
                {
                    switch (i)
                    {
                        case 0:
                            values.put(PuzzleDBContract.LayoutEntry.row2Col1, formattedLayoutRow2[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 1:
                            values.put(PuzzleDBContract.LayoutEntry.row2Col2, formattedLayoutRow2[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 2:
                            values.put(PuzzleDBContract.LayoutEntry.row2Col3, formattedLayoutRow2[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 3:
                            values.put(PuzzleDBContract.LayoutEntry.row2Col4, formattedLayoutRow2[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;
                    }
                }
                for (int i = 0; i < formattedLayoutRow3.length; ++i)
                {
                    switch (i)
                    {
                        case 0:
                            values.put(PuzzleDBContract.LayoutEntry.row3Col1, formattedLayoutRow3[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 1:
                            values.put(PuzzleDBContract.LayoutEntry.row3Col2, formattedLayoutRow3[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 2:
                            values.put(PuzzleDBContract.LayoutEntry.row3Col3, formattedLayoutRow3[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 3:
                            values.put(PuzzleDBContract.LayoutEntry.row3Col4, formattedLayoutRow3[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;
                    }
                }
                if (formattedLayoutRow4 != null)
                {
                    for (int i = 0; i < formattedLayoutRow4.length; ++i)
                    {
                        switch (i)
                        {
                            case 0:
                                values.put(PuzzleDBContract.LayoutEntry.row4Col1, formattedLayoutRow4[i]);
                                db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                                break;

                            case 1:
                                values.put(PuzzleDBContract.LayoutEntry.row4Col2, formattedLayoutRow4[i]);
                                db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                                break;

                            case 2:
                                values.put(PuzzleDBContract.LayoutEntry.row4Col3, formattedLayoutRow4[i]);
                                db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                                break;

                            case 3:
                                values.put(PuzzleDBContract.LayoutEntry.row4Col4, formattedLayoutRow4[i]);
                                db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                                break;
                        }
                    }
                }

                Bitmap bitmap;
                for (int i = 0; i < fullLayoutArray.length; ++i)
                {
                    try
                    {
                        FileInputStream read = getApplicationContext().openFileInput(picture + "" + fullLayoutArray[i] + ".JPEG");
                        bitmap = BitmapFactory.decodeStream(read);
                        Log.i("Image", "Image Exists");
                    } catch (FileNotFoundException fileNotFound)
                    {
                        try
                        {
                            Log.i("Image", "Image does not exist");
                            String image = (getString(R.string.puzzlesImageURI)) + picture + "/" + fullLayoutArray[i];

                            if (fullLayoutArray[i].equals("empty"))
                            {
                                continue;
                            }
                            bitmap = BitmapFactory.decodeStream((InputStream) new URL(image).getContent());
                            FileOutputStream writer = null;
                            try
                            {
                                writer = getApplicationContext().openFileOutput(picture + "" + fullLayoutArray[i] + ".JPEG", Context.MODE_PRIVATE);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, writer);

                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            } finally
                            {
                                writer.close();
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return layout;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            Log.i("Downloading", "Complete");
            Intent intent = new Intent(getApplicationContext(), InGameActivity.class);
            intent.putExtra((getString(R.string.puzzleLayoutCombo1)), formattedLayoutRow1);
            intent.putExtra((getString(R.string.puzzleLayoutCombo2)), formattedLayoutRow2);
            intent.putExtra((getString(R.string.puzzleLayoutCombo3)), formattedLayoutRow3);
            intent.putExtra((getString(R.string.puzzleLayoutCombo4)), formattedLayoutRow4);
            intent.putExtra((getString(R.string.fullPuzzleLayout)), fullLayoutArray);
            intent.putExtra((getString(R.string.puzzleImage)), picture);
            intent.putExtra("puzzle", puzzle);
            startActivity(intent);
        }
    }
}

