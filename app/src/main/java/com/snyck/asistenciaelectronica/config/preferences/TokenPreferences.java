package com.snyck.asistenciaelectronica.config.preferences;

import android.content.Context;

public class TokenPreferences extends Preferences{
    private final String KEY_T = "have_token";
    public TokenPreferences(Context context) {
        super(context);
    }
    public void guardaToken(String value) {setString(KEY_T,value);}
    public String getToken(){
        return getString(KEY_T);
    }
}
