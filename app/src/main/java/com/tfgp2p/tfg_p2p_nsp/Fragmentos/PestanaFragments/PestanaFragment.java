package com.tfgp2p.tfg_p2p_nsp.Fragmentos.PestanaFragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tfgp2p.tfg_p2p_nsp.Fragmentos.FragmentTab;
import com.tfgp2p.tfg_p2p_nsp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class PestanaFragment extends Fragment {

    protected TextView textTitutloHeadPestana;
    protected ImageView imageHeadPestana;
    protected ImageView imageExpandir;

    protected FrameLayout layoutRellenoPestana;

    protected FragmentTab fragmentTab;

    public PestanaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewPestana = inflater.inflate(R.layout.fragment_pestana, container, false);

        textTitutloHeadPestana = viewPestana.findViewById(R.id.pestanahead_texto);
        imageHeadPestana = viewPestana.findViewById(R.id.pestanahead_image_icon);
        imageExpandir = viewPestana.findViewById(R.id.pestana_button_expandir);

        layoutRellenoPestana = viewPestana.findViewById(R.id.pestana_relleno);

        rellenaVariables();

        return viewPestana;
    }

    /**
     * Rellena la pestanya con los datos propios
     */
    protected abstract void rellenaVariables();

    public void setFragmentTab(FragmentTab fragmentTab) {
        this.fragmentTab = fragmentTab;
    }
}
