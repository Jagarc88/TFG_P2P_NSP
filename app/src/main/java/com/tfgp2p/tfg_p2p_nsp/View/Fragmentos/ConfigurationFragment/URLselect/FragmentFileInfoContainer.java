package com.tfgp2p.tfg_p2p_nsp.View.Fragmentos.ConfigurationFragment.URLselect;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.Fichero;
import com.tfgp2p.tfg_p2p_nsp.R;

public class FragmentFileInfoContainer extends Fragment {

    private Fichero item;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.elemento_listaseleccionar, container, false);

        item = null;

        return view;
    }

    public void setFichero(Fichero item){
        this.item = item;
    }

    public Fichero getFichero(){
        return item;
    }

    public void setText(String name){
        TextView nameEtiqueta = getView().findViewById(R.id.name_file);

        nameEtiqueta.setText(name);
    }
}
