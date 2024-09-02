package com.snyck.asistenciaelectronica.config.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class BaseDialogFragment extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void showAlert(String titulo, String mensaje) {
        showAlert(titulo,mensaje,"Aceptar", "", () -> {}, null);
    }

    protected void showAlert(String title, String mensaje, String textoPositivo, MostrarAlertaClickInterface.positive positive) {
        showAlert(title, mensaje, textoPositivo, "", positive, null);
    }

    protected void showAlert(String titulo, String mensaje, String textoPositivo, String textoNegative, MostrarAlertaClickInterface.positive positive, MostrarAlertaClickInterface.negative negative) {
        if (getActivity() != null)
            ((BaseActivity) getActivity()).showAlert(titulo, mensaje, textoPositivo, textoNegative, positive, negative);
    }

    protected void showLoader(Boolean show) {
        if (getActivity() != null)
            ((BaseActivity) getActivity()).showLoader(show);
    }
}
