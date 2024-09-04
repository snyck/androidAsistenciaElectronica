package com.snyck.asistenciaelectronica.configuracion.Singleton;

import android.util.Log;

import com.snyck.asistenciaelectronica.adapters.NUsuario;

public class NUsuarioSingleton {

    private NUsuario usuario;
    private static volatile NUsuarioSingleton instance;

    private static final String TAG = NUsuarioSingleton.class.getSimpleName();

    /**
     * Constructor del Singleton
     * */
    private NUsuarioSingleton() {}

    /**
     * Metodo para crear instancia del Singleton
     * */
    private static NUsuarioSingleton sharedInstance() {
        if (instance == null) {
            instance = new NUsuarioSingleton();
        }
        return instance;
    }

    /**
     * Metodo para obteber instancia del Singleton
     * */
    public synchronized static NUsuario getNUsuario(){
        return sharedInstance().usuario;
    }

    /**
     * Metodo para destruir NUsuario
     * */
    public static void destroyNCEmpleado() {
        sharedInstance().usuario = null;
    }

    /**
     * Metodo para crear NUsuario
     * */
    public synchronized static void getInstance() {
        if (sharedInstance().usuario != null) {
            destroyNCEmpleado();
        }
        instance.usuario = new NUsuario();
        Log.e(TAG,"Se creo instancia de NUsuario");
    }

    /**
     * Metodo para actualizar NUsuario en modo de cola
     * */
    public synchronized static void update() {
        instance.usuario.update();
    }

}
