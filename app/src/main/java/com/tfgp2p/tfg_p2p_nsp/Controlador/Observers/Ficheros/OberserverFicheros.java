package com.tfgp2p.tfg_p2p_nsp.Controlador.Observers.Ficheros;

import java.util.ArrayList;
import java.util.List;

public class OberserverFicheros {

    public static List<IListenerFicheroList> listenerFicherossLists = new ArrayList<>();

    public static void refreshAllViewsAmigosList(){
        for (IListenerFicheroList ficherosList: listenerFicherossLists) {
            ficherosList.refreshListShown();
        }
    }
}
