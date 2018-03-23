package com.mattjamesashworth.android.slidingacw.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mattjamesashworth.android.slidingacw.Class.Managers.GameManager;
import com.mattjamesashworth.android.slidingacw.Class.Puzzle;
import com.mattjamesashworth.android.slidingacw.R;

/**
 * Created by mattjashworth on 21/03/2018.
 * Updated by mattjashworth on 23/03/2018, see git log for updates.
 */


public class playGameActivity extends AppCompatActivity {
    // variables populated before being able to play
    private int m_SelectedPuzzle;
    private int m_LastPuzzlePosition;
    private View m_View;
    private List<Puzzle> fullPuzzleList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);
        m_View = this.findViewById(android.R.id.content); // store root view

        //Transition Animations
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.frags);
        AnimationDrawable animationDrawable;
        animationDrawable =(AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        m_SelectedPuzzle = -1;
        m_LastPuzzlePosition = -1;
        puzzleListViewIdList = new ArrayList<>();
        puzzleList = new ArrayList<>();

        // initialize filterTypes
        filterTypes = new String[]{
                getResources().getString(R.string.none),
                getResources().getString(R.string.played_before),
                getResources().getString(R.string.played_never),
                getResources().getString(R.string.more_then_24_cards),
                getResources().getString(R.string.less_then_24_cards)
        };

        fullPuzzleList = GameManager.getPuzzleList();
        setupSelections(GameManager.getListOfPuzzleIDs());
        Button downloadButton = (Button) findViewById(R.id.button_download_more_puzzles);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), downloadActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RadioButton clickButton = (RadioButton) findViewById(R.id.setup_click_button);
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   GameManager.setGameMode(GameManager.GameMode.MODE_CLICK);
                //   Log.i("[Game Mode Change]", GameManager.getGameMode().toString());
            }
        });

        RadioButton dragButton = (RadioButton) findViewById(R.id.setup_drag_button);
        dragButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    GameManager.setGameMode(GameManager.GameMode.MODE_DRAG);
                //   Log.i("[Game Mode Change]", GameManager.getGameMode().toString());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setupSelections( GameManager.getListOfPuzzleIDs() );
    }

    String[] filterTypes;
    public void filterListView(int filter) {
        //List<Puzzle> filterList = bubbleSort(filter, puzzleList);
        List<Puzzle> filterList = filterList(filter, puzzleList);

        m_LastPuzzlePosition = -1;
        TextView text = (TextView) findViewById(R.id.text_selected_puzzle);
        text.setVisibility(View.INVISIBLE);

        Button playBut = (Button) findViewById(R.id.play_button);
        playBut.setEnabled(false);

        List<Integer> filterIdList = new ArrayList<>();
        for(Puzzle puzzle : filterList){
            filterIdList.add(puzzle.puzzle);

        }
        setupSelections(filterIdList);
    }

    public List<Puzzle> filterList(int filterType, List<Puzzle> list) {
        List<Puzzle> localPuzzleList = new ArrayList<>();

        for( Puzzle p : fullPuzzleList ) {
            if (filterType == 0) { //  NONE
                    localPuzzleList.add(p);
            }
            if (filterType == 1) { // Played Before
                if(p.puzzle_High_Score > 0)
                    localPuzzleList.add(p);
            } else if (filterType == 2) { // Never Played
                if(p.puzzle_High_Score == 0)
                    localPuzzleList.add(p);
            } else if (filterType == 3) { // More then 24 cards
                if(p.puzzle_Layout.size() >= 24)
                    localPuzzleList.add(p);
            } else if (filterType == 4) { // less then 24 cards
                if(p.puzzle_Layout.size() < 24)
                    localPuzzleList.add(p);
            }
        }
        return localPuzzleList;
    }

    // method bubble sorts the list into an order defined by the filterType
    public List<Puzzle> bubbleSort(int filterType, List<Puzzle> list) {
        List<Puzzle> localPuzzleList = list;
        if(localPuzzleList == null) // initialise array if not done so previously
            localPuzzleList = new ArrayList<>();

        Boolean done = true;
        Boolean swap = false;
        for (int i = 0; i < puzzleList.size(); i++) {
            for (int j = 0; j < puzzleList.size()-1; j++) {
                swap = false;
                if (filterType == 0) { // most cards
                    if(localPuzzleList.get(j).puzzle_Layout.size() < localPuzzleList.get(j+1).puzzle_Layout.size())
                        swap = true;
                }else if(filterType == 1){ // least cards
                    if(localPuzzleList.get(j).puzzle_Layout.size() > localPuzzleList.get(j+1).puzzle_Layout.size())
                        swap = true;
                }else if(filterType == 2){ // smallest puzzle ID
                    if(localPuzzleList.get(j).puzzle > localPuzzleList.get(j+1).puzzle)
                        swap = true;
                }else if(filterType == 3){ // highest puzzle ID
                    if(localPuzzleList.get(j).puzzle < localPuzzleList.get(j+1).puzzle)
                        swap = true;
                }

                if(swap) {
                    done = false;
                    Collections.swap(localPuzzleList, j, j+1); // swap
                }
            }
        }

        if( !done ) // if not done (meaning something was swapped)
            localPuzzleList = bubbleSort(filterType, localPuzzleList); // recurse again

        return localPuzzleList;
    }

    public void gameReady(){
        Button playBut = (Button) findViewById(R.id.play_button);
        playBut.setEnabled(true);
        playBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), playingActivity.class);

                Puzzle selected_puzzle = null;
                for(Puzzle puzzle : GameManager.getPuzzleList()) {
                    if (puzzle.puzzle == m_SelectedPuzzle)
                        selected_puzzle = puzzle;
                }

                intent.putExtra("Puzzle_ID", selected_puzzle.puzzle); // store puzzle id in intent
                intent.putExtra("Continue", false);

                startActivity(intent);
            }
        });
    }

    private List<Integer> puzzleListViewIdList;
    private List<Puzzle> puzzleList;
    public void setupSelections(List<Integer> puzzleIDList){
        puzzleList = new ArrayList<>();
        // show main content
        LinearLayout layout = (LinearLayout)findViewById(R.id.setup_main_content);
        layout.setVisibility(View.VISIBLE);

        List<String> puzzleText = new ArrayList<>();
        for(Integer id : puzzleIDList) {
            puzzleListViewIdList.add(id);
            Puzzle temp = GameManager.getPuzzleByID(id);
            puzzleList.add(temp);
           /* SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(String.valueOf(id), MODE_PRIVATE); // get shared preference
            int highScore = sharedPreferences.getInt(playingActivity.HIGH_SCORE, 0); // retrieve highscore
            if( highScore > 0 )
                text += " - "+ getResources().getString(R.string.high_score)+": "+highScore; // if highscore exist, append to original text
*/
            String text = getResources().getString(R.string.puzzle)+" "+id+" - "+getResources().getString(R.string.cards)+": "+temp.puzzle_Layout.size(); // set default text
            if( temp.puzzle_High_Score > 0 )
                text += " - "+ getResources().getString(R.string.high_score)+": "+temp.puzzle_High_Score; // if highscore exist, append to original text

            puzzleText.add(text);
            Log.i("[ListView Puzzle Added]", String.valueOf(id));
        }

        ListView listView = (ListView)findViewById(R.id.list_of_puzzles);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, puzzleText);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedPuzzleText = (TextView) findViewById(R.id.text_selected_puzzle); // retrieve textview object
                selectedPuzzleText.setVisibility(View.VISIBLE); // set visible

                ListView listView = (ListView)findViewById(R.id.list_of_puzzles);
                int puzzle_ID = puzzleList.get(position).puzzle;
                String selected_puzzle_name = String.valueOf(puzzle_ID);

                if(puzzle_ID != m_LastPuzzlePosition) { // update current selection
                    view.setBackgroundColor(Color.DKGRAY); // change background color to highlight current selection
                    if(m_LastPuzzlePosition != -1)
                        listView.getChildAt(m_LastPuzzlePosition).setBackgroundColor(Color.TRANSPARENT); // remove background if previous
                }
                m_LastPuzzlePosition = position; // save position for next click event

                selectedPuzzleText.setText( getResources().getString(R.string.selected_puzzle) +": "+selected_puzzle_name); // update text

                m_SelectedPuzzle = puzzle_ID; // set selected puzzle id
                Log.i("[Puzzle Change]", String.valueOf(m_SelectedPuzzle));

                if(m_SelectedPuzzle != -1)
                    gameReady();
            }
        });
    }
}
