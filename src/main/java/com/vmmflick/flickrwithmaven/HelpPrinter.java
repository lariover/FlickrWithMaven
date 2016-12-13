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
    public void printHeader(PrintWriter out , String tags){
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Metadata reranking of" + tags +"</title>");
         out.println("<link rel=\"stylesheet\" href=\"./index.css\">");
        out.println("<script src=\"http://code.jquery.com/jquery-latest.min.js\"></script>"+
        "<!-- jquery for hide / show button -->" +
        "<script type=\"text/javascript\">"+
        "window.onload=function(){document.getElementById('hideshow').value='Show ranked results';}"+
        "</script>"+
        "<script>" +
        "jQuery(document).ready(function(){" +
            "jQuery('#hideshow').on('click', function(event) {" +
                "jQuery('#norank').toggle('show');" +
                "jQuery('#rank').toggle('show');" +
                "if(this.value == 'Show unranked results'){"+
                	"this.value = 'Show ranked results';} else {"+
                	"this.value = 'Show unranked results';};"+               
                "}"+
            ");"+
        "});"+
        "</script>");
        out.println("</head>");
        out.println("<body>");
    }

    public void printTitle(PrintWriter out, String tags){
        out.print("<h1>Search request for "+ tags);
        out.println("</h1>");
        out.println("<a href=\"./form.jsp\">Return to search form</a>");
        out.println("<input type='button' id='hideshow' value='Still loading images...'>");
        out.println("<hr>");

        //out.println("<a href=\".\">Return to search form</a>");
    }

}
