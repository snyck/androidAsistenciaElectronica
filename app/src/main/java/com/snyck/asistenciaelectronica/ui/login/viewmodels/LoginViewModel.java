package com.snyck.asistenciaelectronica.ui.login.viewmodels;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.auth0.android.jwt.JWT;
import com.snyck.asistenciaelectronica.R;
import com.snyck.asistenciaelectronica.adapters.Login;
import com.snyck.asistenciaelectronica.config.Logg.Logg;
import com.snyck.asistenciaelectronica.config.Singleton.NUsuarioSingleton;
import com.snyck.asistenciaelectronica.config.base.BaseViewModel;
import com.snyck.asistenciaelectronica.config.preferences.TokenSesionPreferences;
import com.snyck.asistenciaelectronica.config.utils.Utilities;
import com.snyck.asistenciaelectronica.ui.login.models.LoginModels;
import com.snyck.asistenciaelectronica.ui.login.repository.LoginRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginViewModel extends BaseViewModel {

    private static final String TAG = LoginViewModel.class.getSimpleName();
    private static final int longitudPassword = 4;
    private final LoginRepository repository;
    Login login = new Login();
    JSONObject jsonObject;
    private final MutableLiveData<Pair<Integer, LoginModels.LoginTipoDato>> detalleDatoLogin;
    private final MutableLiveData<LoginModels.LoginTipoAcceso> tipoLogin;
    private final MutableLiveData<LoginModels.LoginTipoAcceso> muestraInicio;
    private final MutableLiveData<Boolean> statusUsuario;

    public LoginViewModel(LoginRepository repository) {
        this.repository = repository;
        this.tipoLogin = new MutableLiveData<>();
        this.statusUsuario = new MutableLiveData<Boolean>();
        this.muestraInicio = new MutableLiveData<>();
        this.detalleDatoLogin = new MutableLiveData<>();
    }

    public LiveData<Boolean> getExistenciaUsuario() {
        return statusUsuario;
    }

    public LiveData<LoginModels.LoginTipoAcceso> getMuestraInicio() {
        return muestraInicio;
    }

    public LiveData<LoginModels.LoginTipoAcceso> getTipoLogin() {
        return tipoLogin;
    }

    public LiveData<Pair<Integer, LoginModels.LoginTipoDato>> getDetalleDatosLogin() {
        return detalleDatoLogin;
    }

    public void validaTipoLogin() {
        tipoLogin.setValue(repository.validaEmpleado());
    }

    public void validaExistenciaEmpleado() {
        statusUsuario.setValue(repository.userEmpty());
    }

    public void validarDatosLogin(String usuario, String contra, Context mContext) {
        switch (tipoLogin.getValue()) {
            case LoginTipoPrimerAcceso:
                if (validaDatoLogin(usuario, LoginModels.LoginTipoDato.LoginTipoDatoEmpleadoUsuario) &&
                        validaDatoLogin(contra, LoginModels.LoginTipoDato.LoginTipoDatoIngresoContrasenia))
                    this.initSession(usuario, contra, tokenAuth(mContext, LoginModels.LoginTipoDato.LoginTipoDatoToken), mContext);
                break;

            case LoginTipoSegundoAcceso:
                this.validaSession(mContext);
                break;
        }
    }

    private boolean validaDatoLogin(String campo, LoginModels.LoginTipoDato tipoDato) {
        boolean succes = true;
        Pair<Integer, LoginModels.LoginTipoDato> statusCampo = null;
        switch (tipoDato) {
            case LoginTipoDatoEmpleadoUsuario:
                if (Utilities.isNullOrEmpty(campo)) {
                    statusCampo = new Pair<>(R.string.error_numero_empleado, tipoDato);
                    succes = false;
                    break;
                }
                break;
            case LoginTipoDatoIngresoContrasenia:
                if (Utilities.isNullOrEmpty(campo)) {
                    statusCampo = new Pair<>(R.string.error_contrasena_vacia, tipoDato);
                    succes = false;
                    break;
                }
                if (campo.length() < longitudPassword) {
                    statusCampo = new Pair<>(R.string.error_longitud_contrasena, tipoDato);
                    succes = false;
                    break;
                }
                break;
        }

        if (!succes)
            detalleDatoLogin.setValue(statusCampo);

        return succes;
    }

    private void validaSession(Context mContext) {
        if (Utilities.getJWT(tokenAuth(mContext, LoginModels.LoginTipoDato.LoginTipoDatoTokenAuth))) {
            initSession(tokenAuth(mContext, LoginModels.LoginTipoDato.LoginTipoDatoUser),
                    tokenAuth(mContext, LoginModels.LoginTipoDato.LoginTipoDatoPass),
                    tokenAuth(mContext, LoginModels.LoginTipoDato.LoginTipoDatoToken), mContext);
        } else {
            if (NUsuarioSingleton.getNUsuario().cargo_ruta == 1){
                muestraInicio.setValue(tipoLogin.getValue());
            }else {
                getSuc(tokenAuth(mContext, LoginModels.LoginTipoDato.LoginTipoDatoUser),
                        tokenAuth(mContext, LoginModels.LoginTipoDato.LoginTipoDatoTokenAuth),
                        tokenAuth(mContext, LoginModels.LoginTipoDato.LoginTipoDatoToken));
            }
        }
    }

    private void initSession(String usuario, String contra, String token, Context mContext) {
        loader.setValue(true);
        compositeDisposable.add(Single.create((SingleOnSubscribe<String>) emitter ->
                        emitter.onSuccess(repository.getLogin(usuario, contra, token)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            loader.setValue(false);
                            if (!result.isEmpty()) {
                                validateTypeLogin(result, usuario, contra, mContext);
                            } else {
                                alert.setValue("No se cuenta con cobertura para realizar la operación");
                            }
                        },
                        throwable -> {
                            loader.setValue(false);
                            throwable.printStackTrace();
                        }));
    }

    private void validateTypeLogin(String result, String usuario, String contra, Context mContext) throws JSONException {
        jsonObject = new JSONObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("result");
        jsonObject = jsonArray.getJSONObject(0);
        int code_response = login.setCode_status(jsonObject.optInt("code"));
        String message = login.setMessage(jsonObject.optString("message"));

        if (code_response == 200) {
            String id = String.valueOf(login.setUserId(jsonObject.optInt("id_user")));
            int role = login.setRol(jsonObject.optInt("role"));
            String tokenAuth = login.setToken_access(jsonObject.optString("token"));
            String tokenSesion = login.setTokenSesion(jsonObject.optString("tokenSesion"));

            TokenSesionPreferences tokenSesionPreferences = new TokenSesionPreferences(mContext);

            tokenSesionPreferences.guardaUserSession(usuario);
            tokenSesionPreferences.guardaPassSession(contra);
            tokenSesionPreferences.guardaTokenAuth(tokenAuth);
            tokenSesionPreferences.guardaToken(tokenSesion);

            NUsuarioSingleton.getNUsuario().userID = id;
            NUsuarioSingleton.getNUsuario().userName = usuario;
            NUsuarioSingleton.getNUsuario().userPass = Utilities.getBase64(contra);
            NUsuarioSingleton.getNUsuario().userRole = role;

            JWT jwtNew = new JWT(tokenAuth);

            NUsuarioSingleton.getNUsuario().nameUser = jwtNew.getClaim("name").asString();
            NUsuarioSingleton.getNUsuario().n_licencia = jwtNew.getClaim("n_licencia").asString();
            NUsuarioSingleton.getNUsuario().nameRole = jwtNew.getClaim("role").asString();
            NUsuarioSingleton.getNUsuario().pathFirmaAuditor = jwtNew.getClaim("imagen_firma").asString();
            NUsuarioSingleton.getNUsuario().pathProfileAuditor = jwtNew.getClaim("imagen_perfil").asString();
            NUsuarioSingleton.getNUsuario().modificoGafete = jwtNew.getClaim("actualizado").asInt();
            NUsuarioSingleton.getNUsuario().connection = 1;

            NUsuarioSingleton.update();
            muestraInicio.setValue(tipoLogin.getValue());
        } else if (code_response == 405) {
            TokenSesionPreferences tokenSesionPreferences = new TokenSesionPreferences(mContext);
            tokenSesionPreferences.guardaToken("");
            tipoLogin.setValue(LoginModels.LoginTipoAcceso.LoginTipoPrimerAcceso);
        } else {
            alert.setValue(message);
        }
    }

    private void getSuc(String user, String tokenAuth, String tokenSesion) {
        loader.setValue(true);
        compositeDisposable.add(Single.create((SingleOnSubscribe<String>) emitter ->
                        emitter.onSuccess(repository.getLoginActive(user, tokenAuth, tokenSesion)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            loader.setValue(false);
                            try {
                                if (!result.isEmpty()) {
                                    validateTypeLoginActive(result);
                                } else {
                                    alert.setValue("No se cuenta con cobertura para realizar la operación");
                                }
                            } catch (Exception e) {
                                loader.setValue(false);
                                Logg.e(TAG, "Error LoginViewModel Exception ." + e.getMessage());
                            }
                        },
                        throwable -> {
                            loader.setValue(false);
                            Logg.e(TAG, "Error LoginViewModel.");
                            alert.setValue(throwable.getMessage());
                        }));
    }

    private void validateTypeLoginActive(String result) throws JSONException {
        jsonObject = new JSONObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("result");
        jsonObject = jsonArray.getJSONObject(0);
        int code_response = login.setCode_status(jsonObject.optInt("code"));
        String message = login.setMessage(jsonObject.optString("message"));

        if (code_response == 200) {
            muestraInicio.setValue(tipoLogin.getValue());
        } else {
            alert.setValue(message);
        }
    }

    public String getNombreAuditor() {
        return repository.getNombreAuditor();
    }

    private String tokenAuth(Context mContext, LoginModels.LoginTipoDato tipoDato) {
        String result = null;
        TokenSesionPreferences tokenSesionPreferences = new TokenSesionPreferences(mContext);
        switch (tipoDato) {
            case LoginTipoDatoToken:
                result = tokenSesionPreferences.getToken().isEmpty() ? "null" : tokenSesionPreferences.getToken();
                break;

            case LoginTipoDatoTokenAuth:
                result = tokenSesionPreferences.getTokenAuth();
                break;

            case LoginTipoDatoUser:
                result = tokenSesionPreferences.getUserSession();
                break;

            case LoginTipoDatoPass:
                result = tokenSesionPreferences.getPassSession();
                break;
        }
        return result;
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final LoginRepository repository;

        public Factory(LoginRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new LoginViewModel(repository);
        }
    }
}
