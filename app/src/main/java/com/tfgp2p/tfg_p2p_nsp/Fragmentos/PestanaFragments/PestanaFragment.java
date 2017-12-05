package com.tfgp2p.tfg_p2p_nsp.Fragmentos.PestanaFragments;


import android.os.Bundle;
import android.app.Fragment;
import android.util.AttributeSet;
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

    protected LinearLayout layoutRellenoPestana;

    protected View mainLayout;

    protected FragmentTab fragmentTab;

    public PestanaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewPestana = inflater.inflate(R.layout.fragment_pestana, container, false);

        mainLayout = viewPestana;

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

    /**
     * Colapsa o abre la pestanya
     * @param expandir
     */
    public void colapsarPestanya(boolean expandir){
        int estado;
        int newArrowImage;
        ViewGroup.LayoutParams mainParams;

        if(expandir){
            estado = LinearLayout.VISIBLE;
            newArrowImage = R.drawable.ic_expand_less_black_24dp;

            /*mainParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1.0f);*/

        }else{
            estado = LinearLayout.GONE;
            newArrowImage = R.drawable.ic_expand_more_black_24dp;
            //mainParams = new ViewGroup.LayoutParams();
        }

        //mainLayout.setLayoutParams(mainParams);
        imageExpandir.setImageResource(newArrowImage);
        layoutRellenoPestana.setVisibility(estado);
    }

    public void setFragmentTab(FragmentTab fragmentTab) {
        this.fragmentTab = fragmentTab;
    }
}
