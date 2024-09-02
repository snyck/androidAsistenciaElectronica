package com.snyck.asistenciaelectronica.adapters;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.snyck.asistenciaelectronica.config.utils.UTJson;
import com.snyck.asistenciaelectronica.config.utils.Utilities;

import java.io.Serializable;
import java.util.ArrayList;

public class NUsuario implements Serializable {

    public String userID;
    public String userName;
    public String nameUser;
    public String userPass;
    public double latitude;
    public double longitude;

    public int userRole;
    public String userSucursal;
    public String pathFirmaAuditor;
    public String pathProfileAuditor;
    public String pathFirmaEncargado;
    public String n_licencia;
    public String nameRole;
    public int cargo_ruta;
    public String ruta_fecha;
    public int connection;
    public int modificoGafete;
    public ArrayList<Integer> modulosCorreos;
    public int synchronise;
    final String FILENAME = "DataUser.json";

    public NUsuario() {
        init();
        loadInfoUser();
    }

    NUsuario(boolean loadData) {
        init();
        if (loadData)
            loadInfoUser();
    }

    private void init() {
        userID = "";
        userName = "";
        nameUser = "";
        userPass = "";
        userRole = 0;
        pathFirmaAuditor = "";
        pathProfileAuditor = "";
        pathFirmaEncargado = "";
        latitude = 0.0;
        longitude = 0.0;
        modificoGafete = 0;
        n_licencia = "";
        nameRole = "";
        cargo_ruta = 0;
        ruta_fecha = "";
        connection = 0;
        synchronise = 0;
        modulosCorreos = new ArrayList<>();
    }

    public void update() {
        Gson gson = new Gson();
        String cadenaJson = gson.toJson(this);
        Utilities.saveFile(FILENAME, cadenaJson);
    }

    private void loadInfoUser() {
        if (!Utilities.fileExists(FILENAME))
            update();

        if (Utilities.fileExists(FILENAME)) {
            String cadena = Utilities.readFile(FILENAME);
            JsonObject usuarioJson = cadena.isEmpty() ? new JsonObject() : new Gson().fromJson(cadena, JsonObject.class);
            userID = UTJson.isJsonStringValideDefault(usuarioJson, "userID", "");
            userName = UTJson.isJsonStringValideDefault(usuarioJson, "userName", "");
            nameUser = UTJson.isJsonStringValideDefault(usuarioJson, "nameUser", "");
            userPass = UTJson.isJsonStringValideDefault(usuarioJson, "userPass", "");
            userRole = UTJson.isJsonIntValideDefault(usuarioJson, "userRole", 0);
            userSucursal = UTJson.isJsonStringValideDefault(usuarioJson, "userSucursal", "");
            pathFirmaAuditor = UTJson.isJsonStringValideDefault(usuarioJson, "pathFirmaAuditor", "");
            pathProfileAuditor = UTJson.isJsonStringValideDefault(usuarioJson, "pathProfileAuditor", "");
            pathFirmaEncargado = UTJson.isJsonStringValideDefault(usuarioJson, "pathFirmaEncargado", "");
            latitude = UTJson.isJsonDoubleValideDefault(usuarioJson, "latitude", 0.0);
            longitude = UTJson.isJsonDoubleValideDefault(usuarioJson, "longitude", 0.0);
            modificoGafete = UTJson.isJsonIntValideDefault(usuarioJson, "modificoGafete", 0);
            n_licencia = UTJson.isJsonStringValideDefault(usuarioJson, "n_licencia", "");
            nameRole = UTJson.isJsonStringValideDefault(usuarioJson, "nameRole", "");
            cargo_ruta = UTJson.isJsonIntValideDefault(usuarioJson, "cargo_ruta", 0);
            ruta_fecha = UTJson.isJsonStringValideDefault(usuarioJson, "ruta_fecha", "");
            connection = UTJson.isJsonIntValideDefault(usuarioJson, "connection", 0);
            synchronise = UTJson.isJsonIntValideDefault(usuarioJson, "synchronise", 0);
        }
    }
}
