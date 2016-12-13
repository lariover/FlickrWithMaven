/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vmmflick.flickrwithmaven;

import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

    private String geoChecked = null;
    private String dateCheck = null;
    private String likesCheck = null;
    private double geoDegree = 0;
    private double dateDegree = 0;
    private double likesDegree = 0;

    private void extractParametersFromRequest(HttpServletRequest request) {
        geoChecked = request.getParameter("gpscheck");
        dateCheck = request.getParameter("datecheck");
        likesCheck = request.getParameter("likecheck");
        geoDegree = Double.parseDouble(request.getParameter("GPSprio")) / 100;
        dateDegree = Double.parseDouble(request.getParameter("Dateprio")) / 100;
        likesDegree = Double.parseDouble(request.getParameter("Likesprio")) / 100;

    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, FlickrException, ParseException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */

            String name = request.getParameter("query");
            String searchType = request.getParameter("searchtype");
            this.extractParametersFromRequest(request);
           
            double geoLatitude = 0, geoLongitude = 0;
            int likes = 0;
            Date refDate = null;

            if (geoChecked != null) {
                geoLatitude = Double.parseDouble(request.getParameter("latitude"));
                geoLongitude = Double.parseDouble(request.getParameter("longitude"));
            }

            if (likesCheck != null) {
                likes = Integer.parseInt(request.getParameter("likes"));
            }

            if (dateCheck != null) {
                DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                String date = request.getParameter("date");
                refDate = format.parse(date);
            }

            if (name == null) {
                name = "No name";
            }
            
            HelpPrinter print = new HelpPrinter();
            print.printHeader(out, name);
            print.printTitle(out, name);

            //do simple page reranking
            FlickrPhotoManipulator finder = new FlickrPhotoManipulator();
            PhotoList<Photo> photos = null;
            if (searchType.equals("fulltext")) {
                photos = finder.findPhotosByText(name);
            } else {
                photos = finder.findPhotosByTag(name);
            }

            List<RankedPhoto> rankedList = new ArrayList<>();

            if ((photos != null) && !photos.isEmpty()) {
                out.println("<div>");
                int pages = photos.getPages();

                GCDAlgorithm distance = new GCDAlgorithm();
                out.println("<div class=\"norank\">");
                double maxGcd = 1;
                int maxlikesdiff = 1;
                long maxdaysDiff = 1;
                for (Photo photo : photos) {

                    RankedPhoto rphoto = new RankedPhoto(photo);
                    rankedList.add(rphoto);
                    
                    if (geoChecked != null) {
                        finder.getGeodata(photo);

                        double gcd = distance.countGCD(photo.getGeoData(), geoLatitude, geoLongitude);
                        if (gcd > maxGcd) {
                            maxGcd = gcd;
                        }
                        rphoto.setGcd(gcd);

                    }
                    if (likesCheck != null) {
                        
                        int favs=finder.getFavourites(photo);
                        int likesDiff = Math.abs(likes - favs);
                        if (likesDiff > maxlikesdiff) {
                            maxlikesdiff = likesDiff;
                        }
                        rphoto.setFavouritesDiff(likesDiff);

                    }

                    if (dateCheck != null) {
                        finder.getPhotoInfo(photo);
                        long dateDiff = Math.abs(refDate.getTime() - photo.getDateTaken().getTime());
                        if (dateDiff > maxdaysDiff) {
                            maxdaysDiff = dateDiff;
                        }
                        rphoto.setDateDiff(dateDiff);
                    }

                    String p_url = photo.getThumbnailUrl();

                    out.println("<img src=\"" + p_url + "\" alt=\"" + photo.getTitle() + "\"/>");

                }

                for (RankedPhoto rphoto : rankedList) {
                    rphoto.countRank(geoDegree, dateDegree, likesDegree, maxGcd, maxlikesdiff, maxdaysDiff);
                    rphoto.printRank();
                }
                System.out.println("--------------ranked--------------");
                out.println("</div>");
                Collections.sort(rankedList, RankedPhoto.getCompByRank());
                out.println("<div class=\"rank\">");
                for (RankedPhoto p : rankedList) {
                    p.printRank();
                    // System.out.println("The lat: " + p.p.getGeoData().getLatitude() + " lon: " + p.p.getGeoData().getLongitude());
                    String p_url = p.p.getThumbnailUrl();

                    out.println("<img src=\"" + p_url + "\" alt=\"" + p.p.getTitle() + "\"/>");

                }
                out.println("</div>");
            } else {

                out.println(
                        "No photos wiith tags " + name + " found!");
            }

            out.println(
                    "</body>");
            out.println(
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
        } catch (ParseException ex) {
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
        } catch (ParseException ex) {
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
