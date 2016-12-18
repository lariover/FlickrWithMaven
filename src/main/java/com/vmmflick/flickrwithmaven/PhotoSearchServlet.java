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
 * The main servlet class, extracts parameters from the input form and calls other methods
 * also does some of the html outputting
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

	/*
	* Loads geoParameters from the input
	*/
	
    private void getGeoParametrs(HttpServletRequest request) {
        geoLatitude = Double.parseDouble(request.getParameter("latitude"));
        geoLongitude = Double.parseDouble(request.getParameter("longitude"));
    }
	
	/*
	* Loads basic parameters from the input - query, which metadata to use and priorities
	*/

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

	/*
	* Main servlet method called by the overridden post and get methods
	*/
	
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, FlickrException, ParseException, InterruptedException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            this.extractParametersFromRequest(request);
			
			/*
			* Load number of threads (default = 1)
			*/
            String tnum = request.getParameter("threads");
            int thnum = 1;
            if ((tnum != null) && !(tnum.equals(""))) {
                thnum = Integer.parseInt(request.getParameter("threads"));
            }

            ReferenceValues refVals = new ReferenceValues();

			/*
			* Load required metadata if they were checked in the query form
			*/
			
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
			/*
			* Print the header of output html with query information, simple menu and description box
			*/
            print.printHeader(out);

            FlickrPhotoManipulator finder = new FlickrPhotoManipulator();
            PhotoList<Photo> photos = null;
			/*
			* Search for the basic photos
			*/
            if (searchType.equals("fulltext")) {
                photos = finder.findPhotosByText(name);
            } else {
                photos = finder.findPhotosByTag(name);
            }

			/*
			* For each found photo requests the required metadata from Flickr API
			* uses threads to distribute the work evenly
			*/
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

                out.println("<div id=\"norank\">");

                int orig_position = 1;

				/*
				* Count the rank of each of the photo in the list, after that, print the photo (the list is not yet sorted)
				* add original position for each photo, omits it in the current description (it's irrelevant in unsorted list)
				*/
				
                for (RankedPhoto rphoto : rankedList) {
                    rphoto.countRank(geoDegree, dateDegree, likesDegree, MaxValues.MAX_GCD, MaxValues.MAX_FAVS, MaxValues.MAX_DATE);
                    rphoto.printRank();
                    print.printResult(out, rphoto.p, 0, rphoto.rank, rphoto.favourites);
                    rphoto.setPos(orig_position);
                    orig_position++;
                }
                out.println("</div>");
                System.out.println("--------------ranked--------------");

				/*
				* Sort the list and print the results, this time with original position printed (for comparison with original)
				*/
                Collections.sort(rankedList, RankedPhoto.getCompByRank());
                out.println("<div id=\"rank\">");
                for (RankedPhoto p : rankedList) {
                    p.printRank();
                    print.printResult(out, p.p, p.original_position, p.rank, p.favourites);
                }
                out.println("</div>");

            } else {

                out.println(
                        "No photos with tags " + name + " found!");
            }

			/*
			* Closing tags of the generated html page
			*/
            out.println(
                    "</body>");
            out.println(
                    "</html>");

        }
    }

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
