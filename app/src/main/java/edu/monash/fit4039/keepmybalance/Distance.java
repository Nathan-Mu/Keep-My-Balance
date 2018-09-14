package edu.monash.fit4039.keepmybalance;

/**
 * Created by nathan on 20/5/17.
 */

public class Distance {

    //calculate the distance between two points on earth (by longitude and latitude)
    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double a, b, R;
        R = 6378.137;
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (lon1 - lon2) * Math.PI / 180.0;
        double d;
        double sina2, sinb2;
        sina2 = Math.sin(a / 2.0);
        sinb2 = Math.sin(b / 2.0);
        d = 2 * R * Math.asin(Math.sqrt(sina2 * sina2 + Math.cos(lat1) * Math.cos(lat2) * sinb2 * sinb2));
        return d;
    }
}
