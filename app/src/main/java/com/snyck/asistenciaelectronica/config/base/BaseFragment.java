package com.snyck.asistenciaelectronica.config.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public boolean onBackPressed() {
        return false;
    }

    protected void showAlert(String titulo, String mensaje) {
        showAlert(titulo, mensaje, "Aceptar", "", () -> {}, null);
    }

    protected void showAlert(String title, String mensaje, String textoPositivo, MostrarAlertaClickInterface.positive positive) {
        showAlert(title, mensaje, textoPositivo, "", positive, null);
    }

    protected void showAlert(String titulo, String mensaje, String textoPositivo, String textoNegative, MostrarAlertaClickInterface.positive positive, MostrarAlertaClickInterface.negative negative) {
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).showAlert(titulo, mensaje, textoPositivo, textoNegative, positive, negative);
        }
    }

    protected void showLoader(Boolean show) {
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).showLoader(show);
        }
    }

    protected void showLoader(Boolean show, String message) {
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).showLoader(show, message);
        }
    }

    protected void showDialogFragment(BaseDialogFragment fragment, boolean addToBackStack) {
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).showDialogFragment(fragment, addToBackStack);
        }
    }

    protected <T extends Fragment> void mostrarFragmentFade(Class<T> fragmentClass, int containerViewId, boolean addToBackStack) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentClass.getSimpleName());
        if (fragment == null) {
            try {
                fragment = fragmentClass.newInstance();
                fragment.setArguments(new Bundle());
            } catch (Exception e) {
                throw new RuntimeException("New Fragment should have been created", e);
            }
        }
        fragmentTransaction.replace(containerViewId, fragment, fragmentClass.getSimpleName());
        if (addToBackStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public <T extends Fragment> void mostrarFragment(Class<T> fragmentClass, int containerViewId, Bundle bundle, boolean addToBackStack) {
        mostrarFragment(fragmentClass, containerViewId, bundle, addToBackStack, false);
    }

    public <T extends Fragment> void mostrarFragment(Class<T> fragmentClass, int containerViewId, Bundle bundle, boolean addToBackStack, boolean clearStack) {
        mostrarFragment(fragmentClass, containerViewId, bundle, addToBackStack, clearStack, false);
    }

    public <T extends Fragment> void mostrarFragment(Class<T> fragmentClass, int containerViewId, Bundle bundle, boolean addToBackStack, boolean clearStack, boolean commitAllowingStateLoss) {
        FragmentManager fragmentManager = getChildFragmentManager();
        if (clearStack) {
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                fragmentManager.popBackStack();
            }
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentClass.getSimpleName());
        if (fragment == null) {
            try {
                fragment = fragmentClass.newInstance();
                fragment.setArguments(bundle);
            } catch (Exception e) {
                throw new RuntimeException("New Fragment should have been created", e);
            }
        }
        fragmentTransaction.replace(containerViewId, fragment, fragmentClass.getSimpleName());
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragmentClass.getName());
        }
        if (commitAllowingStateLoss) {
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            fragmentTransaction.commit();
        }
    }
}
