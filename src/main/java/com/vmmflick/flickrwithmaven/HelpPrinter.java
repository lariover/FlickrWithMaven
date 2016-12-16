/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vmmflick.flickrwithmaven;

import java.io.PrintWriter;


/**
 *
 * @author Veronika
 */
public class HelpPrinter {
	
    private String geoChecked;
    private String dateCheck;
    private String likesCheck;
    private double geoDegree;
    private double dateDegree;
    private double likesDegree;
    
    private String name = null;
    private String searchType = null;
    private double geoLatitude = 0, geoLongitude = 0;
    private int likes = 0;
    private String date = null;
	
	HelpPrinter(PhotoSearchServlet source)	{
		geoChecked = source.geoChecked;
		dateCheck = source.dateCheck;
		likesCheck = source.likesCheck;
		geoDegree = source.geoDegree;
		dateDegree = source.dateDegree;
		likesDegree = source.likesDegree;
		name = source.name;
		searchType = source.searchType;
		geoLatitude = source.geoLatitude;
		geoLongitude = source.geoLongitude;
		likes = source.likes;
		date = source.date;
	}
	
    protected void printHeader(PrintWriter out){
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Flickr Form results</title>");
        out.println("<link rel=\"stylesheet\" href=\"index.css\">");
        out.println("<script src=\"http://code.jquery.com/jquery-latest.min.js\"></script>");
        out.println("<script type=\"text/javascript\"  src=\"switch_results.js\"></script>");        
        out.println("<script type=\"text/javascript\"  src=\"paste_descr.js\"></script>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div id=\"header\">");
        out.println("<div id=\"menu\">");
        printQuery(out);
        out.println("<div id=\"bottom_menu\">");
        out.println("<a href=\"index.jsp\">Return to search form</a>");
        out.println("<input type='button' id='hideshow' value='Still loading images...'>");
        out.println("</div>");
        out.println("</div>");
        
        out.println("<div id=\"description-shown\">");
        out.println("<p><em id=\"underlined\">Image description</em>");
        out.println("<br>Click image thumbnail for description</p>");
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
    	out.println("<p><em id=\"underlined\">Query details</em>");
    	// print search query
        if(searchType.equals("tag"))
        {
        	out.println("<br>Tag search: ");
        } else
        {
        	out.println("<br>Fulltext search: ");
        }
        out.println(name);
        // print geo data if needed
        if(geoChecked!=null)
        {
        	out.println("<br>");
        	out.println("Latitude: " + geoLatitude + ", ");
         	out.println("longitude: "+ geoLongitude);
        }
        // print date if needed
        if(dateCheck!=null)
        {
        	out.println("<br>");
        	out.println("Date: " + date);
        }
        // print likes if needed
        if(likesCheck!=null)
        {
        	out.println("<br>");
        	out.println("Likes: " + likes);     	
        }
        // print priorities if needed
        if((geoChecked!=null) || (dateCheck!=null) || (likesCheck!=null))
        {
        	out.println("<br>");
        	out.println("Priority - ");
        	if(geoChecked!=null)
        		out.println("geo: " + geoDegree);
        	if(dateCheck!=null)
        	{
        		if(geoChecked!=null)
        			out.println(", ");
        		out.println("date: " + dateDegree);
        	}
        	if(likesCheck!=null)
        	{
        		if((geoChecked!=null)||(dateCheck!=null))
        			out.println(", ");
        		out.println("likes: " + likesDegree);
        	}
        }
        out.println("</p>");
    }
    
    protected void printResult(PrintWriter out, Photo photo, int position, double rank, int favourites){
        String p_title = photo.getTitle().replaceAll("\"","&quot;");
        String p_url= photo.getThumbnailUrl();
        // print basic description - html tags and title
        out.println("<div id=\"item\" data-target=\"#description-shown\" data-content=\"" +
        		 "<p><em id=&quot;underlined&quot;>Image description</em><br>Title: <a href=&quot;" + photo.getUrl() + "&quot; target='_blank'>" + p_title + "</a>");
        // if ranked photo, print original position
        if(position!=0)
        	out.println("<br>Original position: "+position);
        // if rank was counted, print it
        if(rank!=0)
        	out.println("<br>Rank: "+rank);
        // if geodata is included, print them
        if(geoChecked!=null)
        	out.println("<br>Geo lat: " + photo.getGeoData().getLatitude() + "<br>Geo lon: " + photo.getGeoData().getLongitude());
        // if date is included, print it
        if(dateCheck!=null)
        {
        	out.println("<br>Date taken: " + photo.getDateTaken());
        }
        // if likes are included, print them
        if((likesCheck!=null)&&(favourites!=-1))
        {
        	out.println("<br>Likes: " + favourites);     	
        }
        
        // end description
        out.println("</p>\">");
        // print image
        out.println("<div id=\"image\"><img src=\""+p_url+"\" alt=\""+p_title+"\"/></div>");
        out.println("</div>");
    }

}