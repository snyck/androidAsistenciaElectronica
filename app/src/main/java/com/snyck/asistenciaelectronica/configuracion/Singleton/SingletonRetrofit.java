package com.snyck.asistenciaelectronica.configuracion.Singleton;

import com.snyck.asistenciaelectronica.BuildConfig;
import com.snyck.asistenciaelectronica.configuracion.Services.ServiceInterface;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SingletonRetrofit {
    public static Retrofit retrofit = null;
    public static String SERVER_URL = BuildConfig.PERSONAL_API_URL;

    public static ServiceInterface getInstance(String contextURL){
        String contexturl = null;
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        if (retrofit == null){
            contexturl = contextURL;
            retrofit = new Retrofit.Builder()
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(SERVER_URL + contexturl)
                    .build();
        }else {
            contexturl = contextURL;
            retrofit = new Retrofit.Builder()
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(SERVER_URL + contexturl)
                    .build();
        }
        return retrofit.create(ServiceInterface.class);
    }

}
