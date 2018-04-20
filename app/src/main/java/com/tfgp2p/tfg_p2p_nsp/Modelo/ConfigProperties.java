package com.tfgp2p.tfg_p2p_nsp.Modelo;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 */

public class ConfigProperties {

    public static boolean isLoaded = false;

    public static final String PROP_FILES_FOLDER = "urlfolderdownload";

    private static Map<String,String> configData = new HashMap<>();

    public static void loadConfiguration(Context context){

        Properties prop = new Properties();
        InputStream input = null;

//        try {

            AssetManager assetManager = context.getAssets();

//            InputStream inputStream = assetManager.open("com/tfgp2p/tfg_p2p_nsp/Modelo/config/config.properties");

//            prop.load(inputStream);

            // Get the property value and save it
            //configData.put(PROP_FILES_FOLDER,prop.getProperty(PROP_FILES_FOLDER));

            isLoaded = true;

 /*       } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
*/
    }

    /**
     * Devuelve la propiedad que se pide atraves de la constante "PROP_[...]"
     * @param constant
     * @return
     */
    public static String getProperty(String constant){
        return configData.get(constant);
    }

    /**
     * Guarda en la variable temporal el nuevo valor de la constante "PROP_[...]"
     * @param constant
     * @param newValue
     */
    public static void setProperty(String constant, String newValue) {
        configData.put(constant, newValue);
    }

    /**
     * Guarda en disco los nuevos valores dados al fichero de configuracion
     */
    public static void savePropertiesOnharddrive(){
        Properties prop = new Properties();
        OutputStream output = null;

        try {
            output = new FileOutputStream("config/config.properties");

            // Establecemos todas las propiedades
            prop.setProperty(PROP_FILES_FOLDER, configData.get(PROP_FILES_FOLDER));

            // save properties to project root folder
            prop.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
