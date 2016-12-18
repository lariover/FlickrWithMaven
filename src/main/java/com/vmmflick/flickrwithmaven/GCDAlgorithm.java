
package com.vmmflick.flickrwithmaven;

import com.flickr4java.flickr.photos.GeoData;

/**
 *
 * @author references:
 * http://www.movable-type.co.uk/scripts/latlong-vincenty.html
 * 
 * 
 * The class is responsible for counting the distance of 2 point using GCD
 */
public class GCDAlgorithm {

    private static double a = 6378137.0;
    private static double b = 6356752.314245;
    private static double invF = 298.257223563;
    private static double R= 6371; //km
    
    /**
     * The algorithm counts the distance using less precise algorithm
    */
    public double countSimpleGCD(GeoData p, double lat, double lon){
        double lat2=Math.toRadians(p.getLatitude());
        double lon2=Math.toRadians(p.getLongitude());
        double lat1=Math.toRadians(lat);
        double lon1=Math.toRadians(lon);
        double dLat = Math.toRadians(p.getLatitude()-lat);
        double dLon = Math.toRadians(p.getLongitude()-lon);
        
          double a = Math.sin(dLat/2)*Math.sin(dLat/2)+Math.cos(lat1)*Math.cos(lat2)*Math.sin(dLon/2)*Math.sin(dLon/2);

        // great circle distance in radians
        double c = 2 * Math.atan2(Math.sqrt(a),Math.sqrt(1-a));

        // convert back to degrees
        

        // each degree on a great circle of Earth is 60 nautical miles
        
        return R*c;
    }
    /**
     * The algorithm counts distance based on Vincenty formula
    */
    public  double countGCD(GeoData p, double lat, double lon) {
        double f = (a-b)/a;
        double dLon = Math.toRadians(p.getLongitude()) - Math.toRadians(lon);
        double tanU2 = (1 - f) * Math.tan(Math.toRadians(p.getLatitude()));
        double tanU1 = (1 - f) * Math.tan(Math.toRadians(lat));
        double cosU1 = 1 / Math.sqrt(1 + tanU1 * tanU1);
        double sinU1 = tanU1 * cosU1;
        double cosU2 = 1 / Math.sqrt(1 + tanU2 * tanU2);
        double sinU2 = tanU2 * cosU2;

        double lambda = dLon, lambda2 = 0, iterLimit =0;
        double cosSqAlf = 0, cosSig = 0,sinSqsig=0,sinAlf=0,c=0, cos2SigM = 0, sinSig = 0, sig = 0, sinLamb = 0, cosLamb = 0;
        do {
            sinLamb = Math.sin(lambda);
            cosLamb = Math.cos(lambda);
            sinSqsig = (cosU2 * sinLamb) * (cosU2 * sinLamb) + (cosU1 * sinU2 - sinU1 * cosU2 * cosLamb) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLamb);
            sinSig = Math.sqrt(sinSqsig);
            if (sinSig == 0) {
                return 0;
            }
            cosSig = sinU1 * sinU2 + cosU1 * cosU2 * cosLamb;
            sig = Math.atan2(sinSig, cosSig);
            sinAlf = cosU1 * cosU2 * sinLamb / sinSig;
            cosSqAlf = 1 - sinAlf * sinAlf;
            cos2SigM = cosSig - 2 * sinU1 * sinU2 / cosSqAlf;
            if (Double.isNaN(cos2SigM)) {
                cos2SigM = 0;
            }
            c = f / 16 * cosSqAlf * (4 + f * (4 - 3 * cosSqAlf));
            lambda2 = lambda;
            lambda = dLon + (1 - c) * f * sinAlf * (sig + c * sinSig * (cos2SigM + c * cosSig * (-1 + 2 * cos2SigM * cos2SigM)));
        } while ((Math.abs(lambda - lambda2) > 0.000000000001) && ++iterLimit < 200);
        if (iterLimit >= 200) {
            throw new Error("Formula failed to converge");
        }

        double uSq = cosSqAlf * (a * a - b * b) / (b * b);
        double a2 = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double b2 = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double dSig = b2 * sinSig * (cos2SigM + b2 / 4 * (cosSig * (-1 + 2 * cos2SigM * cos2SigM) - b2 / 6 * cos2SigM * (-3 + 4 * sinSig * sinSig) * (-3 + 4 * cos2SigM * cos2SigM)));

        double s = b * a2 * (sig - dSig);

        double fwdAz = Math.atan2(cosU2 * sinLamb, cosU1 * sinU2 - sinU1 * cosU2 * cosLamb);
        double revAz = Math.atan2(cosU1 * sinLamb, -sinU1 * cosU2 + cosU1 * sinU2 * cosLamb);

        return s;
    }
}
