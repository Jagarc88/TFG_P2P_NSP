package com.tfgp2p.tfg_p2p_nsp.View.Fragmentos.PestanaFragments;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.Fichero;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.GestorSistemaFicheros;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.ICompartiendoDataCambio;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.TipoFichero;
import com.tfgp2p.tfg_p2p_nsp.R;
import com.tfgp2p.tfg_p2p_nsp.View.Utils.ViewUtils;

import java.util.List;

/**
 * Created by Deekin on 03/12/2017.
 */
public class PestanaCompartiendo extends PestanaFragment implements ICompartiendoDataCambio{

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
                View newPestanya = crear_pestanya(item.getNombre());
                linearLayout.addView(newPestanya);
            }
        }
    }

    public View crear_pestanya(String name){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View elementFileslot = inflater.inflate(R.layout.elemento_fileslot_small, null, false);

        TextView textName = elementFileslot.findViewById(R.id.fileslot_small_nameFile);
        ImageView iconView = elementFileslot.findViewById(R.id.fileslot_small_icon);

        textName.setText(name);
        iconView.setImageDrawable(TipoFichero.obtainDrawable(TipoFichero.obtainTipoFichero(name), getActivity()));

        return elementFileslot;
    }

    @Override
    public void reloadCompartiendoCarpetaData() {
        draw(linearLayout_listaCompartiendo);
    }
}
