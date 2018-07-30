package com.tfgp2p.tfg_p2p_nsp.View.Personalizados.PersonalizedElements.ActivityPopupAmigo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos.Amigo;
import com.tfgp2p.tfg_p2p_nsp.View.Personalizados.PersonalizedElements.LayoutsFicheros.LayoutElementFichero;

import java.util.ArrayList;
import java.util.List;

public class ListViewSeleccionables extends LinearLayout {

    private List<LayoutElementFichero> elementFicheros;
    private Amigo amigo;

    public ListViewSeleccionables(Context context) {
        super(context);
    }

    public ListViewSeleccionables(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewSeleccionables(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void fillData(List<LayoutElementFichero> listables, Amigo amigo){
        this.amigo = amigo;
        this.elementFicheros = listables;

        for (LayoutElementFichero listable: listables) {
            this.addView(listable);
        }
    }

    public List<LayoutElementFichero> getSelected(){
        List<LayoutElementFichero> selected = new ArrayList<>();
        for (LayoutElementFichero layoutElementFichero: this.elementFicheros) {
            if(layoutElementFichero.isSelected()){
                selected.add(layoutElementFichero);
            }
        }
        return selected;
    }
}
