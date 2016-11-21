/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vmmflick.flickrwithmaven;

import com.flickr4java.flickr.photos.GeoData;
import com.flickr4java.flickr.photos.Photo;

/**
 *
 * @author Veronika references:
 * http://www.movable-type.co.uk/scripts/latlong-vincenty.html
 */
public class GCDAlgorithm {

    private static double a = 6378137.0;
    private static double b = 6356752.314245;
    private static double invF = 298.257223563;

    public static double countGCD(GeoData p, double lat, double lon) {
        double f = 1 / invF;
        double dLon = Math.toRadians(p.getLongitude()) - Math.toRadians(lon);
        double tanU1 = (1 - f) * Math.tan(Math.toRadians(p.getLatitude()));
        double tanU2 = (1 - f) * Math.tan(Math.toRadians(lat));
        // cosU1 = 1 / Math.sqrt((1 + tanU1*tanU1)), sinU1 = tanU1 * cosU1;
        double cosU1 = 1 / Math.sqrt(1 + tanU1 * tanU1);
        double sinU1 = tanU1 * cosU1;
        //cosU2 = 1 / Math.sqrt((1 + tanU2*tanU2)), sinU2 = tanU2 * cosU2;
        double cosU2 = 1 / Math.sqrt(1 + tanU2 * tanU2);
        double sinU2 = tanU2 * cosU2;
        /*
    var λ = L, λʹ, iterationLimit = 100;
do {
    var sinλ = Math.sin(λ), cosλ = Math.cos(λ);
    var sinSqσ = (cosU2*sinλ) * (cosU2*sinλ) + (cosU1*sinU2-sinU1*cosU2*cosλ) * (cosU1*sinU2-sinU1*cosU2*cosλ);
    var sinσ = Math.sqrt(sinSqσ);
    if (sinσ==0) return 0;  // co-incident points
    var cosσ = sinU1*sinU2 + cosU1*cosU2*cosλ;
    var σ = Math.atan2(sinσ, cosσ);
    var sinα = cosU1 * cosU2 * sinλ / sinσ;
    var cosSqα = 1 - sinα*sinα;
    var cos2σM = cosσ - 2*sinU1*sinU2/cosSqα;
    if (isNaN(cos2σM)) cos2σM = 0;  // equatorial line: cosSqα=0 (§6)
    var C = f/16*cosSqα*(4+f*(4-3*cosSqα));
    λʹ = λ;
    λ = L + (1-C) * f * sinα * (σ + C*sinσ*(cos2σM+C*cosσ*(-1+2*cos2σM*cos2σM)));
} while (Math.abs(λ-λʹ) > 1e-12 && --iterationLimit>0);
         */
        double lambda = dLon, lambda2 = 0, iterLimit = 100;
        double cosSqAlf = 0, cosSig = 0, cos2SigM = 0, sinSig = 0, sig = 0, sinLamb = 0, cosLamb = 0;
        do {
            sinLamb = Math.sin(lambda);
            cosLamb = Math.cos(lambda);
            double sinSqsig = (cosU2 * sinLamb) * (cosU2 * sinLamb) + (cosU1 * sinU2 - sinU2 * cosU2 * cosLamb) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLamb);
            sinSig = Math.sqrt(sinSqsig);
            if (sinSig == 0) {
                return 0;
            }
            cosSig = sinU1 * sinU2 + cosU1 * cosU2 * cosLamb;
            sig = Math.atan2(sinSig, cosSig);
            double sinAlf = cosU1 * cosU2 * sinLamb / sinSig;
            cosSqAlf = 1 - sinAlf * sinAlf;
            cos2SigM = cosSig - 2 * sinU1 * sinU2 / cosSqAlf;
            if (Double.isNaN(cos2SigM)) {
                cos2SigM = 0;
            }
            double c = f / 16 * cosSqAlf * (4 + f * (4 - 3 * cosSqAlf));
            lambda2 = lambda;
            lambda = dLon + (1 - c) * f * sinAlf * (sig + c * sinSig * (cos2SigM + c * cosSig * (-1 + 2 * cos2SigM * cos2SigM)));
        } while ((Math.abs(lambda - lambda2) > 0.000000000001) && --iterLimit > 0);
        //if (iterationLimit==0) throw new Error('Formula failed to converge');
        if (iterLimit == 0) {
            throw new Error("Formula failed to converge");
        }
        /*
    var uSq = cosSqα * (a*a - b*b) / (b*b);
var A = 1 + uSq/16384*(4096+uSq*(-768+uSq*(320-175*uSq)));
var B = uSq/1024 * (256+uSq*(-128+uSq*(74-47*uSq)));
var Δσ = B*sinσ*(cos2σM+B/4*(cosσ*(-1+2*cos2σM*cos2σM)-
    B/6*cos2σM*(-3+4*sinσ*sinσ)*(-3+4*cos2σM*cos2σM)));

var s = b*A*(σ-Δσ);

var fwdAz = Math.atan2(cosU2*sinλ,  cosU1*sinU2-sinU1*cosU2*cosλ);
var revAz = Math.atan2(cosU1*sinλ, -sinU1*cosU2+cosU1*sinU2*cosλ);
         */
        double uSq = cosSqAlf * (a * a - b * b) / (b * b);
        double a2 = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double b2 = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double dSig = b2 * sinSig * (cos2SigM + b2 / 4 * (cosSig * (-1 + 2 * cos2SigM * cos2SigM) - b2 / 6 * cos2SigM * (-3 + 4 * sinSig * sinSig) * (-3 + 4 * cos2SigM * cos2SigM)));

        double s = b * a2 * (sig - dSig);

        double fwdAz = Math.atan2(cosU2 * sinLamb, cosU1 * sinU2 - sinU1 * cosU2 * cosLamb);
        double revAz = Math.atan2(cosU1 * sinLamb, -sinU1 * cosU2 + cosU1 * sinU2 * cosLamb);

        /*
    α1 = (α1 + 2*Math.PI) % (2*Math.PI); // normalise to 0..360
    α2 = (α2 + 2*Math.PI) % (2*Math.PI); // normalise to 0..360

    s = Number(s.toFixed(3)); // round to 1mm precision
         */
        fwdAz = (fwdAz + 2 * Math.PI) % (2 * Math.PI);
        revAz = (revAz + 2 * Math.PI) % (2 * Math.PI);

        return s;
    }
}
