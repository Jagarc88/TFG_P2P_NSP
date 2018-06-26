package com.tfgp2p.tfg_p2p_nsp.Controlador;

import android.app.Application;

import com.tfgp2p.tfg_p2p_nsp.Modelo.ConfigProperties;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.Fichero;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.GestorSistemaFicheros;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jagar on 25/11/2017.
 */

public class AplicacionMain extends Application {

    GestorSistemaFicheros gestorSistemaFicheros;

    @Override
    public void onCreate() {
        super.onCreate();

        ConfigProperties.loadConfiguration(this);
        gestorSistemaFicheros = GestorSistemaFicheros.getInstance();

        try {
            gestorSistemaFicheros.fillContent(ConfigProperties.getProperty(ConfigProperties.PROP_FILES_FOLDER));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //ANYADIMOS LOS FICHEROS INCOMPLETOS DE PRUEBA
        List<Fichero> ficheroList = new ArrayList<>();
        for(int i=0;i<5;i++){
            Fichero fichero = new Fichero("INCOMPLETO_"+i+".tfg",ConfigProperties.getProperty(ConfigProperties.PROP_FILES_FOLDER));
            fichero.setIncompleto(true);
            fichero.setTamanyoTotal(5);
            fichero.setCantidadDescargada(i);
            ficheroList.add(fichero);
        }
        GestorSistemaFicheros.addFilesDir(ficheroList);
    }
}
