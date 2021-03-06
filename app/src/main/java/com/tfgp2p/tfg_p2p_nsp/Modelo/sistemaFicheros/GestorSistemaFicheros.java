package com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros;

import com.tfgp2p.tfg_p2p_nsp.Modelo.ConfigProperties;
import com.tfgp2p.tfg_p2p_nsp.Modelo.Property;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos.Amigo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * - Singleton
 * - En esta primera version se cachea el contenido directo de la carpeta de descarga
 *
 * @author jagarc88
 */
public class GestorSistemaFicheros {

    public static final String DEFAULT_NAME_FOLDER="Friend2friend";
    public static final String EXTERNAL_PATH= "/sdcard";//Environment.getExternalStorageState();

    private static GestorSistemaFicheros gestorSistemaFicheros = null;

    private File currentDir;
    private static List<Fichero> dir;

    /**
     * Se impide que el usuario pueda generar un "new"
     */
    private GestorSistemaFicheros(){
        dir = new ArrayList<>();
    }


    public static GestorSistemaFicheros getInstance() {
        if(gestorSistemaFicheros == null){
            gestorSistemaFicheros = new GestorSistemaFicheros();
        }
        return gestorSistemaFicheros;
    }

    public boolean crearCarpetaCompartir(String path){
        boolean success = true;

        File folder = new File(path);

        if (!folder.exists()) {
            success = folder.mkdirs();
        }

        return success;
    }

    public void inicializaSistemaFicheros(){
        Property filesFolderPathProperty = ConfigProperties.getProperty(ConfigProperties.PROP_FILES_FOLDER);
        String filesFolderPath = null;

        //Si esta vacio, se introduce en base de datos un valor inicial y se crea la carpeta
        if(filesFolderPathProperty==null){
            filesFolderPath=EXTERNAL_PATH+File.separator+DEFAULT_NAME_FOLDER;
            crearCarpetaCompartir(filesFolderPath);

            //Almacenamos la direccion en la base de datos
            ConfigProperties.setProperty(ConfigProperties.PROP_FILES_FOLDER, filesFolderPath);
            ConfigProperties.saveProperties();
        } else{
            filesFolderPath=filesFolderPathProperty.getProperty();
        }

        try {
            fillContent(filesFolderPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fill(File f) throws IOException{
        dir = new ArrayList<>();

        if(f != null) {
            File[] dirs = f.listFiles();

            try{
                for(File ff: dirs){
                    dir.add(new Fichero(ff.getName(),ff.getAbsolutePath()));
                }
            }catch(Exception e){}

            Collections.sort(dir);
        }else{
            throw new IOException("La dirección el 'null'.");
        }
    }

    public void fillContent(String Path) throws IOException {
        currentDir = new File(Path);
        fill(currentDir);
    }

    /**
     * ESTA FUNCION SOLO SE USARA EN CASOS DE TEST
     * @param anyadir
     */
    public static void addFilesDir(List<Fichero> anyadir){
        if(dir != null){
            dir.addAll(anyadir);
        }
    }

    public static List<Fichero> getFiles(){
        getInstance();
        return dir;
    }

    /**
     * Devuelve una lista solo con los ficheros incompletos en la carpeta actual
     * @return
     */
    public static List<Fichero> extraerIncompletos(){
        List<Fichero> ficheroList = new ArrayList<>();

        for (Fichero fichero: dir) {
            if(fichero.isIncompleto()){
                ficheroList.add(fichero);
            }
        }
        return ficheroList;
    }

    public static void downloadFilesFromAmigo(List<Fichero> ficheroSelList, Amigo selectedAmigo) {
        // TODO Gestion de la descarga de los ficheros seleccionados
        for (Fichero fichero:ficheroSelList) {
            System.out.println(fichero.getNombre());
        }
    }
}
