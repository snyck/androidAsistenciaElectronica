package com.snyck.asistenciaelectronica.adapters;

public class Login {

    //TODO PARA MVVM
    private String token_access;
    private String message;
    private int code_status;
    private int rol;
    private int userId;
    private int report;
    private String fullName;
    private String tokenSesion;

    private int panaderiaTotal;
    private int rosticeriaTotal;
    private int panaderiaAudiTotal;
    private int rosticeriaAudiTotal;
    private int userAuditoriasTotal;

    public int getPanaderiaTotal() {
        return panaderiaTotal;
    }

    public int setPanaderiaTotal(int panaderiaTotal) {
        this.panaderiaTotal = panaderiaTotal;
        return panaderiaTotal;
    }

    public int getRosticeriaTotal() {
        return rosticeriaTotal;
    }

    public int setRosticeriaTotal(int rosticeriaTotal) {
        this.rosticeriaTotal = rosticeriaTotal;
        return rosticeriaTotal;
    }

    public int getPanaderiaAudiTotal() {
        return panaderiaAudiTotal;
    }

    public int setPanaderiaAudiTotal(int panaderiaAudiTotal) {
        this.panaderiaAudiTotal = panaderiaAudiTotal;
        return panaderiaAudiTotal;
    }

    public int getRosticeriaAudiTotal() {
        return rosticeriaAudiTotal;
    }

    public int setRosticeriaAudiTotal(int rosticeriaAudiTotal) {
        this.rosticeriaAudiTotal = rosticeriaAudiTotal;
        return rosticeriaAudiTotal;
    }

    public int getUserAuditoriasTotal() {
        return userAuditoriasTotal;
    }

    public int setUserAuditoriasTotal(int userAuditoriasTotal) {
        this.userAuditoriasTotal = userAuditoriasTotal;
        return userAuditoriasTotal;
    }

    public int getSucursalModelo() {
        return sucursalModelo;
    }

    public int setSucursalModelo(int sucursalModelo) {
        this.sucursalModelo = sucursalModelo;
        return sucursalModelo;
    }

    private int sucursalModelo;

    public String getTokenSesion() {
        return tokenSesion;
    }

    public String setTokenSesion(String tokenSesion) {
        this.tokenSesion = tokenSesion;
        return tokenSesion;
    }

    public int setUserId(int userId) {
        this.userId = userId;
        return userId;
    }
    public String getUserName() {
        return userName;
    }
    public String setUserName(String userName) {
        this.userName = userName;
        return userName;
    }
    private String userName;

    public String getToken_access() {
        return token_access;
    }

    public String setToken_access(String token_access) {
        this.token_access = token_access;
        return token_access;
    }

    public String getMessage() {
        return message;
    }

    public String setMessage(String message) {
        this.message = message;
        return message;
    }

    public int getCode_status() {
        return code_status;
    }

    public Integer setCode_status(int code_status) {
        this.code_status = code_status;
        return code_status;
    }

    public int getRol() {
        return rol;
    }

    public Integer setRol(int rol) {
        this.rol = rol;
        return rol;
    }

    public String getFullName() {
        return fullName;
    }

    public String setFullName(String fullName) {
        this.fullName = fullName;
        return fullName;
    }

    public int getUserId() {
        return userId;
    }

    public int getReport() {
        return report;
    }

    public int setReport(int report) {
        this.report = report;
        return report;
    }
}