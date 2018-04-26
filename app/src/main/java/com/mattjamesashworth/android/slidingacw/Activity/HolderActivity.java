package com.mattjamesashworth.android.slidingacw.Activity;

/**
 * Created by mattjashworth on 26/04/2018.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.mattjamesashworth.android.slidingacw.R;

import java.io.File;


public class HolderActivity extends AppCompatActivity {

    private TextView mTextMessage;

    public static String PACKAGE_NAME;

    static Context mContext;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_snapsave:
                    fragmentTransaction.replace(R.id.content, new SnapSave()).commit();
                    return true;
                case R.id.navigation_settings:
                    fragmentTransaction.replace(R.id.content, new NameList()).commit();
                    return true;
                case R.id.navigation_gallery:
                    //fragmentTransaction.replace(R.id.content, new GalleryFragment()).commit();
                    Toast.makeText(getApplication(), "Gallery coming soon, v2.0", Toast.LENGTH_LONG).show();
                    return false;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holder);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-7439808203904930~4571758615");
        PACKAGE_NAME = getApplicationContext().getPackageName();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, new SnapSave()).commit();

        mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-7439808203904930/2427922956");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:

                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        mInterstitialAd.show();
                    }
                });

                moveTaskToBack(true);

                return true;
        }
        return false;
    }


    public String getPackageNameString() {
        return PACKAGE_NAME;
    }


    public void fullscreenFragment() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.content, new FullscreenFragment()).commit();

    }


}

