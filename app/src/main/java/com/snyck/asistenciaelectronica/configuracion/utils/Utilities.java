package com.snyck.asistenciaelectronica.configuracion.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Base64;

import com.auth0.android.jwt.JWT;
import com.snyck.asistenciaelectronica.BuildConfig;
import com.snyck.asistenciaelectronica.R;
import com.snyck.asistenciaelectronica.configuracion.Logg.Logg;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utilities {

    public static final String SEPARATOR = "/";
    public static final String FIRMAS = "Firmas";
    public static final String IMAGE = "Image";
    public static final String EXT_JPG = ".jpg";
    public static final String EXT_PNG = ".png";
    public static final String EXT_PDF = ".pdf";
    public static final String EXT_TXT = ".txt";
    public static String UTInvalidLargeDate = "1901-01-01 00:00:00";
    public static String UTDateLargeFormat = "yyyy-MM-dd HH:mm:ss";
    public static String UTDateSmallFormatNOC = "dd/MM/yyyy";
    private static final String TAG_Folder = "UserPhotos";
    public static final String SIGNATURES_DIRECTORY = "Firmas";

    private static final String TAG = Utilities.class.getSimpleName();

    public static boolean fileExists(String path) {
        boolean exists = false;
        File file = new File(Logg.PATH + path);
        if (file.exists()) {
            exists = true;
        }
        return exists;
    }

    public static boolean createDirectory(String name) {
        boolean b = false;
        String path = Logg.PATH + name;
        File docum = new File(path);
        if (!docum.exists() && !docum.isDirectory()) {
            b = docum.mkdir();
        } else {
            Logg.v(TAG, "Carpeta ya existe  " + path);
        }
        return b;
    }

    public static boolean isNullOrEmpty(String text) {
        return text == null || text.trim().isEmpty() || text.trim().equalsIgnoreCase("null");
    }

    public static void writeExtractedFileToDisk(InputStream in, OutputStream outs) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0) {
            outs.write(buffer, 0, length);
        }
        outs.flush();
        outs.close();
        in.close();
    }
    public static Date dateFromString(String dateInString, String dateFormat) {
        Date date;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            date = formatter.parse(dateInString);
        } catch (Exception e) {
            date = Utilities.dateFromString(UTInvalidLargeDate, UTDateLargeFormat);
        }

        return date;
    }

    public static boolean saveFile(String fileName, String chain) {
        try {
            File file = new File(Logg.PATH + fileName);
            File directory = new File(file.getParent());
            if (!directory.exists() && !directory.mkdirs()) {
                Logg.e(TAG, "No se pudo crear el directorio: " + directory.getAbsolutePath());
                return false;
            }
            fileName = file.getAbsolutePath();
            FileOutputStream outputStream = new FileOutputStream(fileName);
            outputStream.write(chain.getBytes());
            outputStream.close();
            return true;
        } catch (Exception e) {
            Logg.e(TAG, e);
            return false;
        }
    }
    public static boolean saveFilePdf(String fileName, byte[] chain) {
        boolean success = true;
        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            outputStream.write(chain);
            outputStream.close();
        } catch (Exception e) {
            success = false;
        }
        return success;
    }

    public static String readFile(String fileName) {
        String path = Logg.PATH + fileName;
        String chain = "";
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            chain = sb.toString();
            br.close();
        } catch (Exception e) {
            Logg.e(TAG, e);
        }
        return chain;
    }
    public static String capitalizarPrimeraLetra(String cadena) {
        if (cadena == null) {
            return "";
        }
        cadena = cadena.replaceAll("( +)", " ").trim();
        if (cadena.length() > 1) {
            return Character.toUpperCase(cadena.charAt(0)) + cadena.substring(1).toLowerCase(Locale.ROOT);
        }

        return cadena.toUpperCase();
    }
    public static int getVersionCode() {
        return (int) Double.parseDouble(BuildConfig.VERSION_NAME.split(" ")[0]);
    }

    public static boolean getJWT(String token) {
        JWT jwt = new JWT(token);
        Date expiresAt = jwt.getExpiresAt();
        return jwt.isExpired(expiresAt.getSeconds());
    }
    public static String documentDirectory() {
        return Logg.PATH;
    }
    public static boolean clearDocuments() {
        if (Utilities.fileExists("")) {
            return deleteDirectoryContent(new File(documentDirectory()));
        }
        return true;
    }
    private static boolean deleteDirectoryContent(File archivo) {
        if (archivo.isDirectory()) {
            File[] files = archivo.listFiles();
            for (int x = 0; x < files.length; x++) {
                if (files[x].isDirectory()) {
                    if (!deleteDirectoryContent(files[x])) {
                        return false;
                    }
                } else {
                    if (!files[x].delete()) {
                        return false;
                    }
                }
            }
        }
        if (!archivo.delete()) {
            return false;
        }
        return true;
    }
    /**
     * Diferencia de dos valores
     * se utiliza el símbolo (-) que conecta los dos números y completa la expresión dada.
     * Este símbolo también es conocido como el signo menos.
     * */
    public static Double calculaDiferencia(double minuendo, double sustraendo){
        Double diferencia = minuendo - sustraendo;
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        df.applyPattern("#.000");
        return Double.valueOf(df.format(diferencia));
    }

    /**
     * multiplicacionConmutativa: esta propiedad refiere, que no importa el orden en el que se multipliquen los números,
     * siempre se obtendrá el mismo resultado. Se puede tener 5 * 4 = 20 o bien 4 * 5 = 20.
     * @param a
     * @param b
     * @return Double
     */
    public static Double multiplicacionConmutativa(double a, double b){
        Double diferencia = a * b;
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        df.applyPattern("#.000");
        return Double.valueOf(df.format(diferencia));
    }
    /**
     * Función para obtener la Suma y posterior la resta de los valores
     * Se suman los valores 1 al 9 con el resultado se hace la resta
     * tomando dia_anterior menos suma de valores
     * */
    public static Double DiferenciaPanContado (double dia_anterior, double inventario_fisico, double pan_crudo, double roscas, double vitrina_gelatina, double inventario_pastel, double soldado, double chocolate_pasta, double pasta){
        Double preDiferencia = inventario_fisico + inventario_pastel + soldado + chocolate_pasta + pasta + pan_crudo + roscas + vitrina_gelatina;
        Double diferencia = dia_anterior - preDiferencia;
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        df.applyPattern("#.000");
        return Double.valueOf(df.format(diferencia));
    }
    /**
     * Multiplicacion_neutro.
     * Propiedad del elemento neutro: Todo número multiplicado por la unidad (1) dará como resultado el mismo número
     * @param multiplicando
     * @param multiplicador
     * @return Double
     * */
    public static Double multiplicacion_neutro (double multiplicando, double multiplicador ){
        Double producto = multiplicando * multiplicador;
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        df.applyPattern("#.000");
        return Double.valueOf(df.format(producto));
    }
    /**
     * Suma de un Array
     * Propiedad conmutativa para la suma
     * @param lista
     * @return Double
     * */
    public static Double sumaArray (ArrayList<Double> lista){
        Double total = 0.0;
        for (int i = 0; i < lista.size() ; i++) {
            total += lista.get(i);
        }
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        df.applyPattern("#.000");
        return Double.valueOf(df.format(total));
    }
    /**
     * sumaConmutativa: esta propiedad refiere, que no importa el orden en el que se sumen los números,
     * siempre se obtendrá el mismo resultado. Se puede tener 5 + 4 = 9 o bien 4 + 5 = 9.
     * @param a
     * @param b
     * @return Double
     */
    public static Double sumaConmutativa(double a, double b){
        Double resultado = a + b;
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        df.applyPattern("#.000");
        return Double.valueOf(df.format(resultado));
    }
    public static boolean saveImage(File file, Bitmap bmp) {
        try (FileOutputStream fOut = new FileOutputStream(file)) {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            return true;
        } catch (Exception e) {
            Logg.e(TAG, e);
            return false;
        }
    }
    public static File guardarFoto(String photoName) {
        String file_path = documentDirectory() + TAG_Folder;
        File dir = new File(file_path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return new File(dir, photoName + EXT_JPG);
    }

    public static Bitmap redimensionarImagen(Bitmap bitmap) {
        int ancho = bitmap.getWidth();
        int alto = bitmap.getHeight();

        if (ancho > (float) 750 || alto > (float) 510) {

            float escalaAncho = (float) 750 / ancho;
            float escalaAlto = (float) 510 / alto;

            Matrix matrix = new Matrix();
            matrix.postScale(escalaAncho, escalaAlto);

            return Bitmap.createBitmap(bitmap, 0, 0, ancho, alto, matrix, false);
        } else {
            return bitmap;
        }
    }
    public static String convertirImgString(Bitmap bitmap) {

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, array);
        byte[] imagenByte = array.toByteArray();

        return Base64.encodeToString(imagenByte, Base64.DEFAULT);
    }

    public static String nSerie(){
        String caracteres = String.valueOf(R.string.validacion_solo_texto);
        String currentDate = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(new Date());
        final int longitudCadena = 10;
        StringBuilder sb = new StringBuilder ();

        for(int l=0; l<=longitudCadena; l++) {
            double aleatorio = Math. random() * caracteres. length();
            int position = (int) aleatorio;
            char letra = caracteres.charAt(position);
            sb.append(letra);
        }

        return currentDate + sb;
    }

    public static String fechaFormat(){
        Date currentTime = Calendar.getInstance().getTime();
        return new SimpleDateFormat("yyyy/MM/dd").format(currentTime);
    }

    public static String fechaFormatSqlite(){
        Date currentTime = Calendar.getInstance().getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(currentTime);
    }

    public static void deleteFiles(String path) {
        File file = new File(Logg.PATH + path);
        if (file.exists()) {
            String deleteCmd = "rm -r " + Logg.PATH + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) {
                Logg.i(TAG, "deleteFiles: ");
            }
        }
    }
    public static String getBase64(String input) {
        return Base64.encodeToString(input.getBytes(), Base64.NO_WRAP);//DEFAULT
    }

    public static String fechaHoraFormat(){
        Date currentTime = Calendar.getInstance().getTime();
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime);
    }

    public static String guardarImagen(Bitmap bitmap,String nombre) {
        File dir = new File(Logg.PATH + IMAGE + "/");
        if (!dir.exists()) {
            boolean directorioCreado = dir.mkdirs();
            if (!directorioCreado)
                Logg.i(TAG, "El directorio " + dir.getName() + " no ha sido creado.");
        }
        String path_img = IMAGE +  "/" + nombre + ".png";
        File file = new File(Logg.PATH + IMAGE + "/", nombre + ".png");
        try (FileOutputStream fOut = new FileOutputStream(file)) {
            if (redimensionarImagen(bitmap).compress(Bitmap.CompressFormat.PNG, 85, fOut)) {
                fOut.flush();
                fOut.close();
            }
        } catch (Exception e) {
            Logg.e(TAG, "Error al guardar archivo --> " + e.getMessage());
            Logg.e(TAG, e);
        }
        Logg.i(TAG,"Ruta " + path_img);
        return  path_img;
    }

    @SuppressLint("HardwareIds")
    public  static String getMac(Context context){
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("tun0:1")) continue;

                byte[] macBytes = nif.getInetAddresses().nextElement().getAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    // res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }
}
