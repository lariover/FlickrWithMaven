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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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

    protected String geoChecked = null;
    protected String dateCheck = null;
    protected String likesCheck = null;
    protected double geoDegree = 0;
    protected double dateDegree = 0;
    protected double likesDegree = 0;

    protected String name = null;
    protected String searchType = null;
    protected double geoLatitude = 0, geoLongitude = 0;
    protected int likes = 0;
    protected String date = null;

    private void getGeoParametrs(HttpServletRequest request) {
        geoLatitude = Double.parseDouble(request.getParameter("latitude"));
        geoLongitude = Double.parseDouble(request.getParameter("longitude"));
    }

    private void extractParametersFromRequest(HttpServletRequest request) {
        geoChecked = request.getParameter("gpscheck");
        dateCheck = request.getParameter("datecheck");
        likesCheck = request.getParameter("likecheck");
        geoDegree = Double.parseDouble(request.getParameter("GPSprio")) / 100;
        dateDegree = Double.parseDouble(request.getParameter("Dateprio")) / 100;
        likesDegree = Double.parseDouble(request.getParameter("Likesprio")) / 100;
        name = request.getParameter("query");
        searchType = request.getParameter("searchtype");

    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, FlickrException, ParseException, InterruptedException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            this.extractParametersFromRequest(request);
            String tnum = request.getParameter("threads");
            int thnum = 1;
            if ((tnum != null) && !(tnum.equals(""))) {
                thnum = Integer.parseInt(request.getParameter("threads"));
            }

            ReferenceValues refVals = new ReferenceValues();

            if (geoChecked != null) {
                this.getGeoParametrs(request);
                refVals.setGeo(geoLatitude, geoLongitude);
            }

            if (likesCheck != null) {
                likes = Integer.parseInt(request.getParameter("likes"));
                refVals.setFavs(likes);
            }

            if (dateCheck != null) {
                DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                date = request.getParameter("date");
                Date refDate = format.parse(date);
                refVals.setDate(refDate);
            }

            if (name == null) {
                name = "No name";
            }

            HelpPrinter print = new HelpPrinter(this);
            print.printHeader(out);

            //do simple page reranking
            FlickrPhotoManipulator finder = new FlickrPhotoManipulator();
            PhotoList<Photo> photos = null;
            if (searchType.equals("fulltext")) {
                photos = finder.findPhotosByText(name);
            } else {
                photos = finder.findPhotosByTag(name);
            }

            if ((photos != null) && !photos.isEmpty()) {
                out.println("<div id=\"content\">");
                List<RankedPhoto> rankedList = new ArrayList<>();;
                PhotoInformationGetter[] workers = new PhotoInformationGetter[thnum];
                Thread[] executors = new Thread[thnum];
                int total = photos.size();
                System.out.println(Thread.currentThread().getId() + ": There are " + total + " photos.");

                int oneLoad = total / thnum;
                int beginPosition = 0;
                for (int i = 0; i < thnum; i++) {

                    int toadd = oneLoad;
                    if (i == (thnum - 1)) {
                        toadd = total - beginPosition;
                    }

                    workers[i] = new PhotoInformationGetter(beginPosition, toadd, photos, this, refVals, rankedList);
                    beginPosition += toadd;
                }

                System.out.println(Thread.currentThread().getId() + ": The threads are started" + " Total workers" + workers.length);

                for (int i = 0; i < thnum; i++) {

                    executors[i] = new Thread(workers[i]);
                    executors[i].start();

                }

                for (int i = 0; i < thnum; i++) {
                    executors[i].join();

                }
                System.out.println(Thread.currentThread().getId() + ": Workers finished");

                // print results before sort
                out.println("<div id=\"norank\">");

                int orig_position = 1;

                for (RankedPhoto rphoto : rankedList) {
                    rphoto.countRank(geoDegree, dateDegree, likesDegree, MaxValues.MAX_GCD, MaxValues.MAX_FAVS, MaxValues.MAX_DATE);
                    rphoto.printRank();
                    print.printResult(out, rphoto.p, 0, rphoto.rank, rphoto.favourites);
                    rphoto.setPos(orig_position);
                    orig_position++;
                }
                out.println("</div>");
                System.out.println("--------------ranked--------------");

                Collections.sort(rankedList, RankedPhoto.getCompByRank());
                out.println("<div id=\"rank\">");
                for (RankedPhoto p : rankedList) {
                    p.printRank();
                    // System.out.println("The lat: " + p.p.getGeoData().getLatitude() + " lon: " + p.p.getGeoData().getLongitude());
                    print.printResult(out, p.p, p.original_position, p.rank, p.favourites);
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
        } catch (InterruptedException ex) {
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
        } catch (InterruptedException ex) {
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
