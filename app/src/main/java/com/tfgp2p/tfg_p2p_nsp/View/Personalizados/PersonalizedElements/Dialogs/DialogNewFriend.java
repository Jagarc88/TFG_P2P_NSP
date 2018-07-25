package com.tfgp2p.tfg_p2p_nsp.View.Personalizados.PersonalizedElements.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tfgp2p.tfg_p2p_nsp.AlertException;
import com.tfgp2p.tfg_p2p_nsp.Controlador.Observers.Amigo.OberserverAmigos;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos.Amigo;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos.GestorSistemaAmigos;
import com.tfgp2p.tfg_p2p_nsp.R;

public class DialogNewFriend {

    private View mainView;

    private ConstraintLayout buttonCancelar;
    private ConstraintLayout buttonCrearAmigo;

    private EditText editTextNombre;
    private EditText editTextIdentificador;

    private TextView textViewError;

    private AlertDialog alertDialog;

    public DialogNewFriend(Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();

        mainView = inflater.inflate(R.layout.layout_dialog_newfriend,null);

        editTextIdentificador = mainView.findViewById(R.id.layout_dialog_newfriend_identificador_editText);
        editTextNombre = mainView.findViewById(R.id.layout_dialog_newfriend_nombre_editText);

        textViewError = mainView.findViewById(R.id.layout_dialog_newfriend_textview_errormessage);

        buttonCancelar = mainView.findViewById(R.id.layout_dialog_newfriend_buttonlay_cancelar);
        buttonCrearAmigo = mainView.findViewById(R.id.layout_dialog_newfriend_buttonlay_anyadiramigo);

        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCancelar();
            }
        });
        buttonCrearAmigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickNewFriend();
            }
        });

        builder.setTitle("Añadir amigo")
                .setView(mainView);

        alertDialog = builder.create();
    }

    private void onClickNewFriend(){
        if(checkDataIsProper()){
            anyadirNuevoAmigo();
            alertDialog.dismiss();
        } else{
            showError(0);
        }
    }

    private void onClickCancelar(){
        alertDialog.dismiss();
    }

    private boolean checkDataIsProper(){

        return true;
    }

    private void anyadirNuevoAmigo(){
        Amigo amigo = new Amigo(
                editTextNombre.getText().toString(),
                editTextIdentificador.getText().toString(),
                GestorSistemaAmigos.STATE_USER_OFFLINE);
        try {
            GestorSistemaAmigos.addFriend(amigo);
        } catch (AlertException e) {
            e.printStackTrace();
        }
    }

    public void show(){
        alertDialog.show();
    }

    private void showError(int razonDelError){
        textViewError.setVisibility(TextView.VISIBLE);

        switch(razonDelError){
            default:{
                textViewError.setText("Los datos introducidos son incorrectos");
            }break;
        }
    }
}
