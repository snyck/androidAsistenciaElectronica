package com.snyck.asistenciaelectronica.ui.login.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.snyck.asistenciaelectronica.R;
import com.snyck.asistenciaelectronica.config.base.BaseActivity;
import com.snyck.asistenciaelectronica.config.utils.Utilities;
import com.snyck.asistenciaelectronica.databinding.ActivityLoginBinding;
import com.snyck.asistenciaelectronica.ui.finger.views.ScanActivity;
import com.snyck.asistenciaelectronica.ui.login.models.LoginModels;
import com.snyck.asistenciaelectronica.ui.login.repository.LoginRepository;
import com.snyck.asistenciaelectronica.ui.login.viewmodels.LoginViewModel;
import com.snyck.asistenciaelectronica.ui.main.views.MainActivity;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel mViewModel;
    private boolean emptyUsuario = true;
    private static final int SCAN_FINGER = 0;

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        LoginRepository repository = new LoginRepository();
        mViewModel = new ViewModelProvider(this, new LoginViewModel.Factory(repository)).get(LoginViewModel.class);

        createObserver();
        addActions();
    }
    private void createObserver() {
        mViewModel.getLoader().observe(this, this::showLoader);
        mViewModel.getAlert().observe(this, s -> showAlert(getString(R.string.atencion), s));
        mViewModel.getTipoLogin().observe(this, this::muestraLogin);
        mViewModel.getMuestraInicio().observe(this, this::muestraInicio);
        mViewModel.getDetalleDatosLogin().observe(this, this::showErrorDatosLogin);
        mViewModel.getExistenciaUsuario().observe(this, this::setUsuarioEmpty);
    }
    private void addActions(){
        binding.btnLoginContinuar.setOnClickListener(v -> this.validarDatosLogin());
        /*binding.imageViewLoginConfiguracion.setOnClickListener(v -> {
            ConfiguracionMenuBottomSheetDialogFragment bottomSheetConfiguration = new ConfiguracionMenuBottomSheetDialogFragment();
            bottomSheetConfiguration.setOnCallbackRestablecer(() -> {
                clearAllError();
                try {
                    SplashActivity splashActivity = new SplashActivity();
                    splashActivity.setEmpleado();
                    splashActivity.setDocuments();
                } catch (Exception ex) {
                    Logg.e(TAG, ex.getMessage());
                } finally {
                    mViewModel.validaTipoLogin();
                }
            });
            bottomSheetConfiguration.show(getSupportFragmentManager(), ConfiguracionMenuBottomSheetDialogFragment.class.getSimpleName());
        });*/
    }
    private void validarDatosLogin() {
        Intent intent = new Intent(this, ScanActivity.class);
        //fingerResult.launch(intent,SCAN_FINGER);
        startActivityForResult(intent, SCAN_FINGER);
        /*this.clearAllError();
        mViewModel.validarDatosLogin(
                binding.inLoginEmpleado.cpfIngresoUsuario.getText().toString(),
                binding.inLoginEmpleado.cpfIngresoContra.getText().toString(),getBaseContext());*/
    }
    private void muestraLogin(LoginModels.LoginTipoAcceso tipoAcceso) {
        switch (tipoAcceso) {
            case LoginTipoPrimerAcceso:
                muestraPrimerLogin();
                break;
            case LoginTipoSegundoAcceso:
                muestraSegundoLogin();
                break;
        }

        mostrarVersion(Utilities.getVersionCode() + ".0");
    }
    private void muestraPrimerLogin() {
        binding.textViewLoginSaludo.setText("¡Bienvenido! ");
        binding.inLoginEmpleado.getRoot().setVisibility(View.VISIBLE);
        binding.inLoginIngreso.getRoot().setVisibility(View.GONE);
    }
    private void muestraSegundoLogin() {
        binding.textViewLoginSaludo.setText("¡Bienvenido! ");
        binding.inLoginEmpleado.getRoot().setVisibility(View.GONE);
        binding.inLoginIngreso.getRoot().setVisibility(View.VISIBLE);
        binding.inLoginIngreso.tvNombre.setText(mViewModel.getNombreAuditor());
    }
    private void muestraInicio(LoginModels.LoginTipoAcceso tipoLogin) {
        Intent menu;
        switch (tipoLogin) {
            case LoginTipoSegundoAcceso:
            case LoginTipoPrimerAcceso:
                finish();
                menu = new Intent(this, MainActivity.class);
                lanzarActivity(menu, Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            default:
                finish();
                break;
        }
    }
    private void showErrorDatosLogin(Pair<Integer, LoginModels.LoginTipoDato> dato) {
        switch (dato.second) {
            case LoginTipoDatoEmpleadoUsuario:
                binding.inLoginEmpleado.tilLoginUsuario.setError(getString(dato.first));
                break;
            case LoginTipoDatoIngresoContrasenia:
                binding.inLoginEmpleado.tilLoginContra.setError(getString(dato.first));
                break;
            default:
                break;
        }
    }
    private void setUsuarioEmpty(Boolean aBoolean) {
        emptyUsuario = aBoolean;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.validaTipoLogin();
        mViewModel.validaExistenciaEmpleado();
    }
    private void mostrarVersion(String s) {
        binding.tvLoginVersion.setText(String.format("%s %S", "Version", s));
    }
    private void clearAllError() {
        binding.inLoginEmpleado.tilLoginUsuario.setError(null);
        binding.inLoginEmpleado.tilLoginContra.setError(null);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String mensaje = "¿Estas seguro de salir de la Aplicación?";
            showAlert(getString(R.string.message_title_message), "\n" + mensaje + "\n \n", "Aceptar", "", () -> finaliza(), null );
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void finaliza() {
        finish();
        System.exit(0);
    }
    @Override
    protected void onDestroy() {
        mViewModel.clearViewModel();
        binding.unbind();
        super.onDestroy();
    }
}
