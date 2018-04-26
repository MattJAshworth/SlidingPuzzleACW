package com.mattjamesashworth.android.slidingacw.Class;

/**
 * Created by MattJAshworth on 30/03/2018.
 * For Sliding Puzzle ACW.
 * Last updated by MattJAshworth on 26/04/2018, see git log for updates.
 */
public class Puzzle
{
    private String m_Name;
    private String m_PictureSet;
    private String m_LayoutDef;
    private String m_Highscore;
    private String m_Username;
    private int m_ID;

    @Override
    public String toString()
    {
        return m_Name;
    }

    public Puzzle(String pName, String pPictureSet, String pLayoutDef, String pHighScore, int pID, String pUsername)
    {
        this.m_Name = pName;
        this.m_PictureSet = pPictureSet;
        this.m_LayoutDef = pLayoutDef;
        this.m_Highscore = pHighScore;
        this.m_ID = pID;
        this.m_Username = pUsername;
    }

    public String Name()
    {
        return m_Name;
    }

    public String Highscore()
    {
        return m_Highscore;
    }

    public String PictureSet()
    {
        return m_PictureSet;
    }

    public String LayoutDef()
    {
        return m_LayoutDef;
    }

    public int ID()
    {
        return m_ID;
    }

    public String Username() {
        return m_Username;
    }
}
