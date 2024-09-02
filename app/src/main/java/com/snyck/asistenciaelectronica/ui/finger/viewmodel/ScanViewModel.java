package com.snyck.asistenciaelectronica.ui.finger.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.snyck.asistenciaelectronica.config.base.BaseViewModel;
import com.snyck.asistenciaelectronica.ui.finger.repository.ScanRepository;

public class ScanViewModel extends BaseViewModel{
    private static final String TAG = ScanViewModel.class.getSimpleName();
    private ScanRepository repository;
    public ScanViewModel(ScanRepository repository){
        this.repository = repository;
    }
    public static class Factory implements ViewModelProvider.Factory {
        private ScanRepository repository;
        public Factory(ScanRepository repository) { this.repository = repository; }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ScanViewModel(repository);
        }
    }
}
