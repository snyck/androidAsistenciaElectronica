package com.snyck.asistenciaelectronica.config.utils;


import com.snyck.asistenciaelectronica.config.Logg.Logg;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class UTStringUtilities {

    public static final String REGEXONLYNUMBERS = "[^\\d]";
    public static final String EMPTY = "";
    public static final String COMA = ",";

    public static String left(String str, int index) {
        try {
            if (str.length() > index) {
                return str.substring(0, index);
            } else {
                return str;
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static String right(String str, int index) {
        try {
            if (str.length() > index) {
                return str.substring(str.length() - index);
            }
        } catch (Exception e) {
            str = "";
        }
        return str;
    }

    public static int sumString(String str) {
        int sum = 0;
        for (int i = 0; i < str.length(); i++) {
            sum = sum + Integer.parseInt(str.substring(i, i + 1));
        }
        if (sum > 9) {
            return sumString("" + sum);
        }
        return sum;
    }

    public static int sumStringSize(String str, int size) {
        Double limit = Math.pow(10, size);
        int sum = 0;
        for (int i = 0; i < str.length(); i++) {
            sum = sum + Integer.parseInt(str.substring(i, i + 1));
        }
        if (sum >= limit) {
            return sumString("" + sum);
        }
        return sum;
    }

    public static String clean(String str) {
        str = str.replace("Á", "A");
        str = str.replace("É", "E");
        str = str.replace("Í", "I");
        str = str.replace("Ó", "O");
        str = str.replace("Ú", "U");
        str = str.replace("Ü", "U");
        str = str.replace("á", "a");
        str = str.replace("é", "e");
        str = str.replace("í", "i");
        str = str.replace("ó", "o");
        str = str.replace("ú", "u");
        str = str.replace("ü", "u");
        str = str.replace("Ñ", "N");
        str = str.replace("ñ", "n");
        str = str.replace("?", "");
        str = str.replace("&", "");
        str = str.replace("'", "");
        str = str.replace("\"", "");
        str = str.replace("+", "");
        return str;
    }

    public static String substringWithNSMakeRange(String string, int beginIndex, int increase) {
        return string.substring(beginIndex, beginIndex + increase).trim();
    }

    public static Double parseToDecimal(String chain) {
        return Double.parseDouble(chain) / 100;
    }

    public static String removeSpaces(String str) {
        //public static String quitarEspacios(String string) { //TODO AVM DELETE
        String chainWithFormat = "";
        String copyChain = str.trim();
        String aux = " ";

        for (int i = 0; i < copyChain.length(); i++) {
            if (!aux.equals(Character.toString(copyChain.charAt(i)))) {
                chainWithFormat += Character.toString(copyChain.charAt(i));
            } else if (((i + 1) < copyChain.length()) && !aux.equals(Character.toString(copyChain.charAt(i + 1)))) {
                chainWithFormat += " ";
            }
        }
        return chainWithFormat;
    }

    public static String formatHTMLWithKeys(String chain, String keylessTag, String replacementCharacter) {
        //public static String formatearHTMLconLlaves(String cadena, String etiquetaSinLlaves, String caracterReemplazo) { //TODO AVM DELETE
        int counter = 0;
        return searchInChain(chain, keylessTag, replacementCharacter, counter);
    }

    public static boolean isNullOrEmpty(String text) {
        return text == null || text.trim().isEmpty() || text.trim().equalsIgnoreCase("null");
    }

    private static String searchInChain(String chain, String keylessTag, String replacementCharacter, int counter) {
        int pos = chain.indexOf(replacementCharacter);
        if (pos != -1) {
            counter++;
            int a = counter % 2;
            if (a == 0) {
                chain = new StringBuilder(chain).replace(pos, pos + 1, "</" + keylessTag + ">").toString();
            } else {
                chain = new StringBuilder(chain).replace(pos, pos + 1, "<" + keylessTag + ">").toString();
            }
            chain = searchInChain(chain, keylessTag, replacementCharacter, counter);
        }
        return chain;
    }

    public static boolean isNumericString(String string) {
        try {
            if (isNullOrEmpty(string)) {
                return false;
            }
            int value = Integer.parseInt(string);
            /*if (LGTest.isDebug()) {
                CCLogger.i(UTStringUtilities.class.getSimpleName(), String.valueOf(value));
            }*/
            return true;
        } catch (Exception ex) {
            Logg.e(UTStringUtilities.class.getSimpleName(), ex.toString());
            return false;
        }
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

    public static String cantidadFormateada(String originalString) {
        Long longval;
        String formattedString = "";
        if (originalString.contains(UTStringUtilities.COMA)) {
            originalString = originalString.replaceAll(UTStringUtilities.COMA, UTStringUtilities.EMPTY);
        }
        if (!originalString.isEmpty()) {
            longval = Long.parseLong(originalString);

            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            formatter.applyPattern("##,###");
            formattedString = formatter.format(longval);
        }

        return formattedString;
    }

    public static int parseInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            Logg.e(UTStringUtilities.class.getSimpleName(), e.toString());
        }
        return -1;
    }

    public static String format4Digits(String cadena, String separador) {
        int blokes = cadena.length() / 4;
        StringBuilder strFinal = new StringBuilder();

        for (int i = 0; i < blokes; i++) {
            if(strFinal.length() > 0) {
                strFinal.append(separador);
            }
            strFinal.append(cadena.substring(4 * i, 4 * i + 4));
        }

        if(cadena.length() % 4 > 0) {
            strFinal.append(cadena.substring(4 * blokes));
        }

        return strFinal.toString();
    }

    public static String cleanErrorWord(String cadena) {
        if (isNullOrEmpty(cadena)) {
            return "";
        }

        String [] palabras = cadena.split(" ");
        StringBuilder cadenaFinal = new StringBuilder();
        for (String word : palabras) {
            if(cadenaFinal.length() > 0) {
                cadenaFinal.append(" ");
            }
            if(word.equalsIgnoreCase("ERROR") ||
                    word.equalsIgnoreCase("ERRO") ||
                    word.equalsIgnoreCase("ERR") ||
                    word.equalsIgnoreCase("WARN") ||
                    word.equalsIgnoreCase("WARNING") ||
                    word.equalsIgnoreCase("HERROR")) {
                cadenaFinal.append("inco");
            } else {
                cadenaFinal.append(word);
            }
        }
        return cadenaFinal.toString();
    }

    public static String capitalizarPalabras(String cadena) {
        if (cadena == null) {
            return "";
        }

        StringBuilder resultado = new StringBuilder();
        String[] palabras = cadena.replaceAll("( +)", " ").trim().split(" ");
        for (String palabra : palabras) {
            resultado.append(String.format(Locale.US, "%s ", capitalizarPrimeraLetra(palabra)));
        }
        return resultado.toString();
    }

    public static String trimLeft(String cadena, char caracter) {
        if (cadena.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (char element : cadena.toCharArray()) {
            if (element == caracter) {
                builder.append(element);
            } else {
                break;
            }
        }

        return cadena.replace(builder.toString(), "");
    }
}
