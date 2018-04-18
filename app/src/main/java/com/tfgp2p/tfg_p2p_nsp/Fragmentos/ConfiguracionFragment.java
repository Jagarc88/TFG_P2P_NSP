package com.tfgp2p.tfg_p2p_nsp.Fragmentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tfgp2p.tfg_p2p_nsp.ActivitySeleccionCarpeta;
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

    public void downloadSelectiorWindow(View view){
        Intent listCarpeta = new Intent(getActivity().getApplicationContext(), ActivitySeleccionCarpeta.class);
        startActivity(listCarpeta);
    }

    @Override
    public void establecerContenido() {

    }
}