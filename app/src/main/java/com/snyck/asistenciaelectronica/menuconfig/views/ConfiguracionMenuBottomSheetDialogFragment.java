package com.snyck.asistenciaelectronica.menuconfig.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.snyck.asistenciaelectronica.R;
import com.snyck.asistenciaelectronica.configuracion.base.BaseBottomSheetDialogFragment;
import com.snyck.asistenciaelectronica.databinding.DialogfragmentConfiguracionMenuBinding;
import com.snyck.asistenciaelectronica.menuconfig.protocols.ConfiguracionMenuProtocol;
import com.snyck.asistenciaelectronica.menuconfig.repository.ConfiguracionMenuRepository;
import com.snyck.asistenciaelectronica.menuconfig.viewmodel.ConfiguracionMenuViewModel;

public class ConfiguracionMenuBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {

    private DialogfragmentConfiguracionMenuBinding binding;
    private ConfiguracionMenuViewModel mViewModel;
    private ConfiguracionMenuProtocol callbackRestablecer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConfiguracionMenuRepository repository = new ConfiguracionMenuRepository();
        mViewModel = new ViewModelProvider(this, new ConfiguracionMenuViewModel.Factory(repository)).get(ConfiguracionMenuViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialogfragment_configuracion_menu, null, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createObserver();
        addActions();
    }

    private void createObserver() {
        mViewModel.getLoader().observe(getViewLifecycleOwner(),this::showLoader);
        mViewModel.getAlert().observe(getViewLifecycleOwner(), s -> showAlert(getString(R.string.atencion), s));
        mViewModel.getExitoReasignar().observe(getViewLifecycleOwner(), this::resultadoReasignarEquipo);
    }

    private void addActions() {
        binding.trCerrarConfiguracion.setOnClickListener(view -> dismiss());
        //binding.trCambiarContrasena.setOnClickListener(view -> muestraCambiarContrasena());
        //binding.trOlvideContrasena.setOnClickListener(view -> muestraOlvideContrasena());
        binding.trReasignarEquipo.setOnClickListener(view -> muestraReasignarEquipo());
    }

    private void muestraReasignarEquipo() {
        mViewModel.reasignarEquipo();
    }
    public void setOnCallbackRestablecer(final ConfiguracionMenuProtocol callbackRestablecer) {
        this.callbackRestablecer = callbackRestablecer;
    }

    private void resultadoReasignarEquipo(Boolean exitoRestablecer) {
        if (exitoRestablecer) {
            showAlert(getString(R.string.atencion),
                    getString(R.string.equipo_se_reasigno_correctamente),
                    getString(R.string.aceptar),
                    () -> {
                        if (callbackRestablecer != null)
                            callbackRestablecer.restablecerEquipo();
                        dismiss();
                    });
        }
        else {
            showAlert(getString(R.string.atencion), getString(R.string.error_al_reasignar_equipo));
        }
    }
}
