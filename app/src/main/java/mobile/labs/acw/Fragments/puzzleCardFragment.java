package mobile.labs.acw.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.DisplayContext;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import mobile.labs.acw.Activity.playingActivity;
import mobile.labs.acw.Class.Managers.ImageManager;
import mobile.labs.acw.Class.PairsUtils;
import mobile.labs.acw.R;

/**
 * Created by Alexander on 27/02/2017.
 */

public class puzzleCardFragment extends Fragment {

    public void setImage(Boolean front, Boolean temp) {
        ImageView image = (ImageView) getView().findViewById(R.id.puzzle_card_image);

        if (front) { // front
            image.setImageBitmap(puzzleImage);
            m_Revealed = true;
            m_TempRevealed = temp;
        } else { // backside
            image.setImageBitmap(puzzleBack);
            m_Revealed = false;
            m_TempRevealed = false;
        }
    }

    public void removeListener() {
        ImageView image = (ImageView) getView().findViewById(R.id.puzzle_card_image);
        image.setOnClickListener(null);
    }

    public void activateListener() {
        if (!m_Revealed) {
            ImageView image = (ImageView) getView().findViewById(R.id.puzzle_card_image);
            // set listener
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("[Fragment Card]", "card clicked");
                    playingActivity activity = (playingActivity) getActivity(); // hardcoded activity, assumes playingActivity

                    activity.onCardSelected(m_myInstance, m_ImageName);
                }
            });
        }
    }

    public void restoreFragment(int newSize) {
        // set fragment layout size size
        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.puzzle_card_fragment);
        GridLayout.LayoutParams params = (GridLayout.LayoutParams) layout.getLayoutParams();
        params.height = newSize;
        params.width = newSize;
        layout.setLayoutParams(params);

        m_ImageSize = newSize;

        puzzleImage = PairsUtils.scaleDown(ImageManager.getImageByFileName(m_ImageName), m_ImageSize, true); // load in bitmap from manager
        puzzleBack = Bitmap.createBitmap(m_ImageSize, m_ImageSize, Bitmap.Config.ARGB_8888); // create empty bitmap
        puzzleBack.eraseColor(m_CardBackColor); // set bitmap color to white;

        setImage(m_Revealed, m_TempRevealed);
        if(m_Revealed && !m_TempRevealed)
            removeListener();
    }

    public String m_ImageName = "";
    public Bitmap puzzleImage;
    public Bitmap puzzleBack;
    public int m_FragmentID = 0;
    public puzzleCardFragment m_myInstance = this;
    public static final int m_CardBackColor = Color.WHITE;
    public Boolean m_Revealed = false;
    public int m_ImageSize;
    public boolean m_TempRevealed = false;

    @Override
    public void onStart(){
        super.onStart();

        ImageView image = (ImageView) getView().findViewById(R.id.puzzle_card_image);
        image.setImageBitmap(puzzleBack); // set image
        // set listener
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("[Fragment Card]", "card clicked");
                playingActivity activity = (playingActivity) getActivity(); // hardcoded activity, assumes playingActivity

                activity.onCardSelected(m_myInstance, m_ImageName);
            }
        });
    }

    public void swap(puzzleCardFragment other){
        puzzleCardFragment temp = this; // save to temp fragment

        // copy values from other to this
        m_ImageName = other.m_ImageName;
        m_ImageSize = other.m_ImageSize;
        puzzleImage = other.puzzleImage;

        // copy values from this to other
        other.m_ImageName = temp.m_ImageName;
        other.m_ImageSize = temp.m_ImageSize;
        other.puzzleImage = temp.puzzleImage;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_puzzle_card, container, false);

        setRetainInstance(true); // save fragment

        m_ImageName = (String) getArguments().get("ImageName");
        m_FragmentID = (int) getArguments().get("FragmentID");
        int imageSize = (int) getArguments().get("ImageSize");

        // set fragment layout size size
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.puzzle_card_fragment);
        GridLayout.LayoutParams params = (GridLayout.LayoutParams) layout.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        layout.setLayoutParams(params);

        m_ImageSize = imageSize;

        puzzleImage = PairsUtils.scaleDown(ImageManager.getImageByFileName(m_ImageName), m_ImageSize, true); // load in bitmap from manager
        puzzleBack = Bitmap.createBitmap(m_ImageSize, m_ImageSize, Bitmap.Config.ARGB_8888); // create empty bitmap
        puzzleBack.eraseColor(m_CardBackColor); // set bitmap color to white;

        return view;
    }
}
