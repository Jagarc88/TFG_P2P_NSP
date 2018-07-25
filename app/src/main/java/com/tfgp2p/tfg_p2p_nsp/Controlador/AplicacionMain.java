package com.tfgp2p.tfg_p2p_nsp.Controlador;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.tfgp2p.tfg_p2p_nsp.AlertException;
import com.tfgp2p.tfg_p2p_nsp.Conexion.Cliente;
import com.tfgp2p.tfg_p2p_nsp.Conexion.Servidor;
import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.DHAmigos;
import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.DHConfiguration;
import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.DatabaseHelper;
import com.tfgp2p.tfg_p2p_nsp.Modelo.ConfigProperties;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos.Amigo;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos.GestorSistemaAmigos;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.Fichero;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.GestorSistemaFicheros;
import com.tfgp2p.tfg_p2p_nsp.View.Personalizados.PersonalizedElements.PestanaAmigos.LayoutElementAmigos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jagar on 25/11/2017.
 */

public class AplicacionMain extends Application {

    GestorSistemaFicheros gestorSistemaFicheros;

    private Servidor server;
    private Cliente client;
    private GestorSistemaAmigos gestorSistemaAmigos;

    public static DHAmigos dhAmigos;
    public static DHConfiguration dhConfiguration;

    @Override
    public void onCreate() {
        super.onCreate();

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Crea las bases de datos
        dhAmigos = DHAmigos.createInstance(databaseHelper);
        dhConfiguration = DHConfiguration.createInstance(databaseHelper);

        if(ConfigProperties.loadConfig()==0) {
            //TODO Crear un metodo para cargar un directorio creado por la aplicacion
            ConfigProperties.setProperty(ConfigProperties.PROP_FILES_FOLDER, "/sdcard/");
            ConfigProperties.saveProperties();
        }
        gestorSistemaFicheros = GestorSistemaFicheros.getInstance();

        try {
            gestorSistemaFicheros.fillContent(ConfigProperties.getProperty(ConfigProperties.PROP_FILES_FOLDER).getProperty());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //ANYADIMOS LOS FICHEROS INCOMPLETOS DE PRUEBA
        List<Fichero> ficheroList = new ArrayList<>();
        for(int i=0;i<=5;i++){
            Fichero fichero = new Fichero("INCOMPLETO_"+i+".tfg",ConfigProperties.getProperty(ConfigProperties.PROP_FILES_FOLDER).getProperty());
            fichero.setIncompleto(true);
            fichero.setTamanyoTotal(5);
            fichero.setCantidadDescargada(i);
            ficheroList.add(fichero);
        }
        GestorSistemaFicheros.addFilesDir(ficheroList);
/*
        try {
            GestorSistemaAmigos.addFriend(new Amigo("Manuel","001", GestorSistemaAmigos.STATE_USER_OFFLINE));
            GestorSistemaAmigos.addFriend(new Amigo("Antonio","002", GestorSistemaAmigos.STATE_USER_OFFLINE));
            GestorSistemaAmigos.addFriend(new Amigo("Sonia","003", GestorSistemaAmigos.STATE_USER_ONLINE));
        } catch (AlertException e) {
            e.printStackTrace();
        }
*/
        // Inicia las conexiones
        new Thread(new Runnable(){
            public void run(){
                gestorSistemaAmigos = GestorSistemaAmigos.getInstance();
                server = Servidor.getInstance();
                client = Cliente.getInstance();
            }
        }).start();
    }
}
