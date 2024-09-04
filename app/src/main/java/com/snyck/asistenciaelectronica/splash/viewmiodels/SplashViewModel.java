package com.snyck.asistenciaelectronica.splash.viewmiodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.snyck.asistenciaelectronica.configuracion.base.BaseViewModel;
import com.snyck.asistenciaelectronica.configuracion.utils.Utilities;
import com.snyck.asistenciaelectronica.splash.construct.SplasVersion;
import com.snyck.asistenciaelectronica.splash.repository.SplashRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.BuildConfig;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SplashViewModel extends BaseViewModel {
    private final SplashRepository repository;
    private final MutableLiveData<List<Object>> actualizaApp;
    private double versionCode;
    private String versionName;
    SplasVersion splasVersion = new SplasVersion();
    JSONObject jsonObject;

    private static final String TAG = SplashViewModel.class.getSimpleName();
    public SplashViewModel(SplashRepository repository){
        this.repository = repository;
        this.actualizaApp = new MutableLiveData<>();
    }

    public LiveData<List<Object>> getValidVersion() {
        return actualizaApp;
    }

    public void getVersionApp(){
        getAppVersion();
        loader.setValue(true);
        compositeDisposable.add(Single.create((SingleOnSubscribe<String>) emitter ->
                        emitter.onSuccess(repository.getServerVersionApp(versionCode)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(version -> {
                    try {
                        if (!version.isEmpty()) {
                            loader.setValue(false);
                            validateTypeVersion(version);
                        } else {
                            loader.setValue(false);
                            alert.setValue("No se cuenta con cobertura para realizar la operación");
                        }
                    } catch (Exception e) {
                        loader.setValue(false);
                        Log.e(TAG, "Error LoginViewModel Exception ." + e.getMessage());
                    }
                }, throwable -> {
                    loader.setValue(false);
                })
        );
    }
    private void getAppVersion() {
        versionCode = Utilities.getVersionCode();
        versionName = BuildConfig.VERSION_NAME;
    }
    private void validateTypeVersion(String result) throws JSONException {
        jsonObject = new JSONObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("result");
        jsonObject = jsonArray.getJSONObject(0);

        int code_response = splasVersion.setCode_status(jsonObject.optInt("code"));
        int environment = splasVersion.setAmbiente_app(jsonObject.optInt("environment"));
        int version_id = splasVersion.setVersion_id(jsonObject.optInt("version"));
        String url_app_production = splasVersion.setUrl_app_production(jsonObject.optString("apk"));
        String url_app_desarrollo = splasVersion.setUrl_app_desarrollo(jsonObject.optString("apk_bug"));
        String message = splasVersion.setMessage_app(jsonObject.optString("message"));

        if (code_response == 200) {
            actualizaApp.setValue(Arrays.asList(code_response,message,environment,version_id,url_app_production,url_app_desarrollo,versionCode));
        } else {
            actualizaApp.setValue(Arrays.asList(code_response,message));
        }
    }
    public static class Factory implements ViewModelProvider.Factory {
        private final SplashRepository repository;

        public Factory(SplashRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new SplashViewModel(repository);
        }
    }
}
