package com.snyck.asistenciaelectronica.configuracion.gps;

import android.location.Location;

public interface GPSListener {
    void onLocationChange(Location location);
}
