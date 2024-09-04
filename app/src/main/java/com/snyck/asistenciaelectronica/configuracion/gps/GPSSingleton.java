package com.snyck.asistenciaelectronica.configuracion.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.snyck.asistenciaelectronica.configuracion.Logg.Logg;
import com.snyck.asistenciaelectronica.configuracion.Singleton.NUsuarioSingleton;

import java.util.List;
import java.util.Locale;

public class GPSSingleton {

    private static volatile GPSSingleton instance;
    private Location location;
    private GPSListener listener;
    private static LocationManager locationManager;
    private static LocationRequest locationRequest;
    private static LocationCallback locationCallback;
    private static final int expiraTime = 20 * 1000;                       //20 segundos
    private static final long LOCATION_REFRESH_TIME = 1000;
    private static final float LOCATION_REFRESH_DISTANCE = 0;

    private static final String TAG = GPSSingleton.class.getSimpleName();

    /**
     * Constructor del Singleton
     * */
    private GPSSingleton() {
        location = new Location("");
        location.setLatitude(NUsuarioSingleton.getNUsuario().mapa.lat);
        location.setLongitude(NUsuarioSingleton.getNUsuario().mapa.lng);
    }

    /**
     * Metodo para crear instancia del Singleton
     * */
    private static GPSSingleton sharedInstance() {
        if (instance == null)
            instance = new GPSSingleton();
        return instance;
    }

    /**
     * Metodo para destruir Location
     * */
    public static void destroyGPS() {
        sharedInstance().location = null;
    }

    /**
     * Metodo para obtener puntos GPS por sensor
     * */
    public static void getGPSSingleton(Context context) {
        configurarUbicacion(context);
    }

    /**
     *  Metodo de retorno para puntos GPS
     * */
    public static Location getLocation() {
        Location location = sharedInstance().location;
        Log.i(TAG, String.format(Locale.US, "Puntos GPS %f,%f", location.getLatitude(), location.getLongitude()));

        return location;
    }

    /**
     *  Metodo para obtener puntos GPS Google
     * */
    public static void getGoogleLocation(Context context) {
        try {
            configurarUbicacionGoogle(context);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     *  Metodo para crear instancia de listener
     * */
    public static void createListener(GPSListener gpsListener) {
        sharedInstance().listener = gpsListener;
    }

    /**
     *  Metodo para destruir instancia de listener
     * */
    public static void destroyListener() {
        sharedInstance().listener = null;
    }

    /**
     * Metodos para obtener puntos GPS
     * */
    private static void configurarUbicacion(Context context) {
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Logg.i(TAG, "No esta habilitados los accesos de localización");
            } else {
                Location location = locationManager.getLastKnownLocation(provider);
                if (location == null) {
                    continue;
                }
                if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = location;
                }
            }
        }
        guardarPunto(bestLocation);

        if (bestLocation == null && locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener);
        }
    }

    private static final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            if (locationManager != null && locationListener != null) {
                guardarPunto(location);
                if (sharedInstance().listener != null) {
                    sharedInstance().listener.onLocationChange(location);
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    private static void guardarPunto(Location location) {
        if (location != null) {
            NGUbicacionActual ubicacionActual = new NGUbicacionActual(location.getLatitude(), location.getLongitude());
            if (ubicacionActual.gpsValido()) {
                sharedInstance().location = location;
                NUsuarioSingleton.getNUsuario().mapa.lat = location.getLatitude();
                NUsuarioSingleton.getNUsuario().mapa.lng = location.getLongitude();
                NUsuarioSingleton.update();
            }
        }
    }

    /**
     * Consulta de GPS por Google
     */
    @SuppressLint("MissingPermission")
    private static void configurarUbicacionGoogle(Context context) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        configurarCallback(fusedLocationProviderClient);
        configurarRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null));
        task.addOnFailureListener(e -> Log.e(TAG, "Error al obtener la ubicación por google"));
    }

    private static void configurarCallback(FusedLocationProviderClient fusedLocationProviderClient) {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    if (fusedLocationProviderClient != null && locationCallback != null) {
                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                    }
                    Location location = locationResult.getLastLocation();
                    NGUbicacionActual ubicacionActual = new NGUbicacionActual(location.getLatitude(), location.getLongitude());
                    if (ubicacionActual.gpsValido()) {
                        guardarPunto(location);
                    }
                }
            }
        };
    }

    private static void configurarRequest() {
        int interval = 1000;                        // 1 segundo
        locationRequest = new LocationRequest();
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(interval / 2);
        locationRequest.setSmallestDisplacement(1); // 1 metro
        locationRequest.setExpirationDuration(expiraTime);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}
