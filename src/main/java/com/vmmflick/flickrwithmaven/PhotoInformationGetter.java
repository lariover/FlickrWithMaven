package com.vmmflick.flickrwithmaven;

import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class represents the activity of thread
 * It processes the subset of retrieved photos
 * The subsets are distributed in servlet
 * 
 */

public class PhotoInformationGetter implements Runnable {

    private int from = 0;
    private int amount = 0;
    private PhotoList<Photo> photos = null;
    private PhotoSearchServlet servlet = null;
    private ReferenceValues refVals = null;
    private List<RankedPhoto> rankedList=null;
            

    public PhotoInformationGetter(int begin, int howMany, PhotoList<Photo> photos,
            PhotoSearchServlet mainServlet, ReferenceValues refVals, List<RankedPhoto> rankedList) {
        from = begin;
        amount = howMany;
        this.photos = photos;
        servlet = mainServlet;
        this.refVals = refVals;
        this.rankedList=rankedList;

    }

    @Override
    public void run() {
        List<Photo> photosBatch = photos.subList(from, from + amount);
        FlickrPhotoManipulator finder = new FlickrPhotoManipulator();
        GCDAlgorithm distance = new GCDAlgorithm();

        ReentrantLock lock1 = new ReentrantLock();
        ReentrantLock lock2 = new ReentrantLock();
        ReentrantLock lock3 = new ReentrantLock();
       

        for (Photo photo : photosBatch) {
            RankedPhoto rphoto = new RankedPhoto(photo);

          
               rankedList.add(rphoto);
         
         
            if (servlet.geoChecked != null) {
                try {
                    finder.getGeodata(photo);
                } catch (FlickrException ex) {
                    Logger.getLogger(PhotoInformationGetter.class.getName()).log(Level.SEVERE, null, ex);
                }

                double gcd = distance.countGCD(photo.getGeoData(), refVals.lat, refVals.lon);
                //   System.out.println(" Geo done");
                lock1.lock();
                try {
                    if (gcd > MaxValues.MAX_GCD) {

                        MaxValues.MAX_GCD = gcd;
                    }

                    rphoto.setGcd(gcd);

                } finally {
                    lock1.unlock();
                }
             
            }
            if (servlet.likesCheck != null) {

                int favs = 0;
                try {
                    favs = finder.getFavourites(photo);
                    rphoto.setFavourites(favs);
                } catch (FlickrException ex) {
                    Logger.getLogger(PhotoInformationGetter.class.getName()).log(Level.SEVERE, null, ex);
                }
                int likesDiff = Math.abs(refVals.refLikes - favs);
                
                lock2.lock();
                try {
                    if (likesDiff > MaxValues.MAX_FAVS) {
                        MaxValues.MAX_FAVS = likesDiff;
                    }
                    rphoto.setFavouritesDiff(likesDiff);

                } finally {
                    lock2.unlock();
                }
               
            }

            if (servlet.dateCheck != null) {
                try {
                    finder.getPhotoInfo(photo);
                } catch (FlickrException ex) {
                    Logger.getLogger(PhotoInformationGetter.class.getName()).log(Level.SEVERE, null, ex);
                }
                long dateDiff = Math.abs(refVals.refDate.getTime() - photo.getDateTaken().getTime());

                lock3.lock();
                try {
                    if (dateDiff > MaxValues.MAX_DATE) {
                        MaxValues.MAX_DATE = dateDiff;
                    }
                    rphoto.setDateDiff(dateDiff);
                } finally {
                    lock3.unlock();
                }
            }

        }

    }
}
