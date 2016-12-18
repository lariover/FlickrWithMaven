
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
import java.util.Collection;


/**
 * The class is used as communicator with Flick
 * It purpose is to access Flick and get photos and their information
 * 
 */
public class FlickrPhotoManipulator {

    String apiKey = "859ff620dda11a192d7ff17513b4dfac";
    String sharedSecret = "a90d392d889f0b69";
    Flickr flickr = null;

    public FlickrPhotoManipulator() {
        flickr = new Flickr(apiKey, sharedSecret, new REST());
    }

    /**
     * The method retrieves photos which has the expression from query somewhere in the text that describes them
     * @param query the text to search by
     * @return list of photos which are assigned wit query
     */
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

    /**
     * The methods parses the expression into words and uses each word as tag
     * all tags have to be valid, for photo to be returned (photo has to contain all of them)
     * @param query the expression to search by
     * @return list of photos which are associated with the tags from expression
     */
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

    
    
    /**
     * The method asks Flick for geodata for specific photo
     * @param p photo, for which we ask for geodata
     * @throws FlickrException 
     */
    public void getGeodata(Photo p) throws FlickrException {
        GeoData geoData = flickr.getGeoInterface().getLocation(p.getId());
        p.setGeoData(geoData);

    }

    public int getFavourites(Photo p) throws FlickrException {
        
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
