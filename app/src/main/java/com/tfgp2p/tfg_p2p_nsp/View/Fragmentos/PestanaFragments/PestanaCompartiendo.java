package com.tfgp2p.tfg_p2p_nsp.View.Fragmentos.PestanaFragments;

import android.view.View;
import android.widget.LinearLayout;

import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.Fichero;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.GestorSistemaFicheros;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.ICompartiendoDataCambio;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.TipoFichero;
import com.tfgp2p.tfg_p2p_nsp.View.Personalizados.PersonalizedElements.LayoutsFicheros.LayoutElementFichero;
import com.tfgp2p.tfg_p2p_nsp.View.Utils.ViewUtils;
import com.tfgp2p.tfg_p2p_nsp.R;

import java.util.List;

/**
 * Created by Deekin on 03/12/2017.
 */
public class PestanaCompartiendo extends PestanaFragment implements ICompartiendoDataCambio {

    LinearLayout linearLayout_listaCompartiendo;

    @Override
    protected void rellenaVariables(View view) {
        imageHeadPestana.setImageResource(R.drawable.ic_screen_share_black_24dp);
        textTitutloHeadPestana.setText(getText(R.string.pestana_titulo_compartiendo));

        linearLayout_listaCompartiendo = view.findViewById(R.id.pestana_relleno);

        draw(ViewUtils.createConteiner(linearLayout_listaCompartiendo, R.id.elemento_listaseleccionar));
    }

    private void draw(LinearLayout linearLayout) {
        List<Fichero> dir = GestorSistemaFicheros.getFiles();

        // Elimina toodo el contenido
        linearLayout.removeAllViews();

        // Rellena con el nuevo contenido
        for (Fichero item: dir) {
            if(item.getTipoFichero() != TipoFichero.FOLD) {
                View newPestanya = crear_pestanya(item);
                linearLayout.addView(newPestanya);
            }
        }
    }

    public View crear_pestanya(Fichero fichero){
        LayoutElementFichero elementFileslot = new LayoutElementFichero(getActivity());

        elementFileslot.fillData(fichero);

        return elementFileslot;
    }

    @Override
    public void reloadCompartiendoCarpetaData() {
        draw(linearLayout_listaCompartiendo);
    }
}
