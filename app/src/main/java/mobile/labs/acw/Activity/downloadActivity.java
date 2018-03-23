package mobile.labs.acw.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mobile.labs.acw.Class.Managers.GameManager;
import mobile.labs.acw.Class.Managers.ImageManager;
import mobile.labs.acw.Class.Managers.JsonManager;
import mobile.labs.acw.Class.PairsUtils;
import mobile.labs.acw.Class.Puzzle;
import mobile.labs.acw.Class.PuzzleImage;
import mobile.labs.acw.Class.PuzzlePictureSet;
import mobile.labs.acw.R;

/**
 * Created by Alexander on 25/02/2017.
 */

public class downloadActivity extends AppCompatActivity{

    public List<Integer> m_Installed_Puzzles_IDs;
    public List<Integer> m_Downloadble_Puzzles_IDs;

    private List<AsyncTask<String, String, String>> m_Json_Downloader;
    private View m_View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        m_View = this.findViewById(android.R.id.content); // store root view

        Button backButton = (Button)findViewById(R.id.button_download_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        m_Installed_Puzzles_IDs = new ArrayList<>();
        m_Downloadble_Puzzles_IDs = new ArrayList<>();
        m_Json_Downloader = new ArrayList<>();

        checkExistingPuzzles();
        DownloadJson("index.json", "");
    }

    public void onIndexDownloaded(String data){
        if( m_Json_Downloader.size() == 0 )
            toggleBackButton(true);

        List<String> puzzleList = GameManager.ReadPuzzlesFromIndex("index.json", data);

        List<Integer> puzzleIDList = new ArrayList<>();
        for(String s : puzzleList){ // remove all puzzles that exist locally
            if( !JsonManager.containsJSONByFileName(s) ){
                puzzleIDList.add( Integer.parseInt(PairsUtils.StripPuzzleDownToID(s, 5)) );
            }
        }

        m_Downloadble_Puzzles_IDs.addAll( puzzleIDList );
        updateListViews(puzzleIDList);
    }

    public void checkExistingPuzzles(){
        m_Installed_Puzzles_IDs.addAll( GameManager.getListOfPuzzleIDs() );

        ListView listView = (ListView)findViewById(R.id.installed_puzzles);
        updateListView(listView, m_Installed_Puzzles_IDs);
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
        m_SelectedPuzzleID = m_Downloadble_Puzzles_IDs.get(position);
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
        m_Downloadble_Puzzles_IDs.remove(m_SelectedPuzzleIDIndexInList); // remove puzzle from downloadable
        updateListView(downloadableListView, m_Downloadble_Puzzles_IDs);

        // download file
        DownloadJson("puzzle"+m_SelectedPuzzleID+".json", mainMenuActivity.PUZZLE_FILES);

        m_Installed_Puzzles_IDs.add( m_SelectedPuzzleID ); // add puzzle to installed
        ListView installed_ListView = (ListView)findViewById(R.id.installed_puzzles);
        updateListView(installed_ListView, m_Installed_Puzzles_IDs);
        toggleBackButton(false);
    }

    public void toggleBackButton(Boolean toggle){
        Button backButton = (Button)findViewById(R.id.button_download_back);
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
                //String newPictureSet = newPuzzle.puzzle_PictureSet;
                String newPictureSet = "Apple";
                DownloadJson(newPictureSet, mainMenuActivity.PUZZLE_PICTURESET);
                Toast.makeText(m_View.getContext(), getResources().getString(R.string.puzzle_download_complete), Toast.LENGTH_SHORT);
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