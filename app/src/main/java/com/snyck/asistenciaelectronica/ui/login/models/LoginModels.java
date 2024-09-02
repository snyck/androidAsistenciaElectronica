package com.snyck.asistenciaelectronica.ui.login.models;

public class LoginModels {

    public enum LoginTipoAcceso {
        LoginTipoPrimerAcceso,
        LoginTipoSegundoAcceso,
        LoginTipoTercerAcceso
    }
    public enum LoginTipoDato{
        LoginTipoDatoEmpleadoUsuario,
        LoginTipoDatoIngresoContrasenia,
        LoginTipoDatoUser,
        LoginTipoDatoPass,
        LoginTipoDatoTokenAuth,
        LoginTipoDatoToken
    }
}
