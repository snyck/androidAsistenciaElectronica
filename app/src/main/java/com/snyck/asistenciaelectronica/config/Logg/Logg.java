package com.snyck.asistenciaelectronica.config.Logg;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Logg {

    public final static int VERBOSE = 0;
    public final static int DEBUG = 1;
    public final static int INFO = 2;
    public final static int WARN = 3;
    public final static int ERROR = 4;
    private static final int DEFAULT_LOG_MAX_FILE_SIZE = 1024;  // 1 MB
    private static final int DEFAULT_LOG_MAX_NUM_LOG_FILES = 5; // 5 archivos

    public static String PATH;
    public static String PATH_CACHE;
    public static int debugLevel = DEBUG;
    private static String mLogsPath;

    private static int line;
    private static String stacktrace;

    private static void guardarEnArchivo(String TAG, int debugLevel, String mensaje) {
        try{
            if (Logg.debugLevel <= debugLevel) {
                Date currentTime = Calendar.getInstance().getTime();
                String fecha = new SimpleDateFormat("yyyy/MM/dd").format(currentTime);
                String hora = new SimpleDateFormat("HH:mm:ss:SSS").format(currentTime);
                String separador = " [~] ";

                String method = Thread.currentThread().getStackTrace()[4].getMethodName();
                line = Thread.currentThread().getStackTrace()[4].getLineNumber();

                String debugTag = "";
                switch (debugLevel) {
                    case VERBOSE:
                        debugTag = "V";
                        break;
                    case DEBUG:
                        debugTag = "D";
                        break;
                    case INFO:
                        debugTag = "I";
                        break;
                    case WARN:
                        debugTag = "W";
                        break;
                    case ERROR:
                        debugTag = "E";
                        break;
                }
                StringBuilder logBuilder = new StringBuilder()
                        .append(fecha).append(" ").append(hora).append(separador)
                        .append(debugTag).append(separador)
                        .append(TAG).append(separador)
                        .append(method).append(separador)
                        .append(line).append(separador)
                        .append(mensaje).append("[~~]")
                        .append("\n");

                saveFile(logBuilder);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    //Escribe la bitacora en el archivo
    private static void createLog(StringBuilder logBuilder, String logFileName) {
        File logFile = new File(mLogsPath + logFileName);
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(new File(mLogsPath + logFileName), true);
            outputStream.write(logBuilder.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Función general que sirve para validar si se debe crear archivo nuevo, escribir en uno existen o eliminar el primero que se creo.
    public static void saveFile(StringBuilder logBuilder) {
        createFolders();

        int totalLogs = getTotalLogFiles();
        if (totalLogs == 0) {
            createLog(logBuilder, getLogFileName());
        } else {
            String nameFile = getNameOfFile(true);
            if (getSizeFile(nameFile)) {
                createLog(logBuilder, nameFile);
            } else {
                if (totalLogs == DEFAULT_LOG_MAX_NUM_LOG_FILES) {
                    deleteFile(getNameOfFile(false));
                }
                createLog(logBuilder, getLogFileName());
            }
        }
    }

    //Obtiene el nombre del archivo mas reciente o el más viejo
    private static String getNameOfFile(boolean recent) {
        FilenameFilter textFilter = (dir, name) -> name.endsWith(".txt");
        File folder = new File(mLogsPath);
        File[] listOfFiles = folder.listFiles(textFilter);
        List<String> files = new ArrayList<>();
        for (File file : listOfFiles) {
            files.add(file.getName());
        }
        Collections.sort(files);
        Collections.reverse(files);
        if (recent) {
            return files.get(0);
        } else {
            return files.get(4);
        }
    }

    //Crea la carpeta de logs
    private static void createFolders() {
        File documents = new File(PATH);
        documents.mkdirs();

        mLogsPath = PATH + "logs/";
        File folderLogs = new File(mLogsPath);
        folderLogs.mkdirs();
    }

    //Obtiene el total de archivos logs en la carpeta de log
    private static int getTotalLogFiles() {
        FilenameFilter textFilter = (dir, name) -> name.endsWith(".txt");
        File folder = new File(mLogsPath);
        File[] listOfFiles = folder.listFiles(textFilter);
        return listOfFiles.length;
    }

    //Genera nombre para un nuevo archivo log
    private static String getLogFileName() {
        Date currentTime = Calendar.getInstance().getTime();
        String today = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(currentTime);
        return "log-" + today + ".txt";
    }

    //Verifica si se puede escribir en el mismo archivo validando el peso del mismo
    private static boolean getSizeFile(String name) {
        File file = new File(mLogsPath + name);
        long size = file.length();
        return (int) (size / DEFAULT_LOG_MAX_FILE_SIZE) < DEFAULT_LOG_MAX_FILE_SIZE;
    }

    //Elimina un archivo log
    private static void deleteFile(String name) {
        File file = new File(mLogsPath + name);
        file.delete();
    }

    private static void getErrorLine(Throwable throwable){
        for(int i = 0; i<throwable.getStackTrace().length ; i++){
            if(Thread.currentThread().getStackTrace()[4].getClassName().equals(throwable.getStackTrace()[i].getClassName())){
                line =  throwable.getStackTrace()[i].getLineNumber();
            }
        }
    }

    private static  void getErrorStackTrace (Throwable throwable){
        StringWriter writer = new StringWriter();
        PrintWriter printWriter= new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        stacktrace = writer.toString();
    }

    public static void v(String TAG, String mensaje) {
        if (mensaje != null && !mensaje.isEmpty()) {
            Log.v(TAG, mensaje);
            guardarEnArchivo(TAG, VERBOSE, mensaje);
        }
    }

    public static void d(String TAG, String mensaje) {
        if (mensaje != null && !mensaje.isEmpty()) {
            Log.d(TAG, mensaje);
            guardarEnArchivo(TAG, DEBUG, mensaje);
        }
    }

    public static void i(String TAG, String mensaje) {
        if (mensaje != null && !mensaje.isEmpty()) {
            Log.i(TAG, mensaje);
            guardarEnArchivo(TAG, INFO, mensaje);
        }
    }

    public static void w(String TAG, String mensaje) {
        if (mensaje != null && !mensaje.isEmpty()) {
            Log.w(TAG, mensaje);
            guardarEnArchivo(TAG, WARN, mensaje);
        }
    }

    public static void e(String TAG, Throwable throwable) {
        String stackStrace = "";
        for (String token : Arrays.toString(throwable.getStackTrace()).split(",")) {
            stackStrace += token + "\n";
        }
        getErrorLine(throwable);
        getErrorStackTrace(throwable);
        throwable.printStackTrace();
        guardarEnArchivo(TAG, ERROR, stackStrace);
        guardarEnArchivo(TAG,ERROR,stacktrace);
    }

    public static void e(String TAG, String mensaje) {
        if (mensaje != null && !mensaje.isEmpty()) {
            Log.e(TAG, mensaje);
            guardarEnArchivo(TAG, ERROR, mensaje);
        }
    }
}
