package com.snyck.asistenciaelectronica.config.preferences;

import android.content.Context;

public class TokenSesionPreferences extends Preferences {
    private final String KEY_T = "have_token_sesion";
    private final String KEY_TK = "have_token_authorize";
    private final String KEY_USER = "have_user";
    private final String KEY_PASS = "have_pass";


    public TokenSesionPreferences(Context context) {
        super(context);
    }
    public void guardaToken(String value) {setString(KEY_T,value);}
    public String getToken(){
        return getString(KEY_T);
    }
    public void guardaTokenAuth(String value) {setString(KEY_TK,value);}
    public String getTokenAuth(){
        return getString(KEY_TK);
    }

    public void guardaUserSession(String value) {setString(KEY_USER,value);}
    public String getUserSession(){
        return getString(KEY_USER);
    }

    public void guardaPassSession(String value) {setString(KEY_PASS,value);}
    public String getPassSession(){
        return getString(KEY_PASS);
    }

    public void deleteToken() {deleteString(KEY_T,"");}
}
