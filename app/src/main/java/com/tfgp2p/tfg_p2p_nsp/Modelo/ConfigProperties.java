package com.tfgp2p.tfg_p2p_nsp.Modelo;

import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.DHConfiguration;
import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.Exception.DatabaseNotLoadedException;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */

public class ConfigProperties{

    public static boolean isLoaded = false;

    public static final String PROP_FILES_FOLDER = "urlfolderdownload";

    private static Map<String,Property> configData = new HashMap<>();

    /**
     * Carga la base de datos, devuelve el numero de elementos cargados
     * @return
     */
    public static int loadConfig(){
        try {
            Map<String, Object> mapLoaded = DAO.databaseConfiguration().getDataByID(PROP_FILES_FOLDER);
            if(mapLoaded!=null) {
                configData.put(PROP_FILES_FOLDER, new Property(PROP_FILES_FOLDER,(String)mapLoaded.get(DHConfiguration.COL_VALUE)));
                return mapLoaded.size();
            }
        } catch (DatabaseNotLoadedException e){
            e.printStackTrace();
        } finally {
            return 0;
        }
    }

    /**
     * Devuelve la propiedad que se pide atraves de la constante "PROP_[...]"
     * @param constant
     * @return
     */
    public static Property getProperty(String constant){
        return configData.get(constant);
    }

    /**
     * Guarda en la variable temporal el nuevo valor de la constante "PROP_[...]"
     * @param constant
     * @param newValue
     */
    public static void setProperty(String constant, String newValue) {
        configData.put(constant, new Property(constant, newValue));
    }

    /**
     * Guarda en disco los nuevos valores dados al fichero de configuracion
     */
    public static void saveProperties(){
        DHConfiguration dhConfiguration = DHConfiguration.getInstance();

        try {
            for (Property property : configData.values()) {
                // Establecemos todas las propiedades
                dhConfiguration.updateElement(property);
            }
        }catch(DatabaseNotLoadedException e){
            e.printStackTrace();
        }
    }
}
