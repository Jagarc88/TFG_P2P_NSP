package com.tfgp2p.tfg_p2p_nsp.View.Fragmentos.PestanaFragments;

import com.tfgp2p.tfg_p2p_nsp.View.Personalizados.PersonalizedElements.PestanaAmigos.LayoutElementAmigos;

import java.util.ArrayList;
import java.util.List;

public class PestanaAmigosSelectable extends PestanaAmigos{

    public PestanaAmigosSelectable() {
        super();
        listaAmigosSeleccionable = true;
    }

    /**
     * Devuelve uan lista de los LayoutElementAmigos seleccionados
     * @return
     */
    public List<LayoutElementAmigos> getListAmigosSelect(){
        List<LayoutElementAmigos> amigoList = new ArrayList<>();
        for (LayoutElementAmigos layAmigo: amigoLinearLayoutMap.values()) {
            if(layAmigo.isSelected()){
                amigoList.add(layAmigo);
            }
        }
        return amigoList;
    }
}
