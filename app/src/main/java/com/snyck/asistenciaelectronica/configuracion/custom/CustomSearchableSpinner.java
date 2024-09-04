package com.snyck.asistenciaelectronica.configuracion.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.Serializable;

@SuppressLint("ParcelCreator")
public class CustomSearchableSpinner extends SearchableSpinner implements Parcelable, Serializable {

    public static boolean isSpinnerDialogOpen = false;

    public CustomSearchableSpinner(Context context) {
        super(context);
    }

    public CustomSearchableSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSearchableSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!isSpinnerDialogOpen) {
                isSpinnerDialogOpen = true;
                return super.onTouch(v, event);
            }
            isSpinnerDialogOpen = false;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isSpinnerDialogOpen = false;
            }
        }, 500);
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}

/*
MOD
@SuppressLint("ParcelCreator")
public class CustomSearchableSpinner extends SearchableSpinner {

    public static boolean isSpinnerDialogOpen = false;

    public CustomSearchableSpinner(Context context) {
        super(context);
    }

    public CustomSearchableSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSearchableSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private static final long MIN_DELAY_MS = 500;

    private long mLastClickTime;

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            long lastClickTime = mLastClickTime;
            long now = System.currentTimeMillis();
            mLastClickTime = now;
            if (now - lastClickTime < MIN_DELAY_MS)
            {
                // Too fast: ignore
                return true;
            }
            else
            {
                // Register the click
                return super.onTouch(v, event);
            }
        }
        return true;
    }
}

////////////////////////ORIGINAL///////////////////

@SuppressLint("ParcelCreator")
public class CustomSearchableSpinner extends SearchableSpinner implements Parcelable{

     public static boolean isSpinnerDialogOpen = false;

     public CustomSearchableSpinner(Context context) {
         super(context);
     }

     public CustomSearchableSpinner(Context context, AttributeSet attrs) {
         super(context, attrs);
     }

     public CustomSearchableSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
         super(context, attrs, defStyleAttr);
     }

     @Override
     public boolean onTouch(View v, MotionEvent event) {
         if (event.getAction() == MotionEvent.ACTION_UP) {
             if (!isSpinnerDialogOpen) {
                 isSpinnerDialogOpen = true;
                 return super.onTouch(v, event);
             }
             isSpinnerDialogOpen = false;
         }
         new Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                 isSpinnerDialogOpen = false;
             }
         }, 500);
         return true;
     }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
 */