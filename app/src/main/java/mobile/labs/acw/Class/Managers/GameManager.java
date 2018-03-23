package mobile.labs.acw.Class.Managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mobile.labs.acw.Activity.mainMenuActivity;
import mobile.labs.acw.Activity.playGameActivity;
import mobile.labs.acw.Activity.playingActivity;
import mobile.labs.acw.Class.PairsUtils;
import mobile.labs.acw.Class.Puzzle;
import mobile.labs.acw.Class.PuzzleImage;
import mobile.labs.acw.Class.PuzzlePictureSet;
import mobile.labs.acw.R;

/**
 * Created by Alexander on 26/02/2017.
 */

public class GameManager {
    //private static GameManager ourInstance = new GameManager();
    private static GameManager ourInstance;

    public static GameManager getInstance() {
        return ourInstance;
    }

    public enum GameMode {
        MODE_DRAG,
        MODE_CLICK
    }

    static GameMode m_GameMode = GameMode.MODE_CLICK;
    public static Boolean m_Init = false;
    public static GameMode getGameMode() {
        return m_GameMode;
    }
    public static int getGameModeInt(){
        if( m_GameMode == GameMode.MODE_CLICK)
            return 1;
        else
            return 2;
    }
    public static void setGameMode(GameMode gameMode){
        m_GameMode = gameMode;
    }
    public GameManager(Context context, Map<String, String> jsonList) {
        if( !m_Init) {
            m_Init = true;
            ProcessJsons(jsonList);

            // load existing puzzle scores
            for(Puzzle p : m_Puzzles) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(p.puzzle_ID), Context.MODE_PRIVATE); // get shared preference
                int highScore = sharedPreferences.getInt(playingActivity.HIGH_SCORE, 0); // retrieve highscore
                if( highScore > 0 )
                    p.puzzle_High_Score = highScore;
            }

        }
    }

    static List<Puzzle> m_Puzzles = new ArrayList<>();
    public static List<Puzzle> getPuzzleList() {
        return m_Puzzles;
    }
    public static Puzzle getPuzzleByID(int id){
        for( Puzzle puzzle : m_Puzzles ) { // iterate through all puzzles
            if ( puzzle.puzzle_ID == id ) // id matc
                return puzzle;
        }
        return null;
    }

    static List<PuzzlePictureSet> m_PictureSets = new ArrayList<>();
    public static List<PuzzlePictureSet> getPuzzlePictureSets() {
        return m_PictureSets;
    }
    public static Map<Integer, String> getPuzzlePictureSetImages(String pictureSetname){
        Map<Integer, String> pictureSetImages = new LinkedHashMap<>();
        for( PuzzlePictureSet set : m_PictureSets ){
            if( set.mSetName.equals(pictureSetname) )
                pictureSetImages = set.getImageSet();
        }
        return pictureSetImages;
    }

    // returns a list containg puzzle ID's
    public static List<Integer> getListOfPuzzleIDs() {
        List<Integer> puzzleIDlist = new ArrayList<>();
        for (Puzzle puzzle : m_Puzzles)
            puzzleIDlist.add(puzzle.puzzle_ID);

        return puzzleIDlist;
    }

    // processes a list of jsons
    public static void ProcessJsons(Map<String, String> json) {
        Object[] values = json.values().toArray(); // list of values
        Object[] keys = json.keySet().toArray(); // list of keys
        for (int i = 0; i < json.size(); i++) {
            String value = (String) values[i];
            String key = (String) keys[i];
            ProcessJsons(key, value);
        }
    }

    // process a json file by reading it sfile name
    public static void ProcessJsons(String key, String value) {
        if (key.contains("index")) { // check if the json we're reading is the index.json
            ReadPuzzlesFromIndex(key, value);
        } else if (key.contains("puzzle")) { // check if the json we're reading is a puzzle
            ReadPuzzleFromJson(key, value);
        } else { // must be a picture set
            ReadPictureSetFromJson(key, value);
        }
    }

    public static List<String> ReadPuzzlesFromIndex(String key, String value) {
        List<String> puzzleList = new ArrayList<>();
        try {
            JSONObject jObj = new JSONObject(value);
            JSONArray s = (JSONArray) jObj.get("PuzzleIndex");
            for (int i = 0; i < s.length(); i++) {
                String puzzleName = (String) s.get(i);
                puzzleList.add(puzzleName);
            }
            Log.i("[Downloaded]", "Dowloaded Puzzle Index");
        } catch (JSONException e) {
            Log.i("[Game Manager]", "Error while parsing index json");
        }
        return puzzleList;
    }

    // paramaters, filename and jsonData
    public static Puzzle ReadPuzzleFromJson(String key, String value) {
        Puzzle newPuzzle = null;
        try {
            JSONObject jObj = new JSONObject(value); // root
            JSONObject jObjContainer = new JSONObject(jObj.getString("layout")); // json array of Puzzle key

            int id = jObjContainer.getInt("Id"); // id in array
            String pictureSet = jObjContainer.getString("PictureSet"); // pictureset in array
            int rows = jObjContainer.getInt("Rows"); // rows in array
            List<Integer> layout = new ArrayList<>();

            JSONArray layout_IDs = (JSONArray) jObjContainer.get("Layout"); // int array in array
            for (int i = 0; i < layout_IDs.length(); i++) { // add ints from the array to the list
                layout.add(layout_IDs.getInt(i));
            }

            newPuzzle = new Puzzle(value, id, pictureSet, rows, layout);
            m_Puzzles.add(newPuzzle);

            Log.i("[Game Manager]", "Added New Puzzle, ID " + id);
        } catch (JSONException e) {
            Log.i("[Game Manager]", "Error while parsing puzzle json");
        }
        return newPuzzle;
    }

    public static PuzzlePictureSet ReadPictureSetFromJson(String key, String value) {
        List<PuzzleImage> imageList = ImageManager.getImageList();
        PuzzlePictureSet newSet = null;
        try {
            JSONObject jOb = new JSONObject(value);
            JSONArray jArray = new JSONArray(jOb.getString("PictureFiles"));

            newSet = new PuzzlePictureSet(key); // new pictureset
            for (int i = 0; i < jArray.length(); i++) { // add all images to the pictureset
                newSet.addImage(jArray.getString(i));
            }
            m_PictureSets.add(newSet); //add set to list

            Log.i("[Game Manager]", "Added New PictureSet, " + key);
        } catch (JSONException e) {
            Log.i("[Game Manager]", "Error while reading pictureset from json");
        }
        return newSet;
    }
}