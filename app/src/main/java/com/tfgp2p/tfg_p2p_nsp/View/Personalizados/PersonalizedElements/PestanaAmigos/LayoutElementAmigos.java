package com.tfgp2p.tfg_p2p_nsp.View.Personalizados.PersonalizedElements.PestanaAmigos;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos.Amigo;
import com.tfgp2p.tfg_p2p_nsp.R;
import com.tfgp2p.tfg_p2p_nsp.View.Activity.ActivityPopupFileWindow;

public class LayoutElementAmigos extends LinearLayout {

    public static final int STATE_USER_OFFLINE = 0;
    public static final int STATE_USER_ONLINE = 1;

    public static Amigo selectedAmigo;

    private TextView textViewFriendName;
    private ImageView imageViewStateIcon;

    private View viewInicio;
    private Amigo amigo;

    public LayoutElementAmigos(Context context) {
        super(context);

        viewInicio = LayoutInflater.from(context).inflate(R.layout.element_amigo_lista, this, false);
        this.addView(viewInicio);

        textViewFriendName = viewInicio.findViewById(R.id.test_descarga_nombrepersona);
        imageViewStateIcon = viewInicio.findViewById(R.id.test_imageview_fragment_element_amigos);

        viewInicio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickExplore();
            }
        });
    }

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

    public void setData(Amigo amigo) {
        this.amigo = amigo;

        textViewFriendName.setText(amigo.getNombreAmigo());
        setIconState(amigo.getState());
    }

    public void onClickExplore(){
        selectedAmigo = amigo;
        Intent listCarpeta = new Intent(getContext(), ActivityPopupFileWindow.class);
        getContext().startActivity(listCarpeta);
    }
}
