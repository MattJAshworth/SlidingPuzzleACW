package com.mattjamesashworth.android.slidingacw.Class.Handlers;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by mattjashworth on 21/03/2018.
 */

public class JsonHandler {
    //private static JsonHandler ourInstance = new JsonHandler(); -- self initialising singleton
    private static JsonHandler ourInstance;

    public static JsonHandler getInstance() {
        return ourInstance;
    }
    public static Boolean m_Init = false;
    public JsonHandler(View view) {
        if( !m_Init ) {
            m_Init = true;
            ourInstance = this;
            LoadExistingFiles(view);
        }
    }

    public JsonHandler(Context context){
        if( !m_Init ) {
            m_Init = true;
            ourInstance = this;
            LoadExistingFiles(context);
        }
    }

    // <FILENAME, JSONDATA>
    public static Map<String, String> m_Jsons = new LinkedHashMap<>();
    public static Map<String, String> getJsonList() {
        return m_Jsons;
    }

    public static Boolean containsJSONByFileName(String fileName){
        return m_Jsons.containsKey(fileName);
    }

    public static void AddJson(String fileName, String data){
        if( !m_Jsons.containsKey(fileName) )
            m_Jsons.put(fileName, data);
        else
            Log.i("[Json Manager]", fileName+" already exist");
    }

    public static void SaveJsonToStorage(Context context, String fileName, String data){
        try{
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            AddJson(fileName, data);
            Log.i("[Json Manager]", "Wrote "+fileName+" to internal storage");
        }catch(IOException e){
            Log.i("[Json Manager]", "Erorr while saving json to internal storage");
        }
    }

    // this method loads in all existing puzzles by checking the internal storage for any files with "puzzle" in its name
    // loaded in using view as paramater
    private static void LoadExistingFiles(View view) {
        File[] files = view.getContext().getFilesDir().listFiles();
        for(File file : files){
            if( !file.getName().contains(".jpg") ){ // check if file has "puzzle"
                String fileName = file.getName(); // store name

                String JSONData = "";
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file)); // file stream reader

                    String line = "";
                    while((line = reader.readLine()) != null){ // readline untill the end
                        JSONData += line; // keep adding each line ot the jsonData
                    }
                    reader.close();
                }
                catch (FileNotFoundException e){}
                catch (IOException e){
                }

                AddJson(fileName, JSONData);
                Log.i("[Json Manager]", "Found Json: "+fileName);
            }
        }
    }

    // this method loads in all existing puzzles by checking the internal storage for any files with "puzzle" in its name
    // loaded in using context as paramater
    private static void LoadExistingFiles(Context context) {
        File[] files = context.getFilesDir().listFiles();
        for(File file : files){
            if( !file.getName().contains(".jpg") ){ // check if file has "puzzle"
                String fileName = file.getName(); // store name

                String JSONData = "";
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file)); // file stream reader

                    String line = "";
                    while((line = reader.readLine()) != null){ // readline untill the end
                        JSONData += line; // keep adding each line ot the jsonData
                    }
                    reader.close();
                }
                catch (FileNotFoundException e){}
                catch (IOException e){
                }

                AddJson(fileName, JSONData);
                Log.i("[Json Manager]", "Found Json: "+fileName);
            }
        }
    }
}

    /*
    // SOURCE: http://stackoverflow.com/questions/13581997/how-get-value-from-linkedhashmap-based-on-index-not-on-key
    public void onJsonDownloaded() {
        List<String> values = new ArrayList<>(m_Json.values());
        List<String> keys = new ArrayList<>(m_Json.keySet());
        String value = values.get(m_Json.size() - 1);
        String key = keys.get(m_Json.size() - 1);

        try {
            List<String> m_Puzzle_IDs = new ArrayList<>();
            if (key.contains("index")) {
                JSONObject jObj = new JSONObject(value);
                JSONArray s = (JSONArray) jObj.get("PuzzleIndex");
                for (int i = 0; i < s.length(); i++) {
                    String puzzleName = (String) s.get(i);
                    if (!m_Puzzle_IDs.contains(puzzleName) && !ifPuzzleExist(puzzleName)) { // if file was not previously downloaded
                        m_Downloadble_Puzzles_IDs.add(SlidingUtils.StripPuzzleDownToID(puzzleName, 5));
                        UpdateListViews((String) s.get(i), m_Downloadble_Puzzles_IDs);
                    }
                }
                Log.i("[Downloaded]", "Dowloaded Puzzle Index");
            } else if (key.contains("pictureset")) { // download images from picture set
                JSONObject jOb = new JSONObject(value);
                JSONArray jArray = new JSONArray(jOb.getString("PictureFiles"));

                List<String> image_to_download = new ArrayList<>();
                for (int i = 0; i < jArray.length(); i++) {
                    image_to_download.add(jArray.getString(i));
                }

                // filter images who were previously downloaded
                for (String s : image_to_download) {
                    if (m_Images.containsKey(s)) { // check downloaded image list, if value was found we dont want to redownload
                        image_to_download.remove(s); // remove from download list
                        Log.i("[Removed]", s + " from images to download, already exist");
                    }
                }

                //m_PictureSets.put( key.substring(PUZZLE_PICTURESET.length(), key.length()),image_to_download );
                DownloadImages(image_to_download);
            } else { // puzzle was downloaded
                JSONObject jObj = new JSONObject(value); // root
                JSONObject jObjContainer = new JSONObject(jObj.getString("Puzzle")); // json array of Puzzle key

                int id = jObjContainer.getInt("Id"); // id in array
                String pictureSet = jObjContainer.getString("PictureSet"); // pictureset in array
                int rows = jObjContainer.getInt("Rows"); // rows in array
                List<Integer> layout = new ArrayList<>();

                JSONArray layout_IDs = (JSONArray) jObjContainer.get("Layout"); // int array in array
                for (int i = 0; i < layout_IDs.length(); i++) { // add ints from the array to the list
                    layout.add(layout_IDs.getInt(i));
                }

                // download picture set
                DownloadJson(pictureSet, PrefaceActivity.PUZZLE_DIRECTORY + PrefaceActivity.PUZZLE_PICTURESET);

                Puzzle new_Puzzle = new Puzzle(value, id, pictureSet, rows, layout);
                m_Puzzles.add(new_Puzzle);
                SavePuzzleData(new_Puzzle);
                Log.i("[Downloaded]", "Added New Puzzle, ID " + id);
            }
        } catch (JSONException e) {
            Log.i("My Error", e.getMessage());
        }
    }
*/