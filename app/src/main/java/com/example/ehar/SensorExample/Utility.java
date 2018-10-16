package com.example.ehar.SensorExample;

import android.location.Location;

public class Utility {
    // calculates distance on a sphere
    // d = arccos(sin(lat1) sin(lat2) + cos(lat1) cos(lat2) cos(lon2 − lon1) ) R
    public static double greatCircleDistance(double lat1, double long1, double lat2, double long2){
        double latitude1 = Math.toRadians(lat1);
        double longitude1 = Math.toRadians(long1);
        double latitude2 = Math.toRadians(lat2);
        double longitude2 = Math.toRadians(long2);
        final double EARTH_RADIUS = 6371.0;
        double sins = Math.sin(latitude1)*Math.sin(latitude2);
        double longs = longitude2 - longitude1;
        double coss = Math.cos(latitude1)*Math.cos(latitude2)*Math.cos(longs);
        double arc = sins + coss;

        double d = Math.acos(arc);

        d *= EARTH_RADIUS;
        //return the distance
        return d;
    }

    public static double compasHeading(Location prev, Location curr) {
        double prevLat = Math.toRadians(prev.getLatitude());
        double prevLon = Math.toRadians(prev.getLongitude());
        double currLat = Math.toRadians(curr.getLatitude());
        double currLon = Math.toRadians(curr.getLongitude());
        return 0.0;
    }
}
