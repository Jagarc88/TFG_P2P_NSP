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

    protected LinearLayout pestanaHeadLayout;

    protected LinearLayout layoutRellenoPestana;

    protected LinearLayout mainLayout;

    protected FragmentTab fragmentTab;

    protected boolean isExpandable = true;

    public PestanaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewPestana = inflater.inflate(R.layout.fragment_pestana, container, false);

        mainLayout = (LinearLayout) viewPestana;

        textTitutloHeadPestana = viewPestana.findViewById(R.id.pestanahead_texto);
        imageHeadPestana = viewPestana.findViewById(R.id.pestanahead_image_icon);
        imageExpandir = viewPestana.findViewById(R.id.pestana_button_expandir);
        pestanaHeadLayout = viewPestana.findViewById(R.id.pestana_head);
        pestanaHeadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extendButtomPressed();
            }
        });

        layoutRellenoPestana = viewPestana.findViewById(R.id.pestana_relleno);

        rellenaVariables(viewPestana);

        setExpandable(isExpandable);

        return viewPestana;
    }

    public void setExpandable(boolean expandable){
        if(imageExpandir != null) {
            imageExpandir.setVisibility(expandable ? LinearLayout.VISIBLE : LinearLayout.GONE);
        }
        isExpandable = expandable;
    }

    public void extendButtomPressed(){
        fragmentTab.colapsarPestanya(this);
    }

    /**
     * Rellena la pestanya con los datos propios
     */
    protected abstract void rellenaVariables(View thisView);

    /**
     * Colapsa o abre la pestanya
     * @param colapsar
     */
    public void colapsarPestanya(boolean colapsar){
        int estado;
        int newArrowImage;
        LinearLayout.LayoutParams mainParams;

        if(isExpandable) {
            if (!colapsar) {
                estado = LinearLayout.VISIBLE;
                newArrowImage = R.drawable.ic_expand_less_black_24dp;
                mainParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1.0f);
            } else {
                estado = LinearLayout.GONE;
                newArrowImage = R.drawable.ic_expand_more_black_24dp;
                mainParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            mainLayout.setLayoutParams(mainParams);
            imageExpandir.setImageResource(newArrowImage);
            layoutRellenoPestana.setVisibility(estado);
        }
    }

    public void setFragmentTab(FragmentTab fragmentTab) {
        this.fragmentTab = fragmentTab;
    }

    public boolean getColapsed() {
        return layoutRellenoPestana.getVisibility()==LinearLayout.GONE;
    }
}
