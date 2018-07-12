package com.tfgp2p.tfg_p2p_nsp.View.Fragmentos.PestanaFragments;

import android.view.View;
import android.widget.LinearLayout;

import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.Fichero;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.GestorSistemaFicheros;
import com.tfgp2p.tfg_p2p_nsp.R;
import com.tfgp2p.tfg_p2p_nsp.View.Personalizados.PersonalizedElements.PestanaDescarga.LayoutElementFicheroDescarga;
import com.tfgp2p.tfg_p2p_nsp.View.Utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deekin on 02/12/2017.
 */

public class PestanaDescarga extends PestanaFragment{

    LinearLayout linearLayout_listaDescargados;

    @Override
    protected void rellenaVariables(View view) {
        imageHeadPestana.setImageResource(R.drawable.ic_file_download_black_24dp);
        textTitutloHeadPestana.setText(getText(R.string.pestana_titulo_descarga));

        linearLayout_listaDescargados = ViewUtils.createConteiner(layoutRellenoPestana, R.id.elemento_listaseleccionar);

        loadFragmentAPestanya();
    }

    private void loadFragmentAPestanya(){

        List<Fichero> ficheroList = GestorSistemaFicheros.extraerIncompletos();
        List<LayoutElementFicheroDescarga> fragments = new ArrayList<LayoutElementFicheroDescarga>();

        linearLayout_listaDescargados.removeAllViews();
        for(int i=0;i<ficheroList.size();i++) {
            LayoutElementFicheroDescarga fragmentED = new LayoutElementFicheroDescarga(this.getActivity());
            fragments.add(fragmentED);
            linearLayout_listaDescargados.addView(fragmentED);
        }

        for(int i=0;i<fragments.size();i++){
            Fichero aFichero = ficheroList.get(i);
            fragments.get(i).setFicheroCargado(aFichero);
            fragments.get(i).loadData();
        }
    }
}
