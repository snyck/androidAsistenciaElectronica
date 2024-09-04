package com.snyck.asistenciaelectronica.configuracion.gps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.snyck.asistenciaelectronica.configuracion.Logg.Logg;
import com.snyck.asistenciaelectronica.configuracion.Singleton.NUsuarioSingleton;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GPSManager {
    private static final String TAG = GPSManager.class.getSimpleName();

    public interface LocationListener {
        void onGetLocation(LatLng latLng);
    }

    private static class LocationListenerClass {
        public boolean procesada = false;
        public LocationListener locationListener;
    }

    public static void getLocation(LocationListener locationListener, boolean conGoogle, Context context) {
        LocationListenerClass locationListenerClass= new LocationListenerClass();
        locationListenerClass.locationListener = locationListener;
        if (conGoogle) {
            GPSManager.getLocationGoogle(locationListenerClass, context);
        } else {
            GPSManager.getLocationSinGoogle(locationListenerClass, context);
        }
    }

    private static void checkLocationReceived(LocationListenerClass locationListenerClass, Location location) {
        LatLng latLng;
        if (location == null) {
            Logg.e(TAG, "Ubicacion: null");
            latLng = new LatLng(NUsuarioSingleton.getNUsuario().latitude, NUsuarioSingleton.getNUsuario().longitude);
        } else {
            Logg.e(TAG, "Ubicacion: " + location.getLatitude() + "," + location.getLongitude());
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        if(!locationListenerClass.procesada) {
            locationListenerClass.procesada = true;
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> locationListenerClass.locationListener.onGetLocation(latLng));
        }
    }

    @SuppressLint("MissingPermission")
    private static void getLocationGoogle(LocationListenerClass locationListenerClass, Context context) {
        Logg.i(TAG, "OBTENIENDO POSICION CON GOOGLE");
        Timer timer = new Timer();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                client.removeLocationUpdates(this);
                if (locationResult.getLastLocation() != null) {
                    synchronized (GPSManager.class) {
                        GPSManager.guardarPunto(locationResult.getLastLocation());
                    }
                    GPSManager.checkLocationReceived(locationListenerClass, locationResult.getLastLocation());
                } else
                    GPSManager.getLocationSinGoogle(locationListenerClass, context);
                Logg.e(TAG, "FINISHING TIMER AT:" + new Date().toLocaleString());
                timer.cancel();
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if (!locationAvailability.isLocationAvailable()) {
                    client.removeLocationUpdates(this);
                    GPSManager.getLocationSinGoogle(locationListenerClass, context);
                }
                Logg.e(TAG, "FINISHING TIMER AT: " + new Date().toLocaleString());
                timer.cancel();
            }
        };

        client.requestLocationUpdates(locationRequest, locationCallback, null);
        Logg.e(TAG, "BEGIN GPS: " + new Date().toLocaleString());
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Looper.prepare();
                client.removeLocationUpdates(locationCallback);
                GPSManager.getLocationSinGoogle(locationListenerClass, context);
                timer.cancel();
            }
        }, 5000);
    }

    @SuppressLint("MissingPermission")
    private static void getLocationSinGoogle(LocationListenerClass locationListenerClass, Context context) {
        Logg.i(TAG, "OBTENIENDO POSICION CON GPS PAX");
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setCostAllowed(false);
            criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setSpeedRequired(true);
            Timer timer = new Timer();
            String provider = LocationManager.NETWORK_PROVIDER;

            if (!locationManager.isProviderEnabled(provider)) {
                provider = LocationManager.GPS_PROVIDER;
            }
            if (locationManager.isProviderEnabled(provider)) {
                android.location.LocationListener locationListenerInter = new android.location.LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        synchronized (GPSManager.class) {
                            GPSManager.guardarPunto(location);
                        }
                        GPSManager.checkLocationReceived(locationListenerClass, location);
                        locationManager.removeUpdates(this);
                        timer.cancel();
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {
                        Logg.e(TAG, "El proveedor de GPS esta deshabilitado");
                        android.location.LocationListener.super.onProviderDisabled(provider);
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        checkLocationReceived(locationListenerClass,null);
                        locationManager.removeUpdates(this);
                        timer.cancel();
                    }

                    @Override
                    public void onFlushComplete(int requestCode) {
                        android.location.LocationListener.super.onFlushComplete(requestCode);
                    }

                    @Override
                    public void onLocationChanged(@NonNull List<Location> locations) {
                        android.location.LocationListener.super.onLocationChanged(locations);
                    }

                    @Override
                    public void onProviderEnabled(@NonNull String provider) {
                        android.location.LocationListener.super.onProviderEnabled(provider);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        android.location.LocationListener.super.onStatusChanged(provider, status, extras);
                    }
                };
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                locationManager.removeUpdates(locationListenerInter);
                                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (lastKnownLocation == null) {
                                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                }
                                if (lastKnownLocation != null) {
                                    synchronized (GPSManager.class) {
                                        GPSManager.guardarPunto(lastKnownLocation);
                                    }
                                }
                                GPSManager.checkLocationReceived(locationListenerClass,lastKnownLocation);
                            }
                        },
                        5000
                );
                locationManager.requestLocationUpdates(provider, 1000, 10, locationListenerInter);
            } else {
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation == null) {
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                GPSManager.checkLocationReceived(locationListenerClass, lastKnownLocation);
            }
        } catch (Exception e) {
            Logg.e(TAG, "Error al obtener el punto GPS desde dispositivo PAX: " + e);
            Handler mainHandler = new Handler(Looper.getMainLooper());
            checkLocationReceived(locationListenerClass,null);
            e.printStackTrace();
        }
    }

    private static void guardarPunto(Location location) {
        if (location != null) {
            NGUbicacionActual ubicacionActual = new NGUbicacionActual(location.getLatitude(), location.getLongitude());
            if (ubicacionActual.gpsValido()) {
                NUsuarioSingleton.getNUsuario().latitude = location.getLatitude();
                NUsuarioSingleton.getNUsuario().longitude = location.getLongitude();
                NUsuarioSingleton.update();
                if (GPSSingleton.getLocation() != null) {
                    GPSSingleton.getLocation().setLatitude(location.getLatitude());
                    GPSSingleton.getLocation().setLongitude(location.getLongitude());
                }
            }
        } else {
            Logg.e(TAG, "Location IS NULL");
        }
    }
}
