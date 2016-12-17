
package com.vmmflick.flickrwithmaven;

import com.flickr4java.flickr.photos.Photo;
import java.util.Comparator;


public class RankedPhoto {

    Photo p;
    int original_position;
    int favourites=-1;
    double rank;
    private double gcd;
    private long dateDifference;
    private double favouritesDifference;

    RankedPhoto(Photo f) {
        p = f;
        rank = 0;
        gcd = 0;
        dateDifference = 0;
        favouritesDifference = 0;
    }

    public void setGcd(double gcd) {
        this.gcd = gcd;
    }
    public void setRank(double rank){
        this.rank=rank;
    }
    public void setDateDiff(long dateDiff){
        this.dateDifference=dateDiff;
    }
    
    public void setFavouritesDiff(double favDiff){
        this.favouritesDifference=favDiff;
    }
    
    public void setPos(int pos){
        this.original_position=pos;
    }
    
    public void setFavourites(int favs){
        this.favourites=favs;
    }   
    
    public double getGcd() {
        return this.gcd;
    }
   
    public long getDateDiff(){
       return this.dateDifference;
    }
    
    public double getFavouritesDiff(){
        return this.favouritesDifference;
    }
    public double getRank(){
        return rank;
    }
    
    public void countRank(double geoD,double dateD, double favD,double maxgcd,int maxlikes, long maxdate ){
        rank=(this.gcd*geoD/maxgcd) + (this.dateDifference*dateD/maxdate)+ (this.favouritesDifference*favD/maxlikes);
    }
    public void printRank(){
        System.out.println("Photo with id "  + p.getId() + " rank is "+ rank);
    }

    /**
     *
     * @return
     */
    public static Comparator<RankedPhoto> getCompByRank() {
        Comparator comp = new Comparator<RankedPhoto>() {
            @Override
            public int compare(RankedPhoto s1, RankedPhoto s2) {
                return Double.compare(s1.getRank(), s2.getRank());
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