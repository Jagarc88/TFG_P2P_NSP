package com.tfgp2p.tfg_p2p_nsp.Modelo;

import android.content.Context;

import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.DHConfiguration;
import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.DatabaseHelper;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */

public class ConfigProperties {

    public static boolean isLoaded = false;

    public static final String PROP_FILES_FOLDER = "urlfolderdownload";

    private static Map<String,String> configData = new HashMap<>();

    /**
     * Carga la base de datos, devuelve el numero de elementos cargados
     * @param context
     * @return
     */
    public static int loadConfig(Context context){
        Map<String,Object> mapLoaded=DAO.databaseConfiguration(context).getDataByID(PROP_FILES_FOLDER);
        if(mapLoaded!=null) {
            configData.put(PROP_FILES_FOLDER, (String) mapLoaded.get(DHConfiguration.COL_VALUE));
            return mapLoaded.size();
        }else{
            return 0;
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
        // Establecemos todas las propiedades
        DAO.databaseConfiguration(context).updateData(
                new String[]{DHConfiguration.COL_ID_PROPERTY_NAME,DHConfiguration.COL_VALUE},
                new Object[]{PROP_FILES_FOLDER,configData.get(PROP_FILES_FOLDER)},
                new int[]{DatabaseHelper.FIELD_TYPE_STRING,DatabaseHelper.FIELD_TYPE_STRING});
    }
}
