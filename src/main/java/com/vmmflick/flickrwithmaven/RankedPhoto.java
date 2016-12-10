/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vmmflick.flickrwithmaven;

import com.flickr4java.flickr.photos.Photo;
import java.util.Comparator;


/**
 *
 * @author Veronika
 */
public class RankedPhoto {

    Photo p;
    double rank;
    int orig_position;

    RankedPhoto(Photo f, double r, int position) {
        p = f;
        rank = r;
        orig_position = position;
    }

    /**
     *
     * @return
     */
    public static Comparator<RankedPhoto> getCompByRank()
{   
 Comparator comp = new Comparator<RankedPhoto>(){
     @Override
     public int compare(RankedPhoto s1, RankedPhoto s2)
     {
         return Double.compare(s1.rank,s2.rank);
     }        
 };
 return comp;
}  
   /*
    @Override
    public int compareTo(RankedPhoto p2) {
        return Double.compare(rank, p2.rank);

    }
*/
}