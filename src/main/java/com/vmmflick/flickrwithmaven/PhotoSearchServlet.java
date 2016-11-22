/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vmmflick.flickrwithmaven;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.GeoData;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Veronika
 */
public class PhotoSearchServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void printHeader(PrintWriter out, String tags){
        out.print("<h1>Search request for "+ tags);
        out.println("</h1>");
        out.println("<a href=\".\">Return to search form</a>");
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, FlickrException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet FormHandler</title>");
        out.println("</head>");
        out.println("<body>");
    
       
        String name = request.getParameter("query");
        String geoChecked=request.getParameter("gpscheck");
        System.out.println("The geo is " + geoChecked);
        double geoLatitude=0, geoLongitude=0;
        if(geoChecked!=null){
         geoLatitude=Double.parseDouble(request.getParameter("latitude"));
         geoLongitude=Double.parseDouble(request.getParameter("longitude"));
        }
         System.out.println("The input lat: "+ geoLatitude + " lon: " + geoLongitude);   
        printHeader(out,name);

        String apiKey = "859ff620dda11a192d7ff17513b4dfac";
        String sharedSecret = "a90d392d889f0b69";
        Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());
        PhotosInterface photosInteface = flickr.getPhotosInterface();
        SearchParameters params = new SearchParameters();

        //do simple page reranking
        if (name == null) {
            name = "No name";
        }
        String[] tags;
        tags = name.split(" ");
        
        for (int i = 0; i < tags.length; i++) {
           tags[i]=tags[i].trim();
        }
        params.setText(name);
       // params.setTags(tags);
       // params.setTagMode("all");
        params.setHasGeo(true);
        List<RankedPhoto> rankedList=new ArrayList<>();
       
        PhotoList<Photo> photos=null;
        try {

            photos = photosInteface.search(params, 500, 1);
        } catch (Exception e) {
            System.out.println(e);
        }
        if((photos!=null) && !photos.isEmpty()){
           out.println("<div>");
            int pages = photos.getPages();
    
                
        GCDAlgorithm distance=new GCDAlgorithm();
            
            out.println("<div class=\"norank\" style=\"width: 50%\">");
        for (Photo photo : photos) {
              double rank=0;
            if(geoChecked!=null){
                
                GeoData geoData=null;
               if (flickr.getGeoInterface().getLocation(photo.getId()) != null) {
                 geoData = flickr.getGeoInterface().getLocation(photo.getId());
                 photo.setGeoData(geoData);
               } 
           double gcd= distance.countGCD(geoData, geoLatitude, geoLongitude);
            rank+=gcd;
            }
           String p_url= photo.getThumbnailUrl();
            rankedList.add(new RankedPhoto(photo,rank));
         
           out.println("<img src=\""+p_url+"\" alt=\""+photo.getTitle()+"\"/>");
           
        }
        out.println("</div>");
        Collections.sort(rankedList,RankedPhoto.getCompByRank());
        out.println("<div class=\"rank\">");
        for(RankedPhoto p: rankedList){
            System.out.println("The rank is: " + p.rank);
            System.out.println("The lat: " + p.p.getGeoData().getLatitude() + " lon: " + p.p.getGeoData().getLongitude());
            String p_url= p.p.getThumbnailUrl();
            out.println("<img src=\""+p_url+"\" alt=\""+p.p.getTitle()+"\"/>");
            
        }
        out.println("</div>");
        }else{

    out.println (

    "No photos wiith tags " + name + " found!");
        }
        
        
        
//Photo interface search method ----think how to solve pages
    /*  
    if (name == null) {
            name = "No name";
        }
        String[] tags;
        tags = name.split(" ");
        
        for (int i = 0; i < tags.length; i++) {
           tags[i]=tags[i].trim();
        }
        params.setText(name);
       // params.setTags(tags);
       // params.setTagMode("all");
        params.setHasGeo(true);
        PhotoList<Photo> photos=null;
        try {

            photos = photosInteface.search(params, 500, 1);
        } catch (Exception e) {
            System.out.println(e);
        }
        if((photos!=null) && !photos.isEmpty()){
           out.println("<div>");
            int pages = photos.getPages();
    

        

        for (Photo photo : photos) {

            String p_url= photo.getThumbnailUrl();
            
         
            out.println("<img src=\""+p_url+"\" alt=\""+photo.getTitle()+"\"/>");
           
        }
        out.println("</div>");
        }else{

    out.println (

    "No photos wiith tags " + name + " found!");
        }*/
    out.println (
            
    "</body>");
    out.println (

"</html>");

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (FlickrException ex) {
            Logger.getLogger(PhotoSearchServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (FlickrException ex) {
            Logger.getLogger(PhotoSearchServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
