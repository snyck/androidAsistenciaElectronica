package com.snyck.asistenciaelectronica.ui.login.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.snyck.asistenciaelectronica.R;
import com.snyck.asistenciaelectronica.configuracion.Logg.Logg;
import com.snyck.asistenciaelectronica.configuracion.base.BaseActivity;
import com.snyck.asistenciaelectronica.configuracion.custom.CustomSearchableSpinner;
import com.snyck.asistenciaelectronica.configuracion.utils.Utilities;
import com.snyck.asistenciaelectronica.databinding.ActivityLoginBinding;
import com.snyck.asistenciaelectronica.menuconfig.views.ConfiguracionMenuBottomSheetDialogFragment;
import com.snyck.asistenciaelectronica.splash.views.SplashActivity;
import com.snyck.asistenciaelectronica.ui.finger.views.ScanActivity;
import com.snyck.asistenciaelectronica.ui.login.models.LoginModels;
import com.snyck.asistenciaelectronica.ui.login.repository.LoginRepository;
import com.snyck.asistenciaelectronica.ui.login.viewmodels.LoginViewModel;
import com.snyck.asistenciaelectronica.ui.main.views.MainActivity;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;

import asia.kanopi.uareu4500library.Status;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel mViewModel;
    private boolean emptyUsuario = true;
    private static final int SCAN_FINGER = 0;
    private int status;
    private String errorMesssage, id_cost_center = "0";
    private byte[] img;
    private SearchableSpinner spinner_cost_center;

    private static final String TAG = LoginActivity.class.getSimpleName();
    ActivityResultLauncher<Intent> firmaActivityResul = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null){
                        result.getData().putExtra("status", Status.ERROR);
                        if (status == Status.SUCCESS) {
                            img = result.getData().getByteArrayExtra("img");
                            mViewModel.validaServiceFinger(img,this);
                        } else {
                            errorMesssage = result.getData().getStringExtra("errorMessage");
                        }
                    }

                } else {

                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        LoginRepository repository = new LoginRepository();
        mViewModel = new ViewModelProvider(this, new LoginViewModel.Factory(repository)).get(LoginViewModel.class);

        initVista();
        createObserver();
        addActions();
        mViewModel.costCenter();
    }
    private void initVista() {
        spinner_cost_center = binding.inLoginEmpleado.includeItemSpinner.spinnerSelectItem;
        binding.btnLoginContinuar.setText(R.string.entrar);
    }
    private void createObserver() {
        mViewModel.getLoader().observe(this, this::showLoader);
        mViewModel.getAlert().observe(this, s -> showAlert(getString(R.string.atencion), s));
        mViewModel.getTipoLogin().observe(this, this::muestraLogin);
        mViewModel.getMuestraInicio().observe(this, this::muestraInicio);
        mViewModel.getDetalleDatosLogin().observe(this, this::showErrorDatosLogin);
        mViewModel.getExistenciaUsuario().observe(this, this::setUsuarioEmpty);
        mViewModel.getListaCostCenterRecycler().observe(this, this::listCostCenter);
    }

    private void addActions(){
        binding.btnLoginContinuar.setOnClickListener(v -> this.validarDatosLogin());
        binding.imageViewLoginConfiguracion.setOnClickListener(v -> {
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
        });
        spinner_cost_center.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clearAllError();
                //LimpiaVista();
                CustomSearchableSpinner.isSpinnerDialogOpen = false;
                spinner_cost_center.setTitle(getString(R.string.title_spinner_cost_center_produccion));
                spinner_cost_center.setPositiveButton(getString(R.string.title_buton_spinner_cerrar));
                String producto_seleccionado_usuario = spinner_cost_center.getItemAtPosition(spinner_cost_center.getSelectedItemPosition()).toString();
                id_cost_center = producto_seleccionado_usuario.substring(0, producto_seleccionado_usuario.indexOf("-"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                CustomSearchableSpinner.isSpinnerDialogOpen = false;
            }
        });
    }
    private void validarDatosLogin() {
        this.clearAllError();
        if (mViewModel.validaCostCenter()){
            Intent intent = new Intent(this, ScanActivity.class);
            firmaActivityResul.launch(intent);
        }else {
            mViewModel.validarDatosLogin(binding.inLoginIngreso.cpfIngresoUsuario.getText().toString(), id_cost_center,this);
        }

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
        binding.textViewLoginSaludo.setText("¡Asignación! ");
        binding.inLoginEmpleado.getRoot().setVisibility(View.VISIBLE);
    }
    private void muestraSegundoLogin() {
        binding.textViewLoginSaludo.setText("¡Bienvenido! ");
        binding.inLoginIngreso.getRoot().setVisibility(View.VISIBLE);
        binding.inLoginEmpleado.getRoot().setVisibility(View.GONE);
        binding.btnLoginContinuar.setText(R.string.huella);
    }
    private void muestraInicio(LoginModels.LoginTipoAcceso tipoLogin) {
        Intent menu;
        switch (tipoLogin) {
            case LoginTipoSegundoAcceso:
            case LoginTipoPrimerAcceso:
                finish();
                lanzarActivity(getIntent(),Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            default:
                finish();
                break;
        }
    }
    private void showErrorDatosLogin(Pair<Integer, LoginModels.LoginTipoDato> dato) {
        switch (dato.second) {
            case LoginTipoDatoEmpleadoUsuario:
                binding.inLoginIngreso.tilLoginUsuario.setError(getString(dato.first));
                break;
            /*case LoginTipoDatoIngresoContrasenia:
                binding.inLoginEmpleado.tilLoginContra.setError(getString(dato.first));
                break;*/
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
        binding.inLoginIngreso.tilLoginUsuario.setError(null);
        /*binding.inLoginEmpleado.tilLoginContra.setError(null);*/
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

    private void listCostCenter(ArrayList<String> spinnerArrayList) {
        if (spinnerArrayList.isEmpty()) {
            showAlert("Resultado", "No hay Productos de Producción para esta sucursal");
        } else {
            spinner_cost_center.setAdapter(new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_dropdown_item, spinnerArrayList));
        }
    }
}
