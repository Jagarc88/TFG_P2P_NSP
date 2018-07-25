package com.tfgp2p.tfg_p2p_nsp.View.Fragmentos;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.tfgp2p.tfg_p2p_nsp.AlertException;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos.Amigo;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos.GestorSistemaAmigos;
import com.tfgp2p.tfg_p2p_nsp.R;
import com.tfgp2p.tfg_p2p_nsp.View.Fragmentos.PestanaFragments.PestanaAmigosSelectable;
import com.tfgp2p.tfg_p2p_nsp.View.Personalizados.PersonalizedElements.Dialogs.DialogNewFriend;
import com.tfgp2p.tfg_p2p_nsp.View.Personalizados.PersonalizedElements.PestanaAmigos.LayoutElementAmigos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deekin on 30/11/2017.
 */

public class AmigosFragment extends FragmentTab {

    protected PestanaAmigosSelectable pestanaFragment;

    protected LinearLayout pestanaLayout;

    private LinearLayout buttonDeleteFriend;
    private LinearLayout buttonAddFriend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewInicio = inflater.inflate(R.layout.fragment_amigos, container, false);

        pestanaLayout = viewInicio.findViewById(R.id.layout_pestana);

        buttonDeleteFriend = viewInicio.findViewById(R.id.fragment_amigos_button_delete);
        buttonAddFriend = viewInicio.findViewById(R.id.fragment_amigos_button_anyadiramigo);

        buttonDeleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDeleteFriend();
            }
        });
        buttonAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddFriend();
            }
        });

        pestanaFragment = new PestanaAmigosSelectable();

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
    public void establecerContenido() {}

    public void onClickAddFriend(){
        DialogNewFriend dialogNewFriend = new DialogNewFriend(getActivity());
        dialogNewFriend.show();
    }

    public void onClickDeleteFriend(){
        List<Amigo> listAmigos = new ArrayList<>();
        for (LayoutElementAmigos layoutElementAmigo:pestanaFragment.getListAmigosSelect()) {
            listAmigos.add(layoutElementAmigo.getAmigo());
        }
        GestorSistemaAmigos.deleteFriendsList(listAmigos);
    }
}