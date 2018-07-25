package com.tfgp2p.tfg_p2p_nsp.Controlador.Observers.Amigo;

import java.util.ArrayList;
import java.util.List;

public interface IListenerAmigosList {

    List<IListenerAmigosList> listenerAmigosLists = new ArrayList<>();

    void refreshListShown();
}
