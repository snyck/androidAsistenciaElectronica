package com.snyck.asistenciaelectronica.ui.login.repository;

import static com.snyck.asistenciaelectronica.BuildConfig.PREFIJO;
import static com.snyck.asistenciaelectronica.BuildConfig.VERSION_CODE;

import android.content.Context;

import com.google.gson.JsonObject;
import com.snyck.asistenciaelectronica.configuracion.Services.BaseCommunication;
import com.snyck.asistenciaelectronica.configuracion.utils.Utilities;
import com.snyck.asistenciaelectronica.ui.login.models.LoginModels;
import com.snyck.asistenciaelectronica.configuracion.Singleton.NUsuarioSingleton;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;


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

    public String getLogin(String usuario, String token, Context mContext) throws JSONException {
        String contextUrl = "login/";

        JsonObject petition = new JsonObject();
        petition.addProperty("cost_center", Utilities.getBase64(usuario));
        petition.addProperty("version", VERSION_CODE);
        petition.addProperty("token", token);
        petition.addProperty("mac", Utilities.getBase64(Utilities.getMac(mContext)));

        if(responsePostParam(contextUrl,"active_device", petition)){
            return json.toString();
        }
        return "";
    }

    public String getLoginActive(String usuario,String tokenAuth, String TokenSesion) throws JSONException {
        String contextUrl = "login/";

        String aut = PREFIJO + " " + tokenAuth;

        JsonObject petition = new JsonObject();
        petition.addProperty("user_app",Utilities.getBase64(usuario));
        petition.addProperty("cost_center_app",Utilities.getBase64(NUsuarioSingleton.getNUsuario().userID));
        petition.addProperty("token",TokenSesion);

        if(responsePostParamAuth(contextUrl,"auth", petition,aut)){
            return json.toString();
        }
        return "";
    }

    public String getCostCenterActive() {
        String contextUrl = "catalogo/";
        Map<String, String> parameter = new HashMap<>();
        parameter.put("cost_center", "1");

        return responseGet(contextUrl, "getCostCenter", parameter);
    }

    public boolean checkCostCenter() {
        return NUsuarioSingleton.getNUsuario().userID.isEmpty();
    }

    public String getFingerService(byte[] img, String tokenAuth) throws JSONException {
        String contextUrl = "login/";

        String aut = PREFIJO + " " + tokenAuth;

        JsonObject petition = new JsonObject();
        petition.addProperty("user_app",Utilities.getBase64(""));
        petition.addProperty("cost_center_app",Utilities.getBase64(NUsuarioSingleton.getNUsuario().userID));

        if(responsePostParamAuth(contextUrl,"auth", petition,aut)){
            return json.toString();
        }
        return "";
    }
}
