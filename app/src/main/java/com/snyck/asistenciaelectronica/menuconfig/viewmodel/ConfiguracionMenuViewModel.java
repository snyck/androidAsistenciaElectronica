package com.snyck.asistenciaelectronica.menuconfig.viewmodel;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.snyck.asistenciaelectronica.configuracion.base.BaseViewModel;
import com.snyck.asistenciaelectronica.menuconfig.repository.ConfiguracionMenuRepository;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ConfiguracionMenuViewModel extends BaseViewModel {

    private static final String TAG = ConfiguracionMenuViewModel.class.getSimpleName();
    private ConfiguracionMenuRepository repository;
    private MutableLiveData<Boolean> exitoReasignar;

    private ConfiguracionMenuViewModel(ConfiguracionMenuRepository repository) {
        this.repository = repository;
        exitoReasignar = new MutableLiveData<>();
    }

    public LiveData<Boolean> getExitoReasignar() {
        return exitoReasignar;
    }

    public void reasignarEquipo() {
        loader.setValue(true);
        compositeDisposable.add(Single.create((SingleOnSubscribe<Pair<Boolean, Boolean>>) emitter ->
                        emitter.onSuccess(repository.reasignarEquipo()))
                .delay(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    loader.setValue(false);
                    if (resp.first) {
                        exitoReasignar.setValue(resp.second);
                    } else {
                        alert.setValue("Existe un error al reasignar tu dispositivo comunícate con sistemas");
                    }
                }, throwable -> {
                    loader.setValue(false);
                    throwable.printStackTrace();
                }));
    }



    public static class Factory implements ViewModelProvider.Factory {

        private ConfiguracionMenuRepository repository;

        public Factory(ConfiguracionMenuRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ConfiguracionMenuViewModel(repository);
        }
    }
}
