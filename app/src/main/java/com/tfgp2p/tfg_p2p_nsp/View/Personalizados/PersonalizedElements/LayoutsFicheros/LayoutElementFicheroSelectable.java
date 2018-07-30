package com.tfgp2p.tfg_p2p_nsp.View.Personalizados.PersonalizedElements.LayoutsFicheros;

import android.content.Context;
import android.view.View;

import com.tfgp2p.tfg_p2p_nsp.R;

public class LayoutElementFicheroSelectable extends LayoutElementFichero{

    private boolean selected;

    public LayoutElementFicheroSelectable(Context context) {
        super(context);
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSelected();
            }
        });
    }

    public void toggleSelected(){
        setSelected(!isSelected());
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        if(selected){
            elementFileslot.setBackgroundResource(R.drawable.background_config_opt_selected);
        }else{
            elementFileslot.setBackgroundResource(R.drawable.background_config_opt);
        }
    }
}
