package com.mattjamesashworth.android.slidingacw.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.mattjamesashworth.android.slidingacw.Class.Managers.GameManager;
import com.mattjamesashworth.android.slidingacw.Class.Managers.ImageManager;
import com.mattjamesashworth.android.slidingacw.Class.Managers.JsonManager;
import com.mattjamesashworth.android.slidingacw.Class.SlidingUtils;
import com.mattjamesashworth.android.slidingacw.Class.Puzzle;
import com.mattjamesashworth.android.slidingacw.Class.PuzzlePictureSet;
import com.mattjamesashworth.android.slidingacw.R;

/**
 * Created by mattjashworth on 21/03/2018.
 */

public class downloadActivity extends AppCompatActivity{

    public List<Integer> installed_Puzzles;
    public List<Integer> available_Puzzles;

    private List<AsyncTask<String, String, String>> m_Json_Downloader;
    private View m_View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        m_View = this.findViewById(android.R.id.content); // store root view

        Button backButton = (Button)findViewById(R.id.btn_Back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        installed_Puzzles = new ArrayList<>();
        available_Puzzles = new ArrayList<>();
        m_Json_Downloader = new ArrayList<>();

        checkExistingPuzzles();
        DownloadJson("index.json", "");

        //Background Transitions
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.frags);
        AnimationDrawable animationDrawable;
        animationDrawable =(AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();


    }

    public void onIndexDownloaded(String data){
        if( m_Json_Downloader.size() == 0 )
            toggleBackButton(true);

        List<String> puzzleList = GameManager.ReadPuzzlesFromIndex("index.json", data);

        List<Integer> puzzleIDList = new ArrayList<>();
        for(String s : puzzleList){ // remove all puzzles that exist locally
            if( !JsonManager.containsJSONByFileName(s) ){
                puzzleIDList.add( Integer.parseInt(SlidingUtils.StripPuzzleDownToID(s, 5)) );
            }
        }

        available_Puzzles.addAll( puzzleIDList );
        updateListViews(puzzleIDList);
    }

    public void checkExistingPuzzles(){
        installed_Puzzles.addAll( GameManager.getListOfPuzzleIDs() );

        ListView listView = (ListView)findViewById(R.id.installed_puzzles);
        updateListView(listView, installed_Puzzles);
    }

    public void downloadImages(String[] imageNames) {
        for (String s : imageNames) {
            if( !ImageManager.containsImageByFileName(s) ) // do not download if already exist
                ImageManager.DownloadImage(getApplicationContext(), s);
        }
    }

    public void downloadImages(List<String> imageNames){
        for (String s : imageNames) {
            if( !ImageManager.containsImageByFileName(s) ) // do not download if already exist
                ImageManager.DownloadImage(getApplicationContext(), s);
        }
    }

