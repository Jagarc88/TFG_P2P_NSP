package com.tfgp2p.tfg_p2p_nsp.Controlador.Observers.Amigo;

import java.util.ArrayList;
import java.util.List;

public class OberserverAmigos {

    public static List<IListenerAmigosList> listenerAmigosLists = new ArrayList<>();

    public static void refreshAllViewsAmigosList(){
        for (IListenerAmigosList amigosList: listenerAmigosLists) {
            amigosList.refreshListShown();
        }
    }
}
