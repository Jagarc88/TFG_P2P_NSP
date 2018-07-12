package com.tfgp2p.tfg_p2p_nsp.View.Fragmentos;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.tfgp2p.tfg_p2p_nsp.R;
import com.tfgp2p.tfg_p2p_nsp.View.Fragmentos.PestanaFragments.PestanaAmigos;
import com.tfgp2p.tfg_p2p_nsp.View.Fragmentos.PestanaFragments.PestanaFragment;

/**
 * Created by Deekin on 30/11/2017.
 */

public class AmigosFragment extends FragmentTab {

    protected PestanaFragment pestanaFragment;

    protected LinearLayout pestanaLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewInicio = inflater.inflate(R.layout.fragment_amigos, container, false);

        pestanaLayout = viewInicio.findViewById(R.id.layout_pestana);

        pestanaFragment = new PestanaAmigos();

        pestanaFragment.setFragmentTab(this);
        pestanaFragment.setExpandable(false);

        listaPestanas.add(pestanaFragment);

        FragmentManager fragMan = getFragmentManager();
        FragmentTransaction fragTransaction = fragMan.beginTransaction();

        fragTransaction.replace(pestanaLayout.getId(), pestanaFragment, "fragment");
        fragTransaction.commit();

        return viewInicio;
    }

    @Override
    public void establecerContenido() {

    }
}