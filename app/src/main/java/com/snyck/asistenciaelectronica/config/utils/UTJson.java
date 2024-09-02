package com.snyck.asistenciaelectronica.config.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.snyck.asistenciaelectronica.config.Logg.Logg;

import java.util.ArrayList;
import java.util.Date;

public class UTJson {
    private static final String TAG = UTJson.class.getSimpleName();

    public static boolean isJsonObject(String jsonInString) {
        try {
            JsonObject ob = new Gson().fromJson(jsonInString, JsonObject.class);
            return ob != null;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isJsonArray(String jsonInString) {
        try {
            JsonArray ob = new Gson().fromJson(jsonInString, JsonArray.class);
            return ob != null;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String isJsonStringValide(JsonObject object, String field) {
        if (object.get(field) != null && !object.get(field).isJsonNull()) {
            if (object.get(field).getAsString() != null && !object.get(field).toString().contains("null")) {
                return object.get(field).getAsString();
            }
        }

        return "";
    }

    public static int isJsonIntValide(JsonObject object, String field) {
        if (object.get(field) != null && !object.get(field).isJsonNull()) {
            return object.get(field).getAsInt();
        }
        return 0;
    }

    public static boolean isElementValide(JsonObject object, String element) {
        return object.has(element);
    }

    public static double jsonGetDoble(JsonElement jsonElement) {
        try {
            if (jsonElement != null) {
                if (!jsonElement.isJsonNull()) {
                    return jsonElement.getAsDouble();
                }
            }
        } catch (Exception ex) {
            Logg.e(TAG, "Error al parsear Doble: " + jsonElement);
        }
        return 0.0;
    }

    public static Boolean jsonGetBoolean(JsonElement jsonElement) {
        try {
            if (jsonElement != null) {
                if (!jsonElement.isJsonNull())
                    return jsonElement.getAsBoolean();
            }
        } catch (Exception ex) {
            Logg.e(TAG, "Error al obtener doble");
        }
        return false;
    }

    public static String jsonGetString(JsonElement jsonElement) {
        try {
            if (jsonElement != null) {
                if (!jsonElement.isJsonNull()) {
                    return jsonElement.getAsString().trim();
                }
            }
        } catch (Exception ex) {
            Logg.e(TAG, "Error al parsear String: " + jsonElement);
        }
        return "";
    }

    public static int jsonGetInt(JsonElement jsonElement) {
        try {
            if (jsonElement != null) {
                if (!jsonElement.isJsonNull()) {
                    return jsonElement.getAsInt();
                }
            }
        } catch (Exception ex) {
            Logg.e(TAG, "Error al parsear Int: " + jsonElement);
        }
        return 0;
    }

    public static JsonObject jsonGetJsonObject(JsonElement jsonElement) {
        try {
            if (jsonElement != null) {
                if (!jsonElement.isJsonNull() && jsonElement.isJsonObject()) {
                    return jsonElement.getAsJsonObject();
                }
            }
        } catch (Exception ex) {
            Logg.e(TAG, "Error al obtener JsonObject");
        }
        return new JsonObject();
    }


    public static String isJsonIntValideToString(JsonObject object, String field) {
        if (object.get(field) != null && !object.get(field).isJsonNull() && object.get(field).getAsString() != null && !object.get(field).toString().contains("null")) {
            return object.get(field).getAsString();
        }
        return "0";
    }

    public static int isJsonIntValideDefault(JsonObject object, String field, int def) {
        if (object.get(field) != null && !object.get(field).isJsonNull()) {
            try {
                return object.get(field).getAsInt();
            } catch (Exception e) {
                Logg.e(TAG, "Error al parsear");
                return def;
            }
        }
        return def;
    }

    public static JsonObject convertirAGsonObject(Object object) {
        Gson gson = new GsonBuilder().create();
        return gson.toJsonTree(object).getAsJsonObject();
    }

    public static Object convertirGsonAObject(Class clase, JsonObject jsonObject) {
        return new Gson().fromJson(jsonObject, clase);
    }

    public static JsonArray convertirArrayAGsonArray(ArrayList array) {
        Gson gson = new GsonBuilder().create();
        return gson.toJsonTree(array).getAsJsonArray();
    }

    public static double isJsonDoubleValideDefault(JsonObject object, String field, double def) {
        if (object.get(field) != null && !object.get(field).isJsonNull()) {
            try {
                return object.get(field).getAsDouble();
            } catch (Exception e) {
                Logg.e(TAG, "Error al parsear");
                return def;
            }
        }
        return def;
    }

    public static String isJsonStringValideDefault(JsonObject object, String field, String def) {
        if (object == null) {
            return def;
        }
        if (object.get(field) != null && !object.get(field).isJsonNull()) {
            if (object.get(field).getAsString() != null && !object.get(field).toString().contains("null")) {
                return object.get(field).getAsString();
            }
            return def;
        }
        return def;
    }

    public static boolean isJsonBooleanValideDefault(JsonObject object, String field, boolean def) {
        if (object.get(field) != null && !object.get(field).isJsonNull()) {
            return object.get(field).getAsBoolean();
        }
        return def;
    }

    public static JsonArray jsonGetJsonArray(JsonElement jsonElement) {
        try {
            if (jsonElement != null) {
                if (!jsonElement.isJsonNull() && jsonElement.isJsonArray()) {
                    return jsonElement.getAsJsonArray();
                }
            }
        } catch (Exception ex) {
            Logg.e(TAG, "Error al obtener JsonArray");
        }
        return new JsonArray();
    }

    public static Date isJsonDateValideDefault(JsonObject object, String field, Date def) {
        if (object.get(field) != null && !object.get(field).isJsonNull()) {
            if (object.get(field).getAsString() != null && !object.get(field).toString().contains("null")) {
                return Utilities.dateFromString(object.get(field).getAsString(), Utilities.UTDateSmallFormatNOC);
            }
            return def;
        }
        return def;
    }

    /**
     * Obtiene las llaves de un JsonObject y devuelve un ArrayList de las mismas
     */
    public static ArrayList<String> allKeys(JsonObject jsonObject) {
        ArrayList<String> keys = null;
        try {
            keys = new ArrayList<>(jsonObject.keySet());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        return keys;
    }

    /**
     * Obtiene las llaves de un objeto de un JsonObject y devuelve un ArrayList de las mismas
     */
    public static ArrayList<String> allKeysForObject(JsonObject jsonObject, String object) {
        ArrayList<String> keys = allKeys(jsonObject);
        ArrayList<String> keysForObject = new ArrayList<>();
        for (String key : keys) {
            if (jsonGetString(jsonObject.get(key)).equals(object)) {
                keysForObject.add(key);
            }

        }
        return keysForObject;
    }

    /**
     * Retorna un Arraylist de los elementos de a-b
     */
    public static ArrayList<String> minusSet(ArrayList<String> a, ArrayList<String> b) {
        ArrayList<String> minusSetArray = new ArrayList<>(a);
        for (String s : b) {
            minusSetArray.remove(s);
        }
        return minusSetArray;
    }

    /**
     * Obtiene un JsonObject de un String
     */
    public static JsonObject stringToJsonObject(String s) {
        JsonObject jsonObject = null;
        try {
            jsonObject = new Gson().fromJson(s, JsonObject.class);

        } catch (Exception e) {
            Logg.e(TAG, e);
        }
        return jsonObject;
    }

    /**
     * Obtiene un String a partir de JsonObject
     */
    public static String jsonObjectToString(JsonObject json) {
        String stringJson = null;
        try {
            stringJson = new Gson().toJson(json);
        } catch (Exception e) {
            Logg.e(TAG, e);
        }
        return stringJson;
    }

    /**
     * Corrije valores double de un JsonObject
     */
    public static JsonObject fixDoubleJson(JsonObject object) {
        ArrayList<String> keys = new ArrayList<>(object.keySet());
        for (String key : keys) {
            String valueFix = object.get(key).getAsString();

            if (key.startsWith("fi") && valueFix.equals(" ")){
                valueFix = "0";
            }else if (valueFix.equals(" ")){
                valueFix = "";
            }

            if (!valueFix.isEmpty()) {
                valueFix = valueFix.contains("-") ? valueFix.replaceFirst("^0*", "") : valueFix;
                valueFix = valueFix.isEmpty() ? "0" : valueFix;
            }
            object.addProperty(key, valueFix);
        }
        return object;
    }

    /**
     * Valida que un JsonObject contenga las llaves que recibe el metodo
     *
     * @param keys llaves de validacion en el JsonObject
     */
    public static boolean containsKeys(JsonObject jsonObject, String... keys) {
        try {
            boolean success = true;
            for (String key : keys) {
                if (!jsonObject.has(key)) {
                    success = false;
                    break;
                }
            }
            return success;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Obtener sub-JsonObject de un JsonObject
     *
     */
    public static JsonObject getValidJsonObject(JsonObject json, String subObject) {
        JsonObject jsonObject = null;
        try {
            jsonObject = isElementValide(json, subObject) ? (json.get(subObject).isJsonNull() ? null : json.getAsJsonObject(subObject)) : null;
        } catch (Exception e) {
            Logg.e(TAG, "No se pudo obtener subObjeto: " + e.getMessage());
        }
        return jsonObject;
    }
}


