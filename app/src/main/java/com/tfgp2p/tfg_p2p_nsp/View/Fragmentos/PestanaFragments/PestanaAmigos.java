package com.tfgp2p.tfg_p2p_nsp.View.Fragmentos.PestanaFragments;

import android.view.View;
import android.widget.LinearLayout;

import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos.Amigo;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos.GestorSistemaAmigos;
import com.tfgp2p.tfg_p2p_nsp.R;
import com.tfgp2p.tfg_p2p_nsp.View.Personalizados.PersonalizedElements.PestanaAmigos.LayoutElementAmigos;
import com.tfgp2p.tfg_p2p_nsp.View.Utils.ViewUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Deekin on 02/12/2017.
 */

public class PestanaAmigos extends PestanaFragment{

    private Map<Amigo,LayoutElementAmigos> amigoLinearLayoutMap = new HashMap<>();

    @Override
    protected void rellenaVariables(View view) {
        imageHeadPestana.setImageResource(R.drawable.ic_people_black_24dp);
        textTitutloHeadPestana.setText(getText(R.string.pestana_titulo_amigos));

        LinearLayout layoutRellenoScroller = ViewUtils.createConteiner(layoutRellenoPestana, R.id.elemento_listaseleccionar);

        fillPestana(layoutRellenoScroller,amigoLinearLayoutMap,GestorSistemaAmigos.amigoList);
    }

    private void fillPestana(LinearLayout layoutContainer, Map<Amigo, LayoutElementAmigos> amigoLinearLayoutMap, List<Amigo> amigoList){
        layoutContainer.removeAllViews();
        for (Amigo amigo: amigoList) {
            LayoutElementAmigos layoutElementAmigos = new LayoutElementAmigos(getActivity());
            layoutElementAmigos.setData(amigo);

            amigoLinearLayoutMap.put(amigo,layoutElementAmigos);
            layoutContainer.addView(layoutElementAmigos);
        }
    }
}
