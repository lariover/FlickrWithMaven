/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vmmflick.flickrwithmaven;

import java.util.Date;

/**
 *
 *class serves as container for reference values - values from submitted form
 */
public class ReferenceValues {
    double lat=0;
    double lon=0;
    Date refDate=null;
    int refLikes=0;
    
    public void setGeo(double lat, double lon){
        this.lat=lat;
        this.lon=lon;        
    }
    
    public void setDate(Date rdate){
        this.refDate=rdate;
    }
    
    public void setFavs(int favs){
        refLikes=favs;
    }
    
}
