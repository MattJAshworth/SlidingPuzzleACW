package com.mattjamesashworth.android.slidingacw.Class.Managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.mattjamesashworth.android.slidingacw.Class.SlidingUtils;
import com.mattjamesashworth.android.slidingacw.Class.PuzzleImage;

/**
 * Created by mattjashworth on 21/03/2018.
 */

public class ImageManager {
    //private static ImageManager ourInstance = new ImageManager(); -- self initialising singleton
    private static ImageManager ourInstance;

    public static ImageManager getInstance() {
        return ourInstance;
    }
    public static Boolean m_Init = false;
    public ImageManager(View view) {
        if( !m_Init ) {
            m_Init = true;
            ourInstance = this;
            LoadExistingImages(view);
        }
    }
    public ImageManager(Context context){
        if( !m_Init ) {
            m_Init = true;
            ourInstance = this;
            LoadExistingImages(context);
        }
    }

   // static Map<String, Bitmap> m_Images = new HashMap<>();
    private static List<PuzzleImage> m_Images = new ArrayList<>();
    public static List<PuzzleImage> getImageList(){
        return m_Images;
    }

    public static Bitmap getImageByName(String imageName){
        for(PuzzleImage image : m_Images){
            if( image.getImageName().equals(imageName) )
                return image.getImage();
        }
        return null;
    }
    public static Bitmap getImageByFileName(String fileName){
        for(PuzzleImage image : m_Images){
            if( image.getFileName().equals(fileName) )
                return image.getImage();
        }
        return null;
    }
    public static Boolean containsImageByFileName(String fileName){
        for(PuzzleImage image : m_Images){
            if( image.getFileName().equals(fileName) )
                return true;
        }
        return false;
    }
    public static Boolean containsImageByName(String imageName){
        for(PuzzleImage image : m_Images){
            if( image.getImageName().equals(imageName) )
                return true;
        }
        return false;
    }

    // add image to list, provided with the name of the file , name of the image and the image data
    public static void AddImage(String fileName, String imageName, Bitmap image) {
        if ( !containsImageByFileName(imageName) ) { // if image doesnt exist in list
            PuzzleImage newImage = new PuzzleImage(fileName, imageName, image); // create new puzzle
            m_Images.add(newImage); // add puzzle to list
            Log.i("[Image Manager]", "Success: Added image " + imageName + " to list");
        }else{
            Log.i("[Image Manager]", "Image already exist: "+imageName);
        }
    }

    // this method loads in all existing images by checking the internal storage for any files with the ".jpg" extension
    // loaded in using view as paramater
    private static void LoadExistingImages(View view) {
        try {
            File[] all_Files = view.getContext().getFilesDir().listFiles();
            for (File file : all_Files) {
                if (file.getName().contains(".jpg")) {
                    int extensionLength = 4; // .jpg = 4 chars
                    String fileName = file.getName(); // original file name
                    if( !containsImageByFileName(fileName) ) { // if the list does not have
                        String imageName = fileName.substring(0, fileName.length()-extensionLength); // image name (not including extension)
                        Bitmap image = BitmapFactory.decodeStream(new FileInputStream(file)); // load file data into bitmap

                        AddImage(fileName, imageName, image); // add image to list
                        Log.i("[Image Manager]", "Success: Image loaded: " + imageName);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Log.i("[Image Manager]", "Error: File not found " + e.getMessage());
        }
    }

    // this method loads in all existing images by checking the internal storage for any files with the ".jpg" extension
    // loaded using context as paramater
    private static void LoadExistingImages(Context context) {
        try {
            File[] all_Files = context.getFilesDir().listFiles();
            for (File file : all_Files) { // iterate through all files
                if (file.getName().contains(".jpg")) {
                    int extensionLength = 4; // .jpg = 4 chars
                    String fileName = file.getName(); // original file name
                    if( !containsImageByFileName(fileName) ) { // if the list does not have
                        String imageName = fileName.substring(0, fileName.length()-extensionLength); // image name (not including extension)
                        Bitmap image = BitmapFactory.decodeStream(new FileInputStream(file)); // load file data into bitmap

                        AddImage(fileName, imageName, image); // add image to list
                        Log.i("[Image Manager]", "Success: Image loaded: " + imageName);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Log.i("[Image Manager]", "Error: File not found " + e.getMessage());
        }
    }

    private static List<AsyncTask<String, String, Bitmap>> m_Image_Downloader = new ArrayList<>();
    public static boolean AllDownloadersFinished(){ // returns whether all downloaders are complted
        return ( m_Image_Downloader.size() <= 0);
    }

    // method called by the AsyncTask when an image has succesfully been downloaded
    public static void onImageDownloadComplete(String fileName, Bitmap image, AsyncTask<String, String, Bitmap> task){
        String imageName = SlidingUtils.clipExtension(fileName, 4); // get image name
        AddImage(fileName, imageName, image); // add image
        m_Image_Downloader.remove(task); // remove task from downloader list
    }

    private static final String PUZZLE_IMAGE_DOWNLOAD_DIRECTORY = "http://www.simongrey.net/08027/slidingPuzzleAcw/images/";

    // method called to download images, provided with their context and the filename of the image
    public static void DownloadImage(Context context, String fileName) {
        if (!containsImageByFileName(fileName))
            m_Image_Downloader.add(new downloadImage(context).execute(fileName, PUZZLE_IMAGE_DOWNLOAD_DIRECTORY));
        else
            Log.i("[Image Manager]", "Image already exist, aborting download");
    }

    // class downloads bitmap data from server using the provided context
    private static class downloadImage extends AsyncTask<String, String, Bitmap> {
        private Context mContext;

        public downloadImage(Context context){
            mContext = context;
        }

        @Override
        protected Bitmap doInBackground(String... args) {
            Bitmap bitmap = null;
            String downloadPath = args[1];
            String fileName = args[0];
            if( !fileName.contains(".jpg") ) { // simple error check to ensure filename has the correct extension
                Log.i("[Downloader Error]", "Invalid file type");
                return null;
            }

            try {
                String url = downloadPath + fileName;
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                FileOutputStream writer = null;
                try {
                    writer = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, writer);

                    onImageDownloadComplete(fileName, bitmap, this); // tell manager update is finished

                    Log.i("Image DL:", fileName);
                } catch (Exception e) {
                    if (e.getMessage() != null)
                        Log.i("My Error1", e.getMessage());
                } finally {
                    writer.close();
                }
            } catch (IOException e) {
                Log.i("[Downloader Error]", e.getMessage());
            }
            return bitmap;
        }

      /*  protected void onPostExecute(Bitmap image) {
            onBitmapDownloaded();
        }*/
    }

}