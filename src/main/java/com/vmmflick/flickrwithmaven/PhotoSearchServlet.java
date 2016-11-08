/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vmmflick.flickrwithmaven;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;
import java.io.IOException;
import java.io.PrintWriter;
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
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet FormHandler</title>");
        out.println("</head>");
        out.println("<body>");
    
       
        String name = request.getParameter("name");
            printHeader(out,name);
            
        String apiKey = "859ff620dda11a192d7ff17513b4dfac";
        String sharedSecret = "a90d392d889f0b69";
        Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());
        PhotosInterface photosInteface = flickr.getPhotosInterface();
        SearchParameters params = new SearchParameters();

//Photo interface search method ----think how to solve pages
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
        }
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
        processRequest(request, response);
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
        processRequest(request, response);
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
