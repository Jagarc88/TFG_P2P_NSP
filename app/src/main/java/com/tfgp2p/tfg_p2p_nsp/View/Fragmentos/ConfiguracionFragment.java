package com.tfgp2p.tfg_p2p_nsp.View.Fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tfgp2p.tfg_p2p_nsp.R;
import com.tfgp2p.tfg_p2p_nsp.View.Fragmentos.PestanaFragments.PestanaFragment;

/**
 * Created by Deekin on 18/03/2018.
 */

public class ConfiguracionFragment extends FragmentTab {

    protected PestanaFragment pestanaFragment;

    protected LinearLayout pestanaLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewInicio = inflater.inflate(R.layout.fragment_configuracion, container, false);

        return viewInicio;
    }

    @Override
    public void establecerContenido() {

    }
}