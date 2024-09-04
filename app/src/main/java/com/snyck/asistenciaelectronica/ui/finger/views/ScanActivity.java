package com.snyck.asistenciaelectronica.ui.finger.views;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.snyck.asistenciaelectronica.R;
import com.snyck.asistenciaelectronica.configuracion.base.BaseActivity;
import com.snyck.asistenciaelectronica.databinding.ActivityScannBinding;
import com.snyck.asistenciaelectronica.ui.finger.repository.ScanRepository;
import com.snyck.asistenciaelectronica.ui.finger.viewmodel.ScanViewModel;

import asia.kanopi.uareu4500library.Fingerprint;
import asia.kanopi.uareu4500library.Status;

public class ScanActivity extends BaseActivity {
    private ActivityScannBinding binding;
    private ScanViewModel mViewModel;
    private Fingerprint fingerprint;
    private static final String TAG = ScanActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scann);
        ScanRepository repository = new ScanRepository();
        mViewModel = new ViewModelProvider(this,new ScanViewModel.Factory(repository)).get(ScanViewModel.class);
        fingerprint = new Fingerprint();
        createObserver();
        addActions();
    }

    private void createObserver() {

    }

    private void addActions(){

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                fingerprint.turnOffReader();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        fingerprint.scan(this, printHandler, updateHandler);
        super.onStart();
    }

    @Override
    protected void onStop() {
        fingerprint.turnOffReader();
        super.onStop();
    }

    Handler updateHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int status = msg.getData().getInt("status");
            binding.tvError.setText("");
            switch (status) {
                case Status.INITIALISED:
                    binding.tvStatus.setText(R.string.finger_text_configuracion_del_lector);
                    break;
                case Status.SCANNER_POWERED_ON:
                    binding.tvStatus.setText(R.string.finger_text_lector_encendido);
                    break;
                case Status.READY_TO_SCAN:
                    binding.tvStatus.setText(R.string.finger_text_listo_para_escanear_el_dedo);
                    break;
                case Status.FINGER_DETECTED:
                    binding.tvStatus.setText(R.string.finger_text_dedo_detectado);
                    break;
                case Status.RECEIVING_IMAGE:
                    binding.tvStatus.setText(R.string.finger_text_recepci_n_de_imagen);
                    break;
                case Status.FINGER_LIFTED:
                    binding.tvStatus.setText(R.string.finger_text_se_ha_levantado_el_dedo_del_lector);
                    break;
                case Status.SCANNER_POWERED_OFF:
                    binding.tvStatus.setText(R.string.finger_text_el_lector_est_desactivado);
                    break;
                case Status.SUCCESS:
                    binding.tvStatus.setText(R.string.finger_text_huella_dactilar_capturada_con_xito);
                    break;
                case Status.ERROR:
                    binding.tvStatus.setText(R.string.finger_text_error);
                    binding.tvError.setText(msg.getData().getString("errorMessage"));
                    break;
                default:
                    binding.tvStatus.setText(String.valueOf(status));
                    binding.tvError.setText(msg.getData().getString("errorMessage"));
                    break;

            }
        }
    };

    Handler printHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            byte[] image;
            String errorMessage = "empty";
            int status = msg.getData().getInt("status");
            Intent intent = new Intent();
            intent.putExtra("status", status);
            if (status == Status.SUCCESS) {
                image = msg.getData().getByteArray("img");
                intent.putExtra("img", image);
            } else {
                errorMessage = msg.getData().getString("errorMessage");
                intent.putExtra("errorMessage", errorMessage);
            }
            setResult(RESULT_OK, intent);
            finish();
        }
    };

}
