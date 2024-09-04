package com.snyck.asistenciaelectronica.configuracion.Logg;

public class Test {

    private static Test instance;
    private boolean debug;
    private boolean openStreetTiles;
    private boolean esRedInterna;

    private Test(){
        this.debug = false;
        this.openStreetTiles = false;
        this.esRedInterna = false;
    }

    private static Test getInstance() {
        if (instance == null)
            instance = new Test();

        return instance;
    }
    public static boolean isDebug() {
        Test test = getInstance();
        return test.debug;
    }

    public static void setDebug(boolean debug) {
        Test test = getInstance();
        test.debug = debug;
    }

    public static  boolean isOpenStreetTiles() {
        Test test = getInstance();

        return test.openStreetTiles;
    }

    public static void setOpenStreetTiles(boolean openStreetTiles) {
        Test test =  getInstance();
        test.openStreetTiles = openStreetTiles;
    }

    public static void setRedInterna(boolean redInterna) {
        Test test = getInstance();
        test.esRedInterna = redInterna;
    }

    public static boolean isRedInterna() {
        Test test = getInstance();

        return test.esRedInterna;
    }
}
