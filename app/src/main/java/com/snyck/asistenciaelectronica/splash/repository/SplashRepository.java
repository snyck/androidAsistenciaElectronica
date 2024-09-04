package com.snyck.asistenciaelectronica.splash.repository;


import com.google.gson.JsonObject;
import com.snyck.asistenciaelectronica.configuracion.Services.BaseCommunication;

import org.json.JSONException;

public class SplashRepository extends BaseCommunication {


    public String getServerVersionApp(double version ) throws JSONException {
        String contextUrl = "splash/";

        JsonObject petition = new JsonObject();
        petition.addProperty("version_app", version);

        if(responsePostParam(contextUrl, "version", petition)){
            return json.toString();
        }
        return "";
    }
}
