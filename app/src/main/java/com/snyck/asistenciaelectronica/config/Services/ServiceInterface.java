package com.snyck.asistenciaelectronica.config.Services;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface ServiceInterface {

    @POST("{method}")
    Call<ResponseBody> responsePOST(
            @Path("method") String method,
            @Body JsonObject body);

    @Headers({"Content-type:application/json"})
    @GET("{method}")
    Call<ResponseBody> responseGET(
            @Path("method") String method,
            @Body JsonObject body);

    @GET("{method}/?")
    Call<ResponseBody> getResponse(
            @Path("method") String method,
            @QueryMap Map<String, String> params);

    @Headers({"Content-type:application/json"})
    @POST("{method}")
    Call<ResponseBody> postResponsePathAuthorization(
            @Header("Authorization") String auth,
            @Path("method") String method
    );

    @Headers({"Content-type:application/json"})
    @POST("{method}")
    Call<ResponseBody> postResponseParamsPathAuthorization(
            @Header("Authorization") String auth,
            @Path("method") String method,
            @Body JsonObject body
    );
    @GET
    Call<ResponseBody> getResponseParamsPath(@Url String url);

    @Headers({"Content-type:application/json"})
    @GET("{method}")
    Call<ResponseBody> getResponseParamsPathAuthorization(
            @Header("Authorization") String auth,
            @HeaderMap Map<String, String> headers,
            @Path("method") String method,
            @QueryMap Map<String, String> params
    );
    @Headers({"Content-type:application/json"})
    @GET("{method}?")
    Call<ResponseBody> responseGETAuthorization(
            @Header("Authorization") String auth,
            @HeaderMap Map<String, String> headers,
            @Path("method") String method,
            @QueryMap Map<String, String> params
    );
}
