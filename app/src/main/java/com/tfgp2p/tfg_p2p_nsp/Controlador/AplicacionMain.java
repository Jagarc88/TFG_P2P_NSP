package com.tfgp2p.tfg_p2p_nsp.Controlador;

import android.app.Application;

import com.tfgp2p.tfg_p2p_nsp.Modelo.ConfigProperties;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.GestorSistemaFicheros;

import java.io.IOException;

/**
 * Created by jagar on 25/11/2017.
 */

public class AplicacionMain extends Application {

    GestorSistemaFicheros gestorSistemaFicheros;

    @Override
    public void onCreate() {
        super.onCreate();

        ConfigProperties.loadConfiguration(this);

        //
        gestorSistemaFicheros = GestorSistemaFicheros.getInstance();

        try {
            gestorSistemaFicheros.fillContent(ConfigProperties.getProperty(ConfigProperties.PROP_FILES_FOLDER));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
