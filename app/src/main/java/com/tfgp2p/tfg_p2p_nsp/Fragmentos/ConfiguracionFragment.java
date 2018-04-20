package com.tfgp2p.tfg_p2p_nsp.Fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tfgp2p.tfg_p2p_nsp.R;

/**
 * Created by Deekin on 18/03/2018.
 */

public class ConfiguracionFragment extends FragmentTab {

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