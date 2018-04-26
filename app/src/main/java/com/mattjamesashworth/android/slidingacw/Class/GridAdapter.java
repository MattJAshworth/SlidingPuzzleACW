package com.mattjamesashworth.android.slidingacw.Class;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mattjamesashworth.android.slidingacw.R;

/**
 * Created by MattJAshworth on 30/03/2018.
 * For Sliding Puzzle ACW.
 * Last updated by MattJAshworth on 26/04/2018, see git log for updates.
 */

public class GridAdapter extends BaseAdapter
{

    private Bitmap tiles[];
    private Context context;
    private LayoutInflater inflater;

    public GridAdapter(Context context, Bitmap tiles[])
    {
        this.context = context;
        this.tiles = tiles;
    }

    @Override
    public int getCount()
    {
        return tiles.length;
    }

    @Override
    public Object getItem(int i)
    {
        return null;
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        View gridView = view;
        if (view == null)
        {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.custom_layout, null);
        }
        ImageView icon = (ImageView) gridView.findViewById(R.id.tiles);
        //ImageView imageView = (ImageView) findViewById(R.id.myImage);
        //imageView.setImageBitmap(BitmapFactory.decodeFile("/data/data/com.example.robda.androidacw/files/apple12.JPEG"));
        icon.setImageBitmap(tiles[i]);
        return gridView;
    }
}
