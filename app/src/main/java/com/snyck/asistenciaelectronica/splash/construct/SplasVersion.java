package com.snyck.asistenciaelectronica.splash.construct;

import java.io.Serializable;

public class SplasVersion implements Serializable {

    private int code_status;
    private double version_id;
    private int ambiente_app;
    private String url_app_production;
    private String url_app_desarrollo;
    private String message_app;

    public SplasVersion(int code_status, double version_id, int ambiente_app, String url_app_production, String url_app_desarrollo, String message_app) {
        this.code_status = code_status;
        this.version_id = version_id;
        this.ambiente_app = ambiente_app;
        this.url_app_production = url_app_production;
        this.url_app_desarrollo = url_app_desarrollo;
        this.message_app = message_app;
    }

    public SplasVersion() {

    }
    public int getCode_status() {
        return code_status;
    }

    public Integer setCode_status(int code_status) {
        this.code_status = code_status;
        return code_status;
    }
    public double getVersion_id() {
        return version_id;
    }

    public int setVersion_id(double version_id) {
        this.version_id = version_id;
        return 0;
    }

    public int getAmbiente_app() {
        return ambiente_app;
    }

    public int setAmbiente_app(int ambiente_app) {
        this.ambiente_app = ambiente_app;
        return ambiente_app;
    }

    public String getUrl_app_production() {
        return url_app_production;
    }

    public String setUrl_app_production(String url_app_production) {
        this.url_app_production = url_app_production;
        return url_app_production;
    }

    public String getUrl_app_desarrollo() {
        return url_app_desarrollo;
    }

    public String setUrl_app_desarrollo(String url_app_desarrollo) {
        this.url_app_desarrollo = url_app_desarrollo;
        return url_app_desarrollo;
    }

    public String getMessage_app() {
        return message_app;
    }

    public String setMessage_app(String message_app) {
        this.message_app = message_app;
        return message_app;
    }
}
