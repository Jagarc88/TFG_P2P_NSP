package com.tfgp2p.tfg_p2p_nsp.Controlador;

import android.app.Application;

/**
 * Created by jagar on 25/11/2017.
 */

public class aplicacionMain extends Application {

    @Override
    public void onCreate() {
        System.out.println("SOLO CUANDO SE INICIA LA APP");
        super.onCreate();
    }
}
