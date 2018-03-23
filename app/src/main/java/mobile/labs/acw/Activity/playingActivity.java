package mobile.labs.acw.Activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mobile.labs.acw.Class.Managers.GameManager;
import mobile.labs.acw.Class.Managers.ImageManager;
import mobile.labs.acw.Class.PairsUtils;
import mobile.labs.acw.Class.Puzzle;
import mobile.labs.acw.Class.PuzzlePictureSet;
import mobile.labs.acw.Fragments.puzzleCardFragment;
import mobile.labs.acw.R;

/**
 * Created by Alexander on 22/02/2017.
 */

    // Todo: Drag Mode?

public class playingActivity extends AppCompatActivity {

    Puzzle m_ActivePuzzle;
    int m_CardsLeft = 0;
    int m_Score = 0;
    int m_HighScore = 0;

    int m_ScoreMultiplier = 1;
    final int SCORE_PER_GUESS = 100;
    final int SCORE_MULTIPLIER_INCREMENT = 1;

    int m_Rows = 0;
    int m_Columns = 0;

    int m_TotalCards = 0;
    int m_PuzzleID = 0;
    Map<Integer, String> m_ImageList;
    List<Integer> m_Layout;
    View m_View;
    private puzzleCardFragment[][] m_Grid;
    boolean mSetupCompleted = false;
    List<puzzleCardFragment> m_Fragments = new ArrayList<>();

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
        }
    }

    // SOURCE: http://stackoverflow.com/questions/13022677/save-state-of-activity-when-orientation-changes-android
    private static final String SCORE_TEXT_VALUE = "scoreValue";
    private static final String SCORE_MULTIPLIER_TEXT_VALUE = "scoreMultiplierValue";
    private static final String CARDS_LEFT_TEXT_VALUE = "textLeftValue";
    private static final String FRAGMENT_TEXT_VALUE = "fragmentValue";

    // data persistency across orientation changes
    @Override
    protected void onSaveInstanceState (Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            savedInstanceState.putInt(SCORE_TEXT_VALUE, m_Score);
            savedInstanceState.putInt(SCORE_MULTIPLIER_TEXT_VALUE, m_ScoreMultiplier);
            savedInstanceState.putInt(CARDS_LEFT_TEXT_VALUE, m_CardsLeft);
            if(lastFragment != null)
                savedInstanceState.putInt(FRAGMENT_TEXT_VALUE, lastFragment.m_FragmentID);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        m_View = this.findViewById(android.R.id.content); // store root view

        Bundle extras = getIntent().getExtras(); // get intent to retrieve extras from
        m_PuzzleID = extras.getInt("Puzzle_ID"); // get extra housing the puzzle ID

        m_ActivePuzzle = GameManager.getPuzzleByID(m_PuzzleID);
        setTextViewText(R.id.text_playing_title, getResources().getString(R.string.puzzle) + " " + String.valueOf(m_ActivePuzzle.puzzle_ID));

        m_CardsLeft = m_ActivePuzzle.puzzle_Layout.size();
        m_ImageList = GameManager.getPuzzlePictureSetImages(m_ActivePuzzle.puzzle_PictureSet);
        m_TotalCards = m_ActivePuzzle.puzzle_Layout.size();
        m_Layout = m_ActivePuzzle.puzzle_Layout;
        m_Rows = m_ActivePuzzle.puzzle_Rows;
        m_Columns = m_Layout.size() / m_Rows;
        m_Grid = new puzzleCardFragment[m_Rows][m_TotalCards / m_Rows]; // create grid

        if(savedInstanceState != null) { // recover saved state
            m_Score = savedInstanceState.getInt(SCORE_TEXT_VALUE);
            m_ScoreMultiplier = savedInstanceState.getInt(SCORE_MULTIPLIER_TEXT_VALUE);
            m_CardsLeft = savedInstanceState.getInt(CARDS_LEFT_TEXT_VALUE);
            setTextViewText(R.id.text_score, getResources().getString(R.string.score) + ": " + String.valueOf(m_Score));

            if (savedInstanceState.containsKey(FRAGMENT_TEXT_VALUE)) { // restore pending card fragment
                FragmentManager fragmentManager = getFragmentManager();
                puzzleCardFragment fragment = (puzzleCardFragment) fragmentManager.findFragmentByTag( String.valueOf(savedInstanceState.getInt( FRAGMENT_TEXT_VALUE)) );
                fragment.activateListener();
                lastFragment = fragment;
                lastImage = fragment.m_ImageName;
            }
        }

        try {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(String.valueOf(m_PuzzleID), MODE_PRIVATE);
            m_HighScore = sharedPreferences.getInt(HIGH_SCORE, 0);
            setTextViewText(R.id.text_high_score, getResources().getString(R.string.high_score) + ": " + String.valueOf(m_HighScore));
        } catch (NullPointerException e) {
            Log.i("Error", "while reading highscore");
        }

        // setup exit button listener
        Button exitBut = (Button) findViewById(R.id.back_button);
        exitBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final GridLayout grid = (GridLayout) findViewById(R.id.grid);
        if (!mSetupCompleted) {
            grid.setColumnCount(m_Columns); // set columns
            grid.setRowCount(m_Rows); // set rows
            mSetupCompleted = true;
        }
        // MODIFIED SOURCE: http://stackoverflow.com/questions/7733813/how-can-you-tell-when-a-layout-has-been-drawn
        ViewTreeObserver vto = grid.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                grid.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = grid.getMeasuredWidth();
                int height = grid.getMeasuredHeight();

                populateWithCards(width, height);
                // testPopulateWithCards(width, height);
            }
        });

    }

    public void updateScore() {
        m_Score += SCORE_PER_GUESS * m_ScoreMultiplier; // add to score
        m_ScoreMultiplier += SCORE_MULTIPLIER_INCREMENT; // increment multiplier

        setTextViewText(R.id.text_score, getResources().getString(R.string.score) + ": " + String.valueOf(m_Score));
    }

    public void resetScoreMultiplier() {
        m_ScoreMultiplier = 1;
    }

    // Modified runnable from source: http://stackoverflow.com/questions/9123272/is-there-a-way-to-pass-parameters-to-a-runnable
    // using delayed runnable source: http://stackoverflow.com/questions/3072173/how-to-call-a-method-after-a-delay-in-android
    public class HideCardRunnable implements Runnable {
        private puzzleCardFragment m_Fragment1;
        private puzzleCardFragment m_Fragment2;
        private Boolean m_HideCard;
        private playingActivity m_Activity;

        public HideCardRunnable(playingActivity activity, puzzleCardFragment fragment, puzzleCardFragment fragment2, Boolean hide) { // runnable constructor
            m_HideCard = hide;
            m_Fragment1 = fragment;
            m_Fragment2 = fragment2;
            m_Activity = activity;
        }

        @Override
        public void run() { // runnable calls run automatically, in this case we call the fragment hideImage
            m_Fragment1.setImage(m_HideCard, false);
            m_Fragment2.setImage(m_HideCard, false);
            m_Activity.unlock();
        }
    }

    private puzzleCardFragment lastFragment = null;
    private String lastImage = null;
    private static final int TIME_TILL_CARD_HIDES = 1000;
    private boolean m_Locked = false;

    public void unlock() {
        m_Locked = false;
    }

    public static String HIGH_SCORE = "_HighScore";

    public void puzzleCompleted() {
        if (m_Score >= m_HighScore) {
            m_HighScore = m_Score;
            SharedPreferences.Editor sharedPreferences = getApplicationContext().getSharedPreferences(String.valueOf(m_PuzzleID), MODE_PRIVATE).edit();
            sharedPreferences.putInt(HIGH_SCORE, m_HighScore);
            sharedPreferences.apply();

            GameManager.getPuzzleByID(m_PuzzleID).puzzle_High_Score = m_Score;
            Toast.makeText(this, getResources().getString(R.string.new_highscore), Toast.LENGTH_LONG).show();
        }

        new AlertDialog.Builder(m_View.getContext()) // set alert dialog for downloading puzzle
                .setTitle(getResources().getString(R.string.puzzle) + " "+String.valueOf(m_PuzzleID)) // title
                .setMessage(getResources().getString(R.string.puzzle_completed)) // description
                .setPositiveButton(getResources().getString(R.string.puzzle_continue), new DialogInterface.OnClickListener() { // button on right side
                    public void onClick(DialogInterface dialog, int which) {
                        completePuzzle();
                    }
                })
                .show(); // show dialog
    }

    public void completePuzzle(){
        Intent intent = new Intent(m_View.getContext(), playGameActivity.class);
        startActivity(intent);

        finish();
    }

    public void onCardSelected(puzzleCardFragment pFragment, String pImageName) {
        if (m_Locked)
            return;

        if (lastImage == null) {
            pFragment.setImage(true, true);
            lastImage = pImageName;
            lastFragment = pFragment;
        } else {
            if (pFragment == lastFragment) // hide card if player wants to de select it
            {
                Handler handler = new Handler();
                handler.postDelayed(new HideCardRunnable(this, lastFragment, pFragment, false), TIME_TILL_CARD_HIDES);
                lastImage = null;
                lastFragment = null;

                m_Locked = true;
                return;
            }

            pFragment.setImage(true, false);
            if (pImageName == lastImage) {
                pFragment.removeListener();
                lastFragment.removeListener();
                m_CardsLeft -= 2;
                updateScore();

                if (m_CardsLeft == 0)
                    puzzleCompleted();
            } else { // reset score multiplier and images

                // hide card delayed
                m_Locked = true;
                Handler handler = new Handler();
                handler.postDelayed(new HideCardRunnable(this, lastFragment, pFragment, false), TIME_TILL_CARD_HIDES);

                //   lastFragment.setImage(false);
                //  pFragment.setImage(false);
                resetScoreMultiplier();
            }

            lastImage = null;
            lastFragment = null;
        }
    }

    public void setTextViewText(int layoutID, String text) {
        TextView textView = (TextView) findViewById(layoutID);
        textView.setText(text);
    }

    public void populateWithCards(int width, int height) {
        FragmentManager fragmentManager = getFragmentManager(); // manager
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // start transaction for adding fragments to the manager

        int originalImageSize = 256;
        int imageSize = width;

        // SOURCE: http://stackoverflow.com/questions/3663665/how-can-i-get-the-current-screen-orientation
        if( getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ) // if landscape, use height instead
             imageSize = height;

        if( m_Rows > m_Columns ) // use m_rows to calculate image size if its bigger (needs more space)
            imageSize = imageSize / m_Rows;
        else// use m_rows to calculate image size if its bigger (needs more space)
            imageSize = imageSize / m_Columns;

        for (int i = 0; i < m_TotalCards; i++) { // create fragments

            puzzleCardFragment fragment = (puzzleCardFragment) fragmentManager.findFragmentByTag(String.valueOf(i));
            if (fragment != null) { // if not null, fragment already exist - continue to next
                fragment.restoreFragment(imageSize); // if fragment exist it must be orientation change
                continue;
            }else // if not instantiated previously, create new fragment
                fragment = new puzzleCardFragment();

            Bundle data = new Bundle(); // pass relevant data to the fragment
            data.putInt("FragmentID", i);
            data.putString("ImageName", m_ImageList.get(m_Layout.get(i) - 1));
            data.putInt("ImageSize", imageSize);
            fragment.setArguments(data);

            fragmentTransaction.add(R.id.grid, fragment, String.valueOf(i));
        }
        fragmentTransaction.commit(); // commit all fragments created
    }

}

