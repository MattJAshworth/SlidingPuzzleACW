package mobile.labs.acw.Class;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 26/02/2017.
 */

public class PuzzlePictureSet {
    private Map<Integer, String> m_ImageSets = new LinkedHashMap<>();
    public Map<Integer, String> getImageSet(){
        return m_ImageSets;
    }

    public List<String> getImageNames(){
        return new ArrayList<>(m_ImageSets.values());
    }

    public String mSetName;
    public PuzzlePictureSet(String setName){
        mSetName = setName;
    }

    // return ID for image
    public Integer getImageID(String fileName){
        for(int i =0; i < m_ImageSets.size();i++)
        {
            if(m_ImageSets.get(i) == fileName)
                return i;
        }
        return null;
    }

    // add image to list, using the current size as the ID
    public void addImage(String imageName){
        m_ImageSets.put(m_ImageSets.size(), imageName);
    }
}


