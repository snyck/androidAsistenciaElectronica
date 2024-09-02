package com.snyck.asistenciaelectronica.ui.login.repository;

import static com.snyck.asistenciaelectronica.BuildConfig.PREFIJO;
import static com.snyck.asistenciaelectronica.BuildConfig.VERSION_CODE;

import com.google.gson.JsonObject;
import com.snyck.asistenciaelectronica.config.Services.BaseCommunication;
import com.snyck.asistenciaelectronica.config.utils.Utilities;
import com.snyck.asistenciaelectronica.ui.login.models.LoginModels;
import com.snyck.asistenciaelectronica.config.Singleton.NUsuarioSingleton;

import org.json.JSONException;


public class LoginRepository extends BaseCommunication {

    public LoginModels.LoginTipoAcceso validaEmpleado() {
        if (NUsuarioSingleton.getNUsuario() == null || NUsuarioSingleton.getNUsuario().userID.trim().isEmpty())
            return LoginModels.LoginTipoAcceso.LoginTipoPrimerAcceso;
        else
            return LoginModels.LoginTipoAcceso.LoginTipoSegundoAcceso;
    }

    public boolean userEmpty() {
        return NUsuarioSingleton.getNUsuario() == null || NUsuarioSingleton.getNUsuario().userID.isEmpty();
    }

    public String getLogin(String usuario, String contra, String token) throws JSONException {
        String contextUrl = "login/";

        JsonObject petition = new JsonObject();
        petition.addProperty("username", Utilities.getBase64(usuario));
        petition.addProperty("password", Utilities.getBase64(contra));
        petition.addProperty("version", VERSION_CODE);
        petition.addProperty("token", token);
        petition.addProperty("platform", 2);

        if(responsePostParam(contextUrl,"auth", petition)){
            return json.toString();
        }
        return "";
    }

    public String getLoginActive(String usuario,String tokenAuth, String TokenSesion) throws JSONException {
        String contextUrl = "login/";

        String aut = PREFIJO + " " + tokenAuth;

        JsonObject petition = new JsonObject();
        petition.addProperty("username_app",Utilities.getBase64(usuario));
        petition.addProperty("token",TokenSesion);

        if(responsePostParamAuth(contextUrl,"active", petition,aut)){
            return json.toString();
        }
        return "";
    }

    public String getNombreAuditor() {
        String[] nombre = NUsuarioSingleton.getNUsuario().userName.split(" ");
        return nombre[0];
    }
}
