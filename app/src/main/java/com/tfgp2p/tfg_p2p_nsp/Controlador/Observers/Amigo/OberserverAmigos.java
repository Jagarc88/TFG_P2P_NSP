package com.tfgp2p.tfg_p2p_nsp.Controlador.Observers.Amigo;

public class OberserverAmigos {

    public static void refreshAllViewsAmigosList(){
        for (IListenerAmigosList amigosList: IListenerAmigosList.listenerAmigosLists) {
            amigosList.refreshListShown();
        }
    }
}
