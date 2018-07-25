package com.tfgp2p.tfg_p2p_nsp.View.Fragmentos.PestanaFragments;

import android.view.View;
import android.widget.LinearLayout;

import com.tfgp2p.tfg_p2p_nsp.Controlador.Observers.Amigo.IListenerAmigosList;
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

public class PestanaAmigos extends PestanaFragment implements IListenerAmigosList{

    protected Map<Amigo,LayoutElementAmigos> amigoLinearLayoutMap = new HashMap<>();

    private LinearLayout layoutRellenoScroller;

    protected boolean listaAmigosSeleccionable;

    public PestanaAmigos(){
        super();
        listaAmigosSeleccionable = false;
        IListenerAmigosList.listenerAmigosLists.add(this);
    }

    @Override
    protected void rellenaVariables(View view) {
        imageHeadPestana.setImageResource(R.drawable.ic_people_black_24dp);
        textTitutloHeadPestana.setText(getText(R.string.pestana_titulo_amigos));

        layoutRellenoScroller = ViewUtils.createConteiner(layoutRellenoPestana, R.id.elemento_listaseleccionar);

        fillPestana();
    }

    private void fillPestana(){
        List<Amigo> amigoList = GestorSistemaAmigos.getAmigoList();
        layoutRellenoScroller.removeAllViews();
        amigoLinearLayoutMap = new HashMap<>();
        for (Amigo amigo: amigoList) {
            LayoutElementAmigos layoutElementAmigos = new LayoutElementAmigos(getActivity(),listaAmigosSeleccionable);
            layoutElementAmigos.setData(amigo);

            amigoLinearLayoutMap.put(amigo,layoutElementAmigos);
            layoutRellenoScroller.addView(layoutElementAmigos);
        }
    }

    @Override
    public void refreshListShown() {
        fillPestana();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IListenerAmigosList.listenerAmigosLists.remove(this);
    }
}