    public void updateListViews(List<Integer> puzzleList) {

        // update listview adapter
        ListView listView = (ListView) findViewById(R.id.download_puzzles);
        updateListView(listView, puzzleList);

        // set listeners
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.isEnabled())
                    buttonClicked(position);
            }
        });
    }

    public void updateListView(ListView listView, List<Integer> puzzles){
        List<String> puzzleStringIDlist = new ArrayList<>();
        for(Integer id : puzzles) // convert list to strings
            puzzleStringIDlist.add( String.valueOf(id) );

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, puzzleStringIDlist);
        listView.setAdapter(arrayAdapter);
    }

    private int m_SelectedPuzzleID;
    private int m_SelectedPuzzleIDIndexInList;
    public void buttonClicked(int position){
        m_SelectedPuzzleID = available_Puzzles.get(position);
        m_SelectedPuzzleIDIndexInList = position;

        new AlertDialog.Builder(m_View.getContext()) // set alert dialog for downloading puzzle
                .setTitle(getResources().getString(R.string.download)+" "+getResources().getString(R.string.puzzle)+" "+m_SelectedPuzzleID) // title
                .setMessage( getResources().getString(R.string.dialog_description)+m_SelectedPuzzleID) // description
                .setPositiveButton(getResources().getString(R.string.download), new DialogInterface.OnClickListener() { // button on right side
                    public void onClick(DialogInterface dialog, int which) {
                        downloadPuzzle();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() { // buton on left side
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show(); // show dialog
    }

    public void downloadPuzzle(){
        ListView downloadableListView = (ListView)findViewById(R.id.download_puzzles); // listview of download puzzles
       // String puzzle_id = (String)((TextView)downloadableListView.getChildAt(m_SelectedPuzzleID)).getText(); // retrieve and store puzzle ID
        available_Puzzles.remove(m_SelectedPuzzleIDIndexInList); // remove puzzle from downloadable
        updateListView(downloadableListView, available_Puzzles);

        // download file
        DownloadJson("puzzle"+m_SelectedPuzzleID+".json", mainMenuActivity.PUZZLE_FILES);

        installed_Puzzles.add( m_SelectedPuzzleID ); // add puzzle to installed
        ListView installed_ListView = (ListView)findViewById(R.id.installed_puzzles);
        updateListView(installed_ListView, installed_Puzzles);
        toggleBackButton(false);
    }

    public void toggleBackButton(Boolean toggle){
        Button backButton = (Button)findViewById(R.id.btn_Back);
        backButton.setEnabled(toggle);
    }

    // method called by the AsyncTask when an image has succesfully been downloaded
    public void onJsonDownloadComplete(String fileName, String data, AsyncTask<String, String, String> task){
        toggleBackButton(true);
        if( fileName.contains("index")){
            onIndexDownloaded(data);
        }else {
            JsonManager.SaveJsonToStorage(getApplicationContext(), fileName, data); // save and add the json to internal storage
            if( fileName.contains("puzzle") ) {
                Puzzle newPuzzle = GameManager.ReadPuzzleFromJson(fileName, data); // read in new puzzle to the manager
                if (newPuzzle != null) {
                    String newPictureSet = newPuzzle.puzzle_PictureSet;
                    DownloadJson(newPictureSet, mainMenuActivity.PUZZLE_PICTURESET);
                    Toast.makeText(m_View.getContext(), getResources().getString(R.string.puzzle_download_complete), Toast.LENGTH_SHORT);
                }
            }
            else{ // must be a pictureset
                PuzzlePictureSet newSet = GameManager.ReadPictureSetFromJson(fileName, data);
                downloadImages(newSet.getImageNames());
                Toast.makeText(m_View.getContext(), getResources().getString(R.string.images_download_complete), Toast.LENGTH_SHORT);
            }
        }
        m_Json_Downloader.remove(task); // remove task from downloader list
        Log.i("[Download Activity]", "Finished downloading: "+fileName);
    }

    // method called to download images, provided with their context and the filename of the image
    public void DownloadJson(String fileName, String url) {
        if ( !JsonManager.m_Jsons.containsKey(fileName) )
            m_Json_Downloader.add(new downloadJson().execute(fileName, mainMenuActivity.PUZZLE_DIRECTORY + url));
        else
            Log.i("[Download Activity]", "Json already exist, aborting download");
    }

    // SOURCE: https://developer.android.com/reference/java/net/HttpURLConnection.html
    // SOURCE: http://stackoverflow.com/questions/2922210/reading-text-file-from-server-on-android
    private class downloadJson extends AsyncTask<String, String, String> {

        // values for onPostExecute
        String m_FileName;
        String m_JSON;

        protected String doInBackground(String... args) {
            String json = "";
            String directory = args[1];
            String fileName = args[0];
            try {
                URL url = new URL(directory+fileName);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                    // read data into string untill readline is null
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        json += line;
                    }
                    in.close();

                    m_FileName = args[0];
                    m_JSON = json;
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
            }
            //catch(MalformedURLException e) {}
            return json;
        }

        protected void onPostExecute(String json) {  onJsonDownloadComplete(m_FileName, m_JSON, this);}
    }
}