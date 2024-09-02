package com.snyck.asistenciaelectronica.config.preferences;

import android.content.Context;

public class UserPreferences extends Preferences{

    private final String KEY_Theme = "have_theme";

    public UserPreferences(Context context) {
        super(context);
    }

    public void guardaTheme(int value) {setInt(KEY_Theme,value);}

    public int getTheme(){
        return getInt(KEY_Theme);
    }
}
