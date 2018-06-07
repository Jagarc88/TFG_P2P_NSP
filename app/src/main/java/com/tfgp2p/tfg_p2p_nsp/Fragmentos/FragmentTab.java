package com.tfgp2p.tfg_p2p_nsp.Fragmentos;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tfgp2p.tfg_p2p_nsp.Fragmentos.PestanaFragments.PestanaFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deekin on 29/11/2017.
 */

public abstract class FragmentTab extends Fragment {

    public static final int TABNAME_INICIO = 1;
    public static final int TABNAME_AMIGOS = 2;
    public static final int TABNAME_FICHEROS = 3;
    public static final int TABNAME_CONFIGURACION = 4;

    /**
     * Lista que se usara para comprobar patrones de todas las pestanyas de forma general
     */
    protected List<PestanaFragment> listaPestanas = new ArrayList<>();

    public static FragmentTab obtainFragment(int tabID){
        FragmentTab fragmentTab = null;

        switch(tabID){
            case TABNAME_INICIO:{
                fragmentTab = new InicioFragment();
            }break;
            case TABNAME_FICHEROS:{
                fragmentTab = new FicherosFragment();
            }break;
            case TABNAME_AMIGOS:{
                fragmentTab = new AmigosFragment();
            }break;
            case TABNAME_CONFIGURACION:{
                fragmentTab = new ConfiguracionFragment();
            }break;
        }

        return fragmentTab;
    }

    public void colapsarPestanya(PestanaFragment pestanaFragment){

        for (PestanaFragment pestana: listaPestanas ) {
            if(pestana == pestanaFragment){
                pestanaFragment.colapsarPestanya(!pestanaFragment.getColapsed());
            } else{
                pestana.colapsarPestanya(false);
            }
        }
    }

    public abstract void establecerContenido();
}
