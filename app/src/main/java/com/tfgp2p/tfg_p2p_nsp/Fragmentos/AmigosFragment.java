package com.tfgp2p.tfg_p2p_nsp.Fragmentos;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tfgp2p.tfg_p2p_nsp.Fragmentos.PestanaFragments.PestanaAmigos;
import com.tfgp2p.tfg_p2p_nsp.Fragmentos.PestanaFragments.PestanaFragment;
import com.tfgp2p.tfg_p2p_nsp.R;

/**
 * Created by Deekin on 30/11/2017.
 */

public class AmigosFragment extends FragmentTab {

    protected PestanaFragment pestanaFragment;

    protected FrameLayout mainLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewInicio = inflater.inflate(R.layout.fragment_amigos, container, false);
/*
        mainLayout = viewInicio.findViewById(R.id.layout_pestana);

        pestanaFragment = new PestanaAmigos();

        pestanaFragment.setFragmentTab(this);
        listaPestanas.add(pestanaFragment);

        FragmentManager fragMan = getFragmentManager();
        FragmentTransaction fragTransaction = fragMan.beginTransaction();

        fragTransaction.add(mainLayout.getId(), pestanaFragment , "fragment");
        fragTransaction.commit();
*/
        return viewInicio;
    }
}