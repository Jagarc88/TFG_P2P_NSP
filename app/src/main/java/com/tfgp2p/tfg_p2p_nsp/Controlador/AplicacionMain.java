package com.tfgp2p.tfg_p2p_nsp.Controlador;

import android.app.Application;

import com.tfgp2p.tfg_p2p_nsp.Conexion.Cliente;
import com.tfgp2p.tfg_p2p_nsp.Conexion.Servidor;
import com.tfgp2p.tfg_p2p_nsp.Modelo.Amigos;
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
    private Amigos amigos;

    @Override
    public void onCreate() {
        super.onCreate();

        if(ConfigProperties.loadConfig(this)==0) {
            //TODO Crear un metodo para cargar un directorio creado por la aplicacion
            ConfigProperties.setProperty(ConfigProperties.PROP_FILES_FOLDER, "/sdcard/");
            ConfigProperties.saveProperties(getApplicationContext());
        }
        gestorSistemaFicheros = GestorSistemaFicheros.getInstance();

        try {
            gestorSistemaFicheros.fillContent(ConfigProperties.getProperty(ConfigProperties.PROP_FILES_FOLDER));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //ANYADIMOS LOS FICHEROS INCOMPLETOS DE PRUEBA
        List<Fichero> ficheroList = new ArrayList<>();
        for(int i=0;i<=5;i++){
            Fichero fichero = new Fichero("INCOMPLETO_"+i+".tfg",ConfigProperties.getProperty(ConfigProperties.PROP_FILES_FOLDER));
            fichero.setIncompleto(true);
            fichero.setTamanyoTotal(5);
            fichero.setCantidadDescargada(i);
            ficheroList.add(fichero);
        }
        GestorSistemaFicheros.addFilesDir(ficheroList);

        GestorSistemaAmigos.amigoList.add(new Amigo("Manuel",001, LayoutElementAmigos.STATE_USER_OFFLINE));
        GestorSistemaAmigos.amigoList.add(new Amigo("Antonio",002, LayoutElementAmigos.STATE_USER_OFFLINE));
        GestorSistemaAmigos.amigoList.add(new Amigo("Sonia",003, LayoutElementAmigos.STATE_USER_ONLINE));

        // Inicia las conexiones
        new Thread(new Runnable(){
            public void run(){
                amigos = Amigos.getInstance();
                server = Servidor.getInstance();
                //client = Cliente.getInstance();
            }
        }).start();
    }
}
