package com.snyck.asistenciaelectronica.configuracion.gps;

import com.snyck.asistenciaelectronica.configuracion.utils.UTLocation;

import java.io.Serializable;

public class NGUbicacionActual implements Serializable {

    public double latitude;
    public double longitude;

    public NGUbicacionActual(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public boolean gpsValido() {
        return ((latitude >= 14.0 && latitude <= 33.0) && (longitude >= -118.0 && longitude <= -86.0));
    }

    public static boolean gpsValido(double Latitud,double Longitud) {
        return ((Latitud >= 14.0 && Latitud <= 33.0) && (Longitud >= -118.0 && Longitud <= -86.0));
    }

    public static boolean coordenadasValidas(double latitud, double longitud) {
        return latitud != 0.0 && longitud != 0.0 && latitud != 0.1 && longitud != -0.1;
    }

    public static double calcularDistancia(double latitudeOrigin, double longitudeOrigin, double latitudeDestiny, double longitudeDestiny) {
        return UTLocation.distanceFromLocations(latitudeOrigin, longitudeOrigin, latitudeDestiny, longitudeDestiny);
    }
}
