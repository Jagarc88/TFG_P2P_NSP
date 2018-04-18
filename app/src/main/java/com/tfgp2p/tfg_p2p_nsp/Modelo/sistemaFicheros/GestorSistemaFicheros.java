package com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * TODO Clase cuya funcion es gestionar el sistema de ficheros
 * - Singleton
 * - En esta primera version se cachea el contenido directo de la carpeta de descarga
 *
 * @author jagarc88
 */
public class GestorSistemaFicheros {

    public static List<ICompartiendoDataCambio> listICompartiendoDataCambios = new ArrayList<>();

    private static GestorSistemaFicheros gestorSistemaFicheros = null;

    private File currentDir;
    private static List<Fichero> dir;
    /*
    private Item itemSelected;
    */
    Map<String,Fichero> mapViewItem;


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

    private void fill(File f) throws IOException{
        dir = new ArrayList<>();

        if(f != null) {
            File[] dirs = f.listFiles();

            try{
                for(File ff: dirs){
                    if(ff.isDirectory()){
                        dir.add(new Fichero(ff.getName(),ff.getAbsolutePath()));
                    }
                }
            }catch(Exception e){}

            Collections.sort(dir);
        }else{
            throw new IOException("La dirección el 'null'.");
        }
    }

    public void fillContent(String Path) throws IOException {

        File file = new File(Path);
        dir = new ArrayList<>();

        if(file.exists() && file.isDirectory()){
            currentDir = file;

            // Si no existe creamos la carpeta
            if(!currentDir.exists()) {
                currentDir.mkdir();
            }

            fill(currentDir);
        } else{
            throw new IOException("No se ha encontrado la carpeta o no es una carpeta.");
        }
    }

    public void reloadDataView() throws IOException {
        fill(currentDir);

    }

    public static List<Fichero> getFiles(){
        getInstance();
        return dir;
    }

    public static void reloadCompartiendoCarpetaData() {

        for (ICompartiendoDataCambio compart: listICompartiendoDataCambios) {
            compart.reloadCompartiendoCarpetaData();
        }
    }
}
