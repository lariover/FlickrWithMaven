/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vmmflick.flickrwithmaven;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photos.GeoData;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;
import com.flickr4java.flickr.stats.Stats;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author Veronika
 */
public class FlickrPhotoManipulator {

    String apiKey = "859ff620dda11a192d7ff17513b4dfac";
    String sharedSecret = "a90d392d889f0b69";
    Flickr flickr = null;

    public FlickrPhotoManipulator() {
        flickr = new Flickr(apiKey, sharedSecret, new REST());
    }

    public PhotoList<Photo> findPhotosByText(String query) {
        PhotosInterface photosInteface = flickr.getPhotosInterface();
        SearchParameters params = new SearchParameters();
        params.setText(query);
        params.setHasGeo(true);
        PhotoList<Photo> photos = null;
        try {

            photos = photosInteface.search(params, 500, 1);
        } catch (Exception e) {
            System.out.println(e);
        }

        return photos;

    }

    public PhotoList<Photo> findPhotosByTag(String query) {
        PhotosInterface photosInteface = flickr.getPhotosInterface();
        SearchParameters params = new SearchParameters();
        String[] tags;
        tags = query.split(" ");
        for (int i = 0; i < tags.length; i++) {
            tags[i] = tags[i].trim();
        }
        params.setTags(tags);
        params.setTagMode("all");
        params.setHasGeo(true);
        PhotoList<Photo> photos = null;
        try {

            photos = photosInteface.search(params, 500, 1);
        } catch (Exception e) {
            System.out.println(e);
        }
        
        return photos;

    }

    public void getGeodata(Photo p) throws FlickrException {
        GeoData geoData = flickr.getGeoInterface().getLocation(p.getId());
        p.setGeoData(geoData);

    }

    public int getFavourites(Photo p) throws FlickrException {
        Calendar today = Calendar.getInstance();
        today.clear(Calendar.HOUR);
        today.clear(Calendar.MINUTE);
        today.clear(Calendar.SECOND);
        Date todayDate = today.getTime();
       // Stats stat = flickr.getStatsInterface().getPhotoStats(p.getId(),todayDate);        
      //  p.setStats(stat);
      PhotosInterface photosInteface = flickr.getPhotosInterface();
         Collection<User> users= photosInteface.getFavorites(p.getId(), Integer.MAX_VALUE, 1);
        
         return users.size();
    }
    
    public void getPhotoInfo(Photo p) throws FlickrException{
         PhotosInterface photosInteface = flickr.getPhotosInterface();
         Photo p2= photosInteface.getInfo(p.getId(),sharedSecret);
         p.setDateTaken(p2.getDateTaken());
    }
    
    
   
}
