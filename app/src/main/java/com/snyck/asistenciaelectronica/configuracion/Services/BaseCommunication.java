package com.snyck.asistenciaelectronica.configuracion.Services;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.snyck.asistenciaelectronica.configuracion.Logg.Logg;
import com.snyck.asistenciaelectronica.configuracion.Singleton.SingletonRetrofit;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class BaseCommunication {

    public JsonObject json;
    public int responseCode;
    public ServiceInterface requestManager;
    public boolean checkConnection;
    public String lastError;
    private static final String TAG = BaseCommunication.class.getSimpleName();

    public BaseCommunication() {
        json = new JsonObject();
        responseCode = 0;
        checkConnection = true;
    }

    /**
     * Realiza una petición POST
     *
     * @param method        method para el servicio
     * @param params        Parameters para el servicio
     * @return true Si el servicio se consume con éxito. false Para cualquier otro caso.
     */
    protected boolean responsePostParam(String contextUrl, String method, JsonObject params) throws JSONException {
        if (!checkConnection()) {
            errorConexion("No se cuenta con cobertura para realizar la operación");
            return false;
        }
        requestManager = SingletonRetrofit.getInstance(contextUrl);
        Call<ResponseBody> call = requestManager.responsePOST(method, params);
        try {
            String jsonString;
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful() || response.code() == 200 ) {
                jsonString = response.body().string();
            }else {
                jsonString = response.errorBody().string();
            }
            json = JsonParser.parseString(jsonString).getAsJsonObject();
            return true;
        } catch (IOException e) {
            errorConexion("No se cuenta con cobertura para realizar la operación");
            Logg.e(TAG, "Error IOException - responsePost - " + e.getMessage());
            return false;
        } catch (Exception e) {
            errorConexion("Error al consultar el servicio");
            Logg.e(TAG, "Error IOException - responsePost - " + e.getMessage());
            return false;
        }
    }
    /**
     * Realiza una petición POST
     *
     * @param contextUrl    method Controller
     * @param method        method para el servicio
     * @param params        Parameters para el servicio
     * @param authorization Parameters para autorizar
     * @return true Si el servicio se consume con éxito. false Para cualquier otro caso.
     */
    protected boolean responsePostParamAuth(String contextUrl, String method, JsonObject params, String authorization) throws JSONException {
        if (!checkConnection()) {
            errorConexion("Error de conexión, No se cuenta con cobertura para realizar la operación");
            return false;
        }
        requestManager = SingletonRetrofit.getInstance(contextUrl);
        Call<ResponseBody> call = requestManager.postResponseParamsPathAuthorization(authorization,method,params);
        try {
            Response<ResponseBody> bodyResponse = call.execute();
            if (bodyResponse.isSuccessful() || bodyResponse.code() == 403) {
                String jsonString = bodyResponse.body().string();
                json = JsonParser.parseString(jsonString).getAsJsonObject();
                return true;
            } else return false;
        } catch (IOException e) {
            errorConexion("No se cuenta con cobertura para realizar la operación");
            Logg.e(TAG, "Error IOException - responsePost - " + e.getMessage());
            return false;
        } catch (Exception e) {
            errorConexion("Error al consultar el servicio");
            Logg.e(TAG, "Error IOException - responsePost - " + e.getMessage());
            return false;
        }
    }

    protected boolean responsePostAuth(String contextUrl, String method, String authorization) {
        if (!checkConnection()) {
            errorConexion("No se cuenta con cobertura para realizar la operación");
            return false;
        }
        requestManager = SingletonRetrofit.getInstance(contextUrl);
        Call<ResponseBody> call = requestManager.postResponsePathAuthorization(authorization, method);
        try {
            Response<ResponseBody> bodyResponse = call.execute();
            if (bodyResponse.isSuccessful()) {
                String jsonString = bodyResponse.body().string();
                json = JsonParser.parseString(jsonString).getAsJsonObject();
                return true;
            } else  return false;
        } catch (IOException e) {
            errorConexion("No se cuenta con cobertura para realizar la operación");
            Logg.e(TAG, "Error IOException - responsePost - " + e.getMessage());
            return false;
        } catch (Exception e) {
            errorConexion("Error al consultar el servicio");
            Logg.e(TAG, "Error IOException - responsePost - " + e.getMessage());
            return false;
        }
    }

    public String responseGet(String contextUrl, String method, Map<String, String> params) {
        requestManager = SingletonRetrofit.getInstance(contextUrl);
        Call<ResponseBody> call = requestManager.getResponse(method, params);
        try {
            String jsonString;
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful() || response.code() == 200 ) {
                jsonString = response.body().string();
            }else {
                jsonString = response.errorBody().string();
            }
            json = JsonParser.parseString(jsonString).getAsJsonObject();
        } catch (IOException e) {
            Logg.e(TAG, "Error IOException - responseGet - " + e.getMessage());
        }
        return json.toString();
    }

    public String responseGetPathAuth(String contextUrl, Map<String, String> headers, String method, Map<String, String> params, boolean decode, String authorization) {
        if (!checkConnection()) {
            errorConexion("No se cuenta con cobertura para realizar la operación");
            return "";
        }
        if (headers == null)
            headers = new HashMap<>();

        requestManager = SingletonRetrofit.getInstance(contextUrl);
        Call<ResponseBody> call =  requestManager.getResponseParamsPathAuthorization(authorization,headers,method,params);
        try {
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
                String jsonString = response.body().string();
                json = JsonParser.parseString(jsonString).getAsJsonObject();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error IOException - responseGet - " + e.getMessage());
        }
        return json.toString();
    }

    public synchronized boolean checkConnection() {
        boolean result = false;
        if (!checkConnection) {
            return true;
        }

        HttpURLConnection con = null;
        try {
            URL urlConnection = new URL("http://192.168.193.52");
            con = (HttpURLConnection) urlConnection.openConnection();
            int responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                result = true;
                checkConnection = false;
            }
        } catch (Exception e) {
            Logg.e(TAG, e);
            checkConnection = true;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return result;
    }

    protected void errorConexion(String error) {
        lastError = error;
        Log.e(TAG, error);
    }

    protected void errorParseo(String error) {
        lastError = error;
        Log.e(TAG, error);
    }

    private boolean verificarToken(String authorization) {
        String token = authorization.replace("Bearer", "").replace("Basic", "").trim();
        return token.length() > 0;
    }
}