package com.snyck.asistenciaelectronica.adapters;

import com.google.gson.JsonObject;
import com.snyck.asistenciaelectronica.configuracion.utils.UTJson;

import java.io.Serializable;

public class NUsuarioMapas implements Serializable {
    public int tipoMapa;
    public int tipoTiles;
    public int fuenteTiles;
    public boolean simpliRoute;
    public double lat;
    public double lng;
    public int distanciaGestDomCte;
    public boolean rutaOptimizada;

    public NUsuarioMapas() {
        this.tipoMapa = 0;
        this.tipoTiles = 200;
        this.fuenteTiles = 0;
        this.simpliRoute = true;
        this.lat = 0.0d;
        this.lng = 0.0d;
        distanciaGestDomCte = 100;
        rutaOptimizada = false;
    }

    public void loadInfo(JsonObject json) {
        tipoMapa = UTJson.isJsonIntValideDefault(json, "tipoMapa", 0);
        tipoTiles = UTJson.isJsonIntValideDefault(json, "tipoTiles", 300);
        fuenteTiles = UTJson.isJsonIntValideDefault(json, "fuenteTiles", 0);
        simpliRoute = UTJson.isJsonBooleanValideDefault(json, "simpliRoute", true);
        lat = UTJson.isJsonDoubleValideDefault(json,"lat",0.0d);
        lng = UTJson.isJsonDoubleValideDefault(json,"lng",0.0d);
        distanciaGestDomCte = UTJson.isJsonIntValideDefault(json,"distanciaGestDomCte",100);
        rutaOptimizada = UTJson.isJsonBooleanValideDefault(json,"rutaOptimizada", false);
    }
}
