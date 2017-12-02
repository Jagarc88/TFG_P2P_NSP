package com.tfgp2p.tfg_p2p_nsp.Fragmentos.PestanaFragments;

import com.tfgp2p.tfg_p2p_nsp.R;

/**
 * Created by Deekin on 02/12/2017.
 */

public class PestanaInicioDescarga extends PestanaFragment{
    @Override
    protected void rellenaVariables() {
        imageHeadPestana.setImageResource(R.drawable.ic_file_download_black_24dp);
        textTitutloHeadPestana.setText("Descargas");
    }
}
