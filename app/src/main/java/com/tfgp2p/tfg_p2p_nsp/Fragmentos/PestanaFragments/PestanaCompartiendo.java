package com.tfgp2p.tfg_p2p_nsp.Fragmentos.PestanaFragments;

import com.tfgp2p.tfg_p2p_nsp.R;

/**
 * Created by Deekin on 03/12/2017.
 */

public class PestanaCompartiendo extends PestanaFragment {
    @Override
    protected void rellenaVariables() {
        imageHeadPestana.setImageResource(R.drawable.ic_screen_share_black_24dp);
        textTitutloHeadPestana.setText(getText(R.string.pestana_titulo_compartiendo));
    }
}
