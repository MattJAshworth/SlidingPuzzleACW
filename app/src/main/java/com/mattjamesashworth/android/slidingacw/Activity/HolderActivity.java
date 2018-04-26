package com.mattjamesashworth.android.slidingacw.Activity;

/**
 * Created by mattjashworth on 26/04/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.support.design.widget.BottomNavigationView;

import com.mattjamesashworth.android.slidingacw.Class.Handlers.PuzzleDBHandler;
import com.mattjamesashworth.android.slidingacw.Class.PuzzleDBContract;
import com.mattjamesashworth.android.slidingacw.Fragments.HomeFragment;
import com.mattjamesashworth.android.slidingacw.Fragments.Highscore;
import com.mattjamesashworth.android.slidingacw.R;


/**
 * Created by MattJAshworth on 30/03/2018.
 * For Sliding Puzzle ACW.
 * Last updated by MattJAshworth on 26/04/2018, see git log for updates.
 */

public class HolderActivity extends AppCompatActivity {

    private TextView mTextMessage;

    public static String PACKAGE_NAME;

    static Context mContext;

    PuzzleDBHandler m_DBHelperRead;

    public static final String MY_PREFS_NAME = "UserSessionData";


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentTransaction.replace(R.id.content, new HomeFragment()).commit();
                    return true;
                case R.id.navigation_highscores:
                    if (onCheckHighscore() == true) {
                        fragmentTransaction.replace(R.id.content, new Highscore()).commit();
                        return true;
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.navigationNoHighscores), Toast.LENGTH_LONG).show();
                        return false;
                    }

                case R.id.navigation_leaderboard:
                    //fragmentTransaction.replace(R.id.content, new GalleryFragment()).commit();
                    Toast.makeText(getApplication(), getString(R.string.globalLeaderboards), Toast.LENGTH_LONG).show();
                    return false;
                case R.id.navigation_logout:
                    initLogout();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holder);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //navigation.getBackground().setAlpha(0);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, new HomeFragment()).commit();

        //m_DBHelperRead = new PuzzleDBHandler(getContext());
         m_DBHelperRead = new PuzzleDBHandler(getApplicationContext());


        //Background transitions
        LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.container);
        AnimationDrawable animationDrawable;
        animationDrawable =(AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();


    }

    @Override
    public void onResume() {
        super.onResume();

        //moveTaskToBack(true);
    }

  /*  @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }*/


    public static Context getContext() {
        //  return instance.getApplicationContext();
        return mContext;
    }



    public boolean onCheckHighscore() {
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
            return false;
        }
        else
        {
            return true;
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }


   public void initLogout() {

       SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

       try {
           prefs.edit().remove("username").commit();
           prefs.edit().remove("keepAlive").commit();
       } catch (Exception ex) {
           Log.e("HolderActivity", "Couldn't find prefs");
       }

       Intent intent = new Intent(this, PrefaceActivity.class);
       startActivity(intent);



   }

}

