package com.tfgp2p.tfg_p2p_nsp.View.Personalizados.PersonalizedElements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.Fichero;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.TipoFichero;
import com.tfgp2p.tfg_p2p_nsp.R;

public class LayoutElementFichero extends LinearLayout {

    private TextView textName;
    private ImageView iconView;

    private Fichero fichero;

    private boolean selected;

    public LayoutElementFichero(Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View elementFileslot = inflater.inflate(R.layout.elemento_fileslot_small, null, false);

        textName = elementFileslot.findViewById(R.id.fileslot_small_nameFile);
        iconView = elementFileslot.findViewById(R.id.fileslot_small_icon);

        selected = false;
        setBackgroundResource(R.drawable.background_config_opt);

        elementFileslot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selected=!selected;
                if(selected){
                    setBackgroundResource(R.drawable.background_config_opt_selected);
                }else{
                    setBackgroundResource(R.drawable.background_config_opt);
                }
            }
        });

        this.addView(elementFileslot);
    }

    public Fichero getFichero(){
        return this.fichero;
    }

    public void fillData(Fichero fichero){
        String name = fichero.getNombre();

        this.fichero = fichero;

        textName.setText(name);
        iconView.setImageDrawable(TipoFichero.obtainDrawable(TipoFichero.obtainTipoFichero(name), getContext()));
    }

    @Override
    public boolean isSelected() {
        return selected;
    }
}
