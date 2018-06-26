package com.tfgp2p.tfg_p2p_nsp.Modelo;

import android.content.Context;
import android.content.res.AssetManager;
import android.app.Application;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static android.content.Context.MODE_PRIVATE;

/**
 *
 */

public class ConfigProperties {

    public static boolean isLoaded = false;

    public static final String PROP_FILES_FOLDER = "urlfolderdownload";

    private static Map<String,String> configData = new HashMap<>();

    public static void loadConfiguration(Context context){

        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();

        try {

            InputStream inputStream = assetManager.open("config");

            properties.load(inputStream);

            System.out.println("ASDASDASDASD ------ "+properties.getProperty(PROP_FILES_FOLDER));

            // Get the property value and save it
            configData.put(PROP_FILES_FOLDER,properties.getProperty(PROP_FILES_FOLDER));

            isLoaded = true;
            inputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
    public static void saveProperties(Context context){
        FileOutputStream output = null;

        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();

        try {
//            output = assetManager.openFd("config").createOutputStream();
            output = context.openFileOutput("config.properties",
                    MODE_PRIVATE);

            output.write(5);
            // Establecemos todas las propiedades
            properties.setProperty(PROP_FILES_FOLDER, /*configData.get(PROP_FILES_FOLDER)*/"ASDASDASDASDASD");

            // Save properties to project root folder
            properties.store(output, null);
            output.flush();
            output.close();
       } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
