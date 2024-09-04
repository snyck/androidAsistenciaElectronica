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
import com.snyck.asistenciaelectronica.configuracion.Logg.Logg;
import com.snyck.asistenciaelectronica.configuracion.Singleton.NUsuarioSingleton;
import com.snyck.asistenciaelectronica.configuracion.base.BaseViewModel;
import com.snyck.asistenciaelectronica.configuracion.preferences.TokenSesionPreferences;
import com.snyck.asistenciaelectronica.configuracion.utils.Utilities;
import com.snyck.asistenciaelectronica.ui.login.models.LoginModels;
import com.snyck.asistenciaelectronica.ui.login.repository.LoginRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginViewModel extends BaseViewModel {

    private static final String TAG = LoginViewModel.class.getSimpleName();
    private static final int longitudPassword = 3;
    private final LoginRepository repository;
    Login login = new Login();
    JSONObject jsonObject;
    int i = 0;
    ArrayList<String> arrayListSpinner = new ArrayList<>();
    private final MutableLiveData<Pair<Integer, LoginModels.LoginTipoDato>> detalleDatoLogin;
    private final MutableLiveData<LoginModels.LoginTipoAcceso> tipoLogin;
    private final MutableLiveData<LoginModels.LoginTipoAcceso> muestraInicio;
    private final MutableLiveData<Boolean> statusUsuario;
    private MutableLiveData<ArrayList<String>> listaCostCenterSpinner;

    public LoginViewModel(LoginRepository repository) {
        this.repository = repository;
        this.tipoLogin = new MutableLiveData<>();
        this.statusUsuario = new MutableLiveData<Boolean>();
        this.muestraInicio = new MutableLiveData<>();
        this.detalleDatoLogin = new MutableLiveData<>();
        this.listaCostCenterSpinner = new MutableLiveData<>();
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
    public LiveData<ArrayList<String>> getListaCostCenterRecycler() {
        return listaCostCenterSpinner;
    }

    public void validarDatosLogin(String usuario,String costCenter, Context mContext) {
        switch (tipoLogin.getValue()) {
            case LoginTipoPrimerAcceso:
                if (validaDatoLogin(costCenter, LoginModels.LoginTipoDato.LoginTipoDatoEmpleadoUsuario))
                    this.initSession(costCenter, tokenAuth(mContext, LoginModels.LoginTipoDato.LoginTipoDatoToken), mContext);
                break;

            case LoginTipoSegundoAcceso:
                this.validaSession(usuario,mContext);
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
                if (campo.length() == 0) {
                    statusCampo = new Pair<>(R.string.error_servicio_incorrecto, tipoDato);
                    succes = false;
                    break;
                }
                break;
        }

        if (!succes)
            detalleDatoLogin.setValue(statusCampo);

        return succes;
    }

    private void validaSession(String costCenter, Context mContext) {
        if (Utilities.getJWT(tokenAuth(mContext, LoginModels.LoginTipoDato.LoginTipoDatoTokenAuth))) {
            initSession(costCenter, tokenAuth(mContext, LoginModels.LoginTipoDato.LoginTipoDatoToken), mContext);
        } else {
            getSuc(costCenter,tokenAuth(mContext, LoginModels.LoginTipoDato.LoginTipoDatoTokenAuth),
                    tokenAuth(mContext, LoginModels.LoginTipoDato.LoginTipoDatoToken));
        }
    }

    private void initSession(String usuario, String token, Context mContext) {
        loader.setValue(true);
        compositeDisposable.add(Single.create((SingleOnSubscribe<String>) emitter ->
                        emitter.onSuccess(repository.getLogin(usuario, token, mContext)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            loader.setValue(false);
                            if (!result.isEmpty()) {
                                validateTypeLogin(result, usuario, mContext);
                            } else {
                                alert.setValue("No se cuenta con cobertura para realizar la operación");
                            }
                        },
                        throwable -> {
                            loader.setValue(false);
                            throwable.printStackTrace();
                        }));
    }

    private void validateTypeLogin(String result, String usuario, Context mContext) throws JSONException {
        jsonObject = new JSONObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("result");
        jsonObject = jsonArray.getJSONObject(0);
        int code_response = login.setCode_status(jsonObject.optInt("code"));
        String message = login.setMessage(jsonObject.optString("message"));

        if (code_response == 200) {
            String tokenAuth = login.setToken_access(jsonObject.optString("token"));
            String tokenSesion = login.setTokenSesion(jsonObject.optString("tokenSession"));

            TokenSesionPreferences tokenSesionPreferences = new TokenSesionPreferences(mContext);

            tokenSesionPreferences.guardaUserSession(usuario);
            tokenSesionPreferences.guardaTokenAuth(tokenAuth);
            tokenSesionPreferences.guardaToken(tokenSesion);
            NUsuarioSingleton.getNUsuario().userID = usuario;

            //JWT jwtNew = new JWT(tokenAuth);

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
        }
        return result;
    }

    public void costCenter() {
        loader.setValue(true);
        compositeDisposable.add(Single.create((SingleOnSubscribe<String>) emitter ->
                        emitter.onSuccess(repository.getCostCenterActive()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            loader.setValue(false);
                            try {
                                if (!result.isEmpty()) {
                                    validateCostCenter(result);
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

    private void validateCostCenter(String result) throws JSONException {
        jsonObject = new JSONObject(result);
        JSONArray spinnerArray = jsonObject.getJSONArray("result");
        jsonObject = spinnerArray.getJSONObject(0);
        int code = jsonObject.optInt("code");
        if (code == 200){
            JSONArray itemSpinnerArray = jsonObject.getJSONArray("costCenter");
            itemSpinnerArray.getJSONObject(0);
            for (i = 0; i < itemSpinnerArray.length(); i++) {
                JSONObject listSpinner = itemSpinnerArray.getJSONObject(i);

                String codigo_producto = listSpinner.getString("id");
                String nombre = listSpinner.getString("nombre");

                arrayListSpinner.add(codigo_producto+ "- " +nombre);
            }
        }
        listaCostCenterSpinner.setValue(arrayListSpinner);
    }

    public boolean validaCostCenter() {
        return  repository.checkCostCenter();
    }

    public void validaServiceFinger(byte[] img,Context mContext) {
        loader.setValue(true);
        compositeDisposable.add(Single.create((SingleOnSubscribe<String>) emitter ->
                        emitter.onSuccess(repository.getFingerService(img,tokenAuth(mContext, LoginModels.LoginTipoDato.LoginTipoDatoTokenAuth))))
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
