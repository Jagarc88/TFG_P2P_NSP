package com.tfgp2p.tfg_p2p_nsp.Fragmentos.PestanaFragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        imageExpandir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFragmentTabCollapse();
            }
        });

        layoutRellenoPestana = viewPestana.findViewById(R.id.pestana_relleno);

        rellenaVariables();

        return viewPestana;
    }

    private void callFragmentTabCollapse(){
        fragmentTab.colapsarPestanyaDada(this);
    }

    /**
     * Rellena la pestanya con los datos propios
     */
    protected abstract void rellenaVariables();

    /**
     * Colapsa o abre la pestanya
     * @param expandir
     */
    public void colapsarPestanya(boolean expandir){
        int estado;
        int newArrowImage;

        System.out.println("Pestanya: "+expandir);

        if(expandir){
            estado = LinearLayout.VISIBLE;
            newArrowImage = R.drawable.ic_expand_less_black_24dp;
        }else{
            estado = LinearLayout.GONE;
            newArrowImage = R.drawable.ic_expand_more_black_24dp;
        }

        imageExpandir.setImageResource(newArrowImage);
        layoutRellenoPestana.setVisibility(estado);
    }

    public void setFragmentTab(FragmentTab fragmentTab) {
        this.fragmentTab = fragmentTab;
    }
}
