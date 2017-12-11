package com.tfgp2p.tfg_p2p_nsp.Fragmentos;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tfgp2p.tfg_p2p_nsp.Fragmentos.PestanaFragments.PestanaFragment;
import com.tfgp2p.tfg_p2p_nsp.Fragmentos.PestanaFragments.PestanaAmigos;
import com.tfgp2p.tfg_p2p_nsp.Fragmentos.PestanaFragments.PestanaDescarga;
import com.tfgp2p.tfg_p2p_nsp.R;

public class InicioFragment extends FragmentTab {

    protected PestanaFragment pestanaFragmentTop;
    protected PestanaFragment pestanaFragmentBottom;

    protected LinearLayout pestanaLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewInicio = inflater.inflate(R.layout.fragment_inicio, container, false);

        pestanaLayout = viewInicio.findViewById(R.id.layout_pestana);

        pestanaFragmentTop = new PestanaAmigos();
        pestanaFragmentBottom = new PestanaDescarga();

        pestanaFragmentBottom.setFragmentTab(this);
        pestanaFragmentTop.setFragmentTab(this);

        listaPestanas.add(pestanaFragmentBottom);
        listaPestanas.add(pestanaFragmentTop);

        FragmentManager fragMan = getFragmentManager();
        FragmentTransaction fragTransaction = fragMan.beginTransaction();

        fragTransaction.add(pestanaLayout.getId(), pestanaFragmentTop, "fragment");
        fragTransaction.add(pestanaLayout.getId(), pestanaFragmentBottom, "fragment");
        fragTransaction.commit();

        return viewInicio;
    }

    @Override
    public void setExtendable(PestanaFragment pestanaFragment) {

        // Todas las pestanyas son colapsables
        pestanaFragment.setExpandable(true);
    }
}
