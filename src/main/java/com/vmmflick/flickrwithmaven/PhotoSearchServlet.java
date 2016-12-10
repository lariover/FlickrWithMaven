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
	
	// checkbox variables
    String searchtype;
	String geoChecked;
	
	// value variables
	String name;
	String[] tags;
    double geoLatitude, geoLongitude;
    
    // priority variables
    double geoPrio;
	
    protected void printHeader(PrintWriter out){
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet FormHandler</title>");
        out.println("<link rel=\"stylesheet\" href=\"index.css\">");
        out.println("<script src=\"http://code.jquery.com/jquery-latest.min.js\"></script>");
        out.println("<script type=\"text/javascript\"  src=\"switch_results.js\"></script>");        
        out.println("<script type=\"text/javascript\"  src=\"paste_descr.js\"></script>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div id=\"header\">");
        out.println("<div id=\"menu\">");
        printQuery(out);
        out.println("<a href=\"index.jsp\">Return to search form</a>");
        out.println("<input type='button' id='hideshow' value='Still loading images...'>");
        out.println("</div>");
        
        out.println("<div id=\"description-shown\">");
        out.println("</div>");
      
        out.println("<hr id=\"line\">");
        out.println("</div>");
    }
    
    protected void printQuery(PrintWriter out) {
    	// print only no query when empty
    	if(name.replaceAll("\\s+","").isEmpty())
    	{
    		out.println("<p>No query!</p>");
    		return;
    	}
    	// print search query
        if(searchtype.equals("tag"))
        {
        	out.println("<p>Tag search: ");
            for (int i = 0; i < tags.length; i++) {
            	out.print(tags[i]);
            	if(i != tags.length-1)
            		out.println(", ");
            }
        } else
        {
        	out.println("<p>Fulltext search: ");
        	out.println(name);
        }
        // print geo data if needed
        if(geoChecked!=null)
        {
        	out.println("<br>");
        	out.println("Latitude: " + geoLatitude + ", ");
         	out.println("longitude: "+ geoLongitude + "<br>");
         	out.println("Geo priority: " + geoPrio);
        }
        out.println("</p>");
    }
    
    protected void printResult(PrintWriter out, Photo photo, int position){
        String p_title = photo.getTitle().replaceAll("\"","&quot;");
        String p_url= photo.getThumbnailUrl();
        // print basic description - html tags and title
        out.println("<div id=\"item\" data-target=\"#description-shown\" data-content=\"" +
        		 "<p>Title: "+p_title);
        // if ranked photo, print original position
        if(position!=0)
        	out.println("<br>Original position: "+position);
        // if geodata is included, print them
        if(geoChecked!=null)
        	out.println("<br>Geo lat: " + photo.getGeoData().getLatitude() + "<br>Geo lon: " + photo.getGeoData().getLongitude());
        // end description
        out.println("</p>\">");
        // print image
        out.println("<div id=\"image\"><img src=\""+p_url+"\" alt=\""+p_title+"\"/></div>");
        out.println("</div>");
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, FlickrException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {             
       
        name = request.getParameter("query");
        searchtype = request.getParameter("searchtype");
        geoChecked=request.getParameter("gpscheck");
        if(geoChecked!=null){
        	geoLatitude=Double.parseDouble(request.getParameter("latitude"));
         	geoLongitude=Double.parseDouble(request.getParameter("longitude"));
         	String prioString=request.getParameter("GPSprio");
         	geoPrio=Double.parseDouble(prioString) / 100;
        }
        if(searchtype.equals("tag"))
        {
            tags = name.split(" ");
            for (int i = 0; i < tags.length; i++) {
                   tags[i]=tags[i].trim();
                }
        }
        	 
        printHeader(out);

        String apiKey = "859ff620dda11a192d7ff17513b4dfac";
        String sharedSecret = "a90d392d889f0b69";
        Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());
        PhotosInterface photosInteface = flickr.getPhotosInterface();
        SearchParameters params = new SearchParameters();

        //do simple page reranking
        if (name == null) {
            name = "No name";
        }       
        
        if(searchtype.equals("fulltext"))
        {
        	params.setText(name);
        } else
        {
        	params.setTags(tags);
        	params.setTagMode("all");        	
        }
        params.setHasGeo(true);
        List<RankedPhoto> rankedList=new ArrayList<>();
       
        PhotoList<Photo> photos=null;
        try {

            photos = photosInteface.search(params, 500, 1);
        } catch (Exception e) {
            System.out.println(e);
        }
        if((photos!=null) && !photos.isEmpty()){
            int pages = photos.getPages();
    
                
        GCDAlgorithm distance=new GCDAlgorithm();
        out.println("<div id=\"content\" >");
        out.println("<div id=\"norank\" >");
        int current_place=0;
        for (Photo photo : photos) {
              double rank=0;
            current_place += 1;
            if(geoChecked!=null){
                GeoData geoData=null;
               if (flickr.getGeoInterface().getLocation(photo.getId()) != null) {
                 geoData = flickr.getGeoInterface().getLocation(photo.getId());
                 photo.setGeoData(geoData);
               } 
           double gcd= distance.countGCD(geoData, geoLatitude, geoLongitude);
            rank+=gcd;
            }
           rankedList.add(new RankedPhoto(photo,rank,current_place));
           printResult(out,photo,0);
        }
        out.println("</div>");
        Collections.sort(rankedList,RankedPhoto.getCompByRank());
        out.println("<div id=\"rank\">");
        for(RankedPhoto p: rankedList){
        	printResult(out,p.p,p.orig_position);
        }
        out.println("</div>");
        }else{

    out.println (

    "No photos with tags " + name + " found!");
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
    out.println("</div>");
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