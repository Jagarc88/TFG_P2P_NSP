package com.tfgp2p.tfg_p2p_nsp.View.Fragmentos;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tfgp2p.tfg_p2p_nsp.View.Fragmentos.PestanaFragments.PestanaCompartiendo;
import com.tfgp2p.tfg_p2p_nsp.View.Fragmentos.PestanaFragments.PestanaDescarga;
import com.tfgp2p.tfg_p2p_nsp.View.Fragmentos.PestanaFragments.PestanaFragment;
import com.tfgp2p.tfg_p2p_nsp.R;

/**
 * Created by Deekin on 30/11/2017.
 */

public class FicherosFragment extends FragmentTab {

    protected PestanaFragment pestanaFragmentTop;
    protected PestanaFragment pestanaFragmentBottom;

    protected LinearLayout pestanaLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewInicio = inflater.inflate(R.layout.fragment_ficheros, container, false);

        pestanaLayout = viewInicio.findViewById(R.id.layout_pestana);

        pestanaFragmentTop = new PestanaDescarga();
        pestanaFragmentBottom = new PestanaCompartiendo();

        pestanaFragmentBottom.setFragmentTab(this);
        pestanaFragmentTop.setFragmentTab(this);

        listaPestanas.add(pestanaFragmentBottom);
        listaPestanas.add(pestanaFragmentTop);

        FragmentManager fragMan = getFragmentManager();
        FragmentTransaction fragTransaction = fragMan.beginTransaction();
        fragTransaction.replace(pestanaLayout.getId(), pestanaFragmentTop, "fragment_top");
        fragTransaction.commit();
        fragTransaction = fragMan.beginTransaction();
        fragTransaction.add(pestanaLayout.getId(), pestanaFragmentBottom, "fragment_bottom");
        fragTransaction.commit();

        establecerContenido();

        return viewInicio;
    }

    @Override
    public void establecerContenido() {}
}
