package com.tfgp2p.tfg_p2p_nsp.View.Fragmentos.FragmentElements.pestanaAmigos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos.Amigo;
import com.tfgp2p.tfg_p2p_nsp.R;

public class LayoutElementAmigos extends LinearLayout{

    private Button buttonExplore;
    private TextView textViewFriendName;
    private ImageView imageViewStateIcon;

    private View viewInicio;
    private Amigo amigo;

    public LayoutElementAmigos(Context context) {
        super(context);

        viewInicio = LayoutInflater.from(context).inflate(R.layout.element_amigo_lista, this, false);
        this.addView(viewInicio);

        buttonExplore = viewInicio.findViewById(R.id.element_fragment_element_amigos);
        textViewFriendName = viewInicio.findViewById(R.id.test_descarga_nombrepersona);
        imageViewStateIcon = viewInicio.findViewById(R.id.test_imageview_fragment_element_amigos);
    }

    public static final int STATE_USER_OFFLINE = 0;
    public static final int STATE_USER_ONLINE = 1;

    public void setIconState(int state){
        switch (state){
            case STATE_USER_OFFLINE:{
                imageViewStateIcon.setImageResource(R.drawable.ic_person_grey_24dp);
            }break;
            case STATE_USER_ONLINE:{
                imageViewStateIcon.setImageResource(R.drawable.ic_person_green_24dp);
            } break;
        }
    }

    public Amigo getAmigo() {
        return amigo;
    }

    public void setAmigo(Amigo amigo) {
        this.amigo = amigo;
    }
}
