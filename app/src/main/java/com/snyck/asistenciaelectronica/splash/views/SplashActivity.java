package com.snyck.asistenciaelectronica.splash.views;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonIOException;
import com.snyck.asistenciaelectronica.R;
import com.snyck.asistenciaelectronica.configuracion.Logg.Logg;
import com.snyck.asistenciaelectronica.configuracion.Logg.Test;
import com.snyck.asistenciaelectronica.configuracion.Singleton.NUsuarioSingleton;
import com.snyck.asistenciaelectronica.configuracion.base.BaseActivity;
import com.snyck.asistenciaelectronica.configuracion.gps.GPSSingleton;
import com.snyck.asistenciaelectronica.configuracion.utils.Utilities;
import com.snyck.asistenciaelectronica.databinding.ActivitySplashBinding;
import com.snyck.asistenciaelectronica.splash.repository.SplashRepository;
import com.snyck.asistenciaelectronica.splash.viewmiodels.SplashViewModel;
import com.snyck.asistenciaelectronica.ui.login.views.LoginActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends BaseActivity {

    private ActivitySplashBinding binding;
    private SplashViewModel mViewModel;
    public static final int CODIGO_PERMISOS = 12345;
    public static boolean activarPermisosActivity = true;
    Animation animation;
    String DOWNLOAD_URL, fileExtensions, nameFile;
    DownloadManager.Request request;

    private static final String TAG = SplashActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Test.setDebug(true);

        if (Test.isDebug()) {
            Test.setRedInterna(true);
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        SplashRepository repository = new SplashRepository();
        mViewModel = new ViewModelProvider(this, new SplashViewModel.Factory(repository)).get(SplashViewModel.class);

        createObserver();
        revisarPermisos();
        try {
            setDocuments();
        } catch (IOException e) {
            Logg.e(TAG, "Carpeta ya existe  " + e.getMessage());
        }
        setEmpleado();
        animations();

        binding.inContentSplash.lyImg.setAnimation(animation);
        binding.inContentVersion.rlVersion.setAnimation(animation);
    }

    public void setEmpleado() {
        NUsuarioSingleton.getInstance();
    }

    private void createObserver() {
        mViewModel.getLoader().observe(this, this::showLoader);
        mViewModel.getAlert().observe(this, s -> showAlert(getString(R.string.attention), s));
        mViewModel.getValidVersion().observe(this,this::validaUpdate);
    }
    private void revisarPermisos() {
        ArrayList<String> permisosRequeridos = new ArrayList<>();
        permisosRequeridos.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permisosRequeridos.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //permisosRequeridos.add(Manifest.permission.CAMERA);
        permisosRequeridos.add(Manifest.permission.INTERNET);
        permisosRequeridos.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permisosRequeridos.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        ArrayList<String> permisosFaltantesList = new ArrayList<>();

        for (String permiso : permisosRequeridos) {
            if (ActivityCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
                permisosFaltantesList.add(permiso);
            }
        }

        if (!permisosFaltantesList.isEmpty()) {
            String[] permisosFaltantesArray = new String[permisosFaltantesList.size()];
            permisosFaltantesArray = permisosFaltantesList.toArray(permisosFaltantesArray);
            if (activarPermisosActivity) {
                activarPermisosActivity = false;
                ActivityCompat.requestPermissions(this, permisosFaltantesArray, SplashActivity.CODIGO_PERMISOS);
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_PERMISOS) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                        grantResults.length > i &&
                        grantResults[i] == PackageManager.PERMISSION_GRANTED
                ) {
                    GPSSingleton.getGPSSingleton(getApplicationContext());
                    GPSSingleton.getGoogleLocation(getApplicationContext());
                }
            }
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED)
                        activarPermisosActivity = true;
                }
            } else
                activarPermisosActivity = true;
        }
    }
    public void setDocuments() throws IOException {
        String pathDocuments = "/Documents/";
        Logg.PATH = getBaseContext().getApplicationInfo().dataDir + pathDocuments;
        Logg.PATH_CACHE = getBaseContext().getCacheDir().getAbsolutePath();
        if (!Utilities.fileExists("")) {
            Utilities.createDirectory("");
        }
    }

    private void validaUpdate(List<Object> data){
        try {
            int codeStatus = data.get(0).hashCode();
            String message = data.get(1).toString();
            if (codeStatus == 200){
                int environment = data.get(2).hashCode();
                int version_id = data.get(3).hashCode();
                String url_app_production = data.get(4).toString();
                String url_app_desarrollo = data.get(5).toString();
                int versionCode = data.get(6).hashCode();
                lanzarActivity(new Intent(getBaseContext(), LoginActivity.class),Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }else {
                showAlert("Mensaje","No se puede obtener la Version del la Aplicación");
            }

        } catch (JsonIOException exception) {
            showAlert(getString(R.string.message_title_attention), getString(R.string.message_error_null));
            Logg.e(TAG, "JsonIOException  - " + exception);
        }
    }
    private void animations() {
        animation = AnimationUtils.loadAnimation(getBaseContext(),R.anim.splash_transition);
        animation.setDuration(5000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                //Intent intent = new Intent(android.provider.Settings.ACTION_VPN_SETTINGS);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //startActivity(intent);
                lanzarActivity(new Intent(getBaseContext(), LoginActivity.class),Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
    private void downloadUpdate(int environment, String url_app_production, String url_app_desarrollo) {
        if (environment == 0) {
            DOWNLOAD_URL = url_app_desarrollo;
        } else {
            DOWNLOAD_URL = url_app_production;
        }
        DownloadManager downloadManager = (DownloadManager) SplashActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
        request = new DownloadManager.Request(Uri.parse(DOWNLOAD_URL));
        fileExtensions = MimeTypeMap.getFileExtensionFromUrl(DOWNLOAD_URL);
        nameFile = URLUtil.guessFileName(DOWNLOAD_URL, null, fileExtensions);
        request.setDestinationInExternalPublicDir("/apk", nameFile);
        String h = request.setDestinationInExternalPublicDir("/apk", nameFile).toString();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        long enqueue = downloadManager.enqueue(request);

        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                try {
                    String action = intent.getAction();
                    if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                        File apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), nameFile);
                        Intent intentApk = new Intent(Intent.ACTION_VIEW);
                        Uri apkUri;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            apkUri = Uri.parse("content://" + apkFile.getAbsolutePath());
                        } else {
                            apkUri = Uri.fromFile(apkFile);
                        }
                        intentApk.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        intentApk.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try {
                            context.startActivity(intentApk);
                            finish();
                            System.exit(0);
                        } catch (Exception e) {
                            Toast.makeText(context, "Error al instalar el APK", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "HAY UN ERROR" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        SplashActivity.this.registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    @Override
    protected void onDestroy() {
        mViewModel.clearViewModel();
        binding.unbind();
        super.onDestroy();
    }
}
