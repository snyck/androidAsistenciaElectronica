package com.snyck.asistenciaelectronica.configuracion.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private SharedPreferences preferences;

    public Preferences(Context context) {
        preferences = context.getSharedPreferences("AppsPersonalPreferences", Context.MODE_PRIVATE);
    }

    void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }
    void setString(String key, String value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
    String getString(String key){return preferences.getString(key,"");}

    void deleteString(String key, String value){
        SharedPreferences.Editor editor = preferences.edit();
        //editor.remove(key);
        //editor.commit();
        //editor.putString(key,"");
        editor.clear();
        editor.apply();
    }
    void setInt(String key, int value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key,value);
        editor.apply();
    }
    int getInt(String key){return preferences.getInt(key,0);}

}
