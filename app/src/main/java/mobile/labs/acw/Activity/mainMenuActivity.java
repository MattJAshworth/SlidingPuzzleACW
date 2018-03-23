package mobile.labs.acw.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;
import java.util.Set;

import mobile.labs.acw.Class.Managers.GameManager;
import mobile.labs.acw.Class.Managers.ImageManager;
import mobile.labs.acw.Class.Managers.JsonManager;
import mobile.labs.acw.Class.PuzzleImage;
import mobile.labs.acw.R;

/**
 * Created by mattjashworth on 21/03/2018.
 * Updated by mattjashworth on 23/03/2018, included hallucinogenics.
 */

public class mainMenuActivity extends AppCompatActivity {

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
            initGameData();
            retrieveSession();
        }


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
                    editor.putBoolean("keepAlive", true);
                    editor.apply();
                }


                Intent intent = new Intent(mainMenuActivity.this, playGameActivity.class);
                startActivity(intent);
            }
        });

    }



    private void initGameData() {
        if( !ImageManager.m_Init )
            new ImageManager(getApplicationContext()); // initialise ImageManager
        if( !JsonManager.m_Init)
            new JsonManager(getApplicationContext()); // initialise JsonManager
        if( !GameManager.m_Init )
            new GameManager(getApplicationContext(), JsonManager.getJsonList()); // initialise GameManager
    }

    private void retrieveSession() {
        Intent intent = new Intent(mainMenuActivity.this, playGameActivity.class);
        startActivity(intent);
    }

}
