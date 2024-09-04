package com.snyck.asistenciaelectronica.menuconfig.repository;

import android.util.Pair;

import com.snyck.asistenciaelectronica.configuracion.Singleton.NUsuarioSingleton;
import com.snyck.asistenciaelectronica.configuracion.utils.Utilities;

public class ConfiguracionMenuRepository {
    public Pair<Boolean, Boolean> reasignarEquipo() {
        return new Pair(true, restablecer());
    }

    public boolean restablecer() {
        NUsuarioSingleton.destroyNCEmpleado();
        boolean exito = Utilities.clearDocuments();
        return exito;
    }
}
