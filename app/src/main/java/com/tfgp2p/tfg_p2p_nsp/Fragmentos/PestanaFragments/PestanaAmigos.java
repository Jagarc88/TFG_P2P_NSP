package com.tfgp2p.tfg_p2p_nsp.Fragmentos.PestanaFragments;

import android.view.View;

import com.tfgp2p.tfg_p2p_nsp.R;

/**
 * Created by Deekin on 02/12/2017.
 */

public class PestanaAmigos extends PestanaFragment{
    @Override
    protected void rellenaVariables(View view) {
        imageHeadPestana.setImageResource(R.drawable.ic_people_black_24dp);
        textTitutloHeadPestana.setText(getText(R.string.pestana_titulo_amigos));
    }
}
