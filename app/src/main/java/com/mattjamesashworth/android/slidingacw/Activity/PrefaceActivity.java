package com.mattjamesashworth.android.slidingacw.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.mattjamesashworth.android.slidingacw.R;

/**
 * Created by mattjashworth on 21/03/2018.
 * Updated by mattjashworth on 23/03/2018, see git log for updates.
 */

public class PrefaceActivity extends AppCompatActivity {

    public static final String PUZZLE_DIRECTORY = "http://www.simongrey.net/08027/slidingPuzzleAcw/";
    public static final String PUZZLE_INDEX = "index.json";
    public static final String PUZZLE_FILES = "puzzles/";
    public static final String PUZZLE_PICTURESET = "images/";
    public static final String PUZZLE_IMAGES = "images/";

    public static final String MY_PREFS_NAME = "UserSessionData";

    public static String baseUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        baseUsername = prefs.getString("username", "a");
        Boolean advance = prefs.getBoolean("keepAlive", false);

        if (advance == true) {
            retrieveSession();
        }


        if (prefs.getBoolean("my_first_time", true)) {
            showTutorial();
            prefs.edit().putBoolean("my_first_time", false).commit();
        }

        //Background transitions
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.content);
        AnimationDrawable animationDrawable;
        animationDrawable =(AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();


        final EditText usernameEntry = (EditText) findViewById(R.id.preface_UsernameTxt);
        final Switch keepSessionSwitch = (Switch) findViewById(R.id.switch_KeepSession);
        Button playACW = (Button) findViewById(R.id.btn_Login);

        playACW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validation
                String strUsername = usernameEntry.getText().toString();
                if (strUsername.length() < 2) {
                    Toast.makeText(getApplicationContext(), "Username is too short, try again", Toast.LENGTH_LONG).show();
                    return;
                }
                if (strUsername.length() > 15) {
                    Toast.makeText(getApplicationContext(), "Username is too long, try again", Toast.LENGTH_LONG).show();
                    return;
                }

                if (keepSessionSwitch.isChecked()) {
                    //Keep user signed in via shared prefs
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("username", strUsername);
                    baseUsername = strUsername;
                    editor.putBoolean("keepAlive", true);
                    editor.apply();
                }


                Intent intent = new Intent(PrefaceActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


    private void retrieveSession() {
        Intent intent = new Intent(PrefaceActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void showTutorial() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PrefaceActivity.this);
        builder.setTitle("About Sliding ACW");
        builder.setMessage("Sliding Puzzle_old used read/write storage permissions for saving of long term saving highscores. \n \nHighscores can be saved without this permission in shared preferences but its recommended you allow storage. \n \nIf you like the game and want to know more, hit the developer button in game to view on Google Play and the source code on github. \n Thanks, Matt.");
        builder.setPositiveButton("Lets Play", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Dismiss
            }
        });

        builder.create().show();

    }

}
