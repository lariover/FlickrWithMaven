/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vmmflick.flickrwithmaven;

import com.flickr4java.flickr.photos.Photo;


/**
 *
 * @author Veronika
 */
public class RankedPhoto implements Comparable<RankedPhoto> {

    Photo p;
    double rank;

    RankedPhoto(Photo f, double r) {
        p = f;
        rank = r;
    }

   
    @Override
    public int compareTo(RankedPhoto p2) {
        return Double.compare(rank, p2.rank);

    }

}
