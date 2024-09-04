package com.snyck.asistenciaelectronica.configuracion.utils;

public class UTLocation {

    public static double distanceFromLocations(double latitudeOrig, double longitudeOrig, double latitudeDest, double longitudeDest) {
        final double R = 6371.0; // Radius of the earth

        double latDistance = Math.toRadians(latitudeDest - latitudeOrig);
        double lonDistance = Math.toRadians(longitudeDest - longitudeOrig);
        double a = Math.sin(latDistance / 2.0) * Math.sin(latDistance / 2.0)
                + Math.cos(Math.toRadians(latitudeOrig)) * Math.cos(Math.toRadians(latitudeDest))
                * Math.sin(lonDistance / 2.0) * Math.sin(lonDistance / 2.0);
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        double distance = R * c * 1000.0; // convert to meters

        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }
}
