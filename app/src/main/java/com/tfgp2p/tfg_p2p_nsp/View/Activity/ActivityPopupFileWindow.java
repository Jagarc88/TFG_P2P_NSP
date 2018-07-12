package com.tfgp2p.tfg_p2p_nsp.View.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos.Amigo;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos.GestorSistemaAmigos;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.Fichero;
import com.tfgp2p.tfg_p2p_nsp.R;
import com.tfgp2p.tfg_p2p_nsp.View.MainActivity;
import com.tfgp2p.tfg_p2p_nsp.View.Personalizados.PersonalizedElements.ActivityPopupAmigo.ListViewSeleccionables;
import com.tfgp2p.tfg_p2p_nsp.View.Personalizados.PersonalizedElements.LayoutElementFichero;
import com.tfgp2p.tfg_p2p_nsp.View.Personalizados.PersonalizedElements.PestanaAmigos.LayoutElementAmigos;

import java.util.ArrayList;
import java.util.List;

public class ActivityPopupFileWindow extends Activity {

    private ListViewSeleccionables contentListView;
    private Button buttonDownload;
    private Button buttonCerrar;

    private Amigo selectedAmigo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_popupfilewindow);

        contentListView = findViewById(R.id.popupfilewindow_container);
        buttonDownload = findViewById(R.id.popupfilewindow_downloadbutton);
        buttonCerrar = findViewById(R.id.popupfilewindow_cerrarbutton);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        Long width = Math.round(dm.widthPixels*.8);
        Long height = Math.round(dm.heightPixels*.8);

        getWindow().setLayout(width.intValue(),height.intValue());

        selectedAmigo = LayoutElementAmigos.selectedAmigo;
        fillListFiles(GestorSistemaAmigos.getFicheroListFromAmigo(selectedAmigo));

        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickButtonDownload();
            }
        });
        buttonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickButtonCerrar();
            }
        });
    }

    public void fillListFiles(List<Fichero> ficheroList){
        List<LayoutElementFichero> ficheroLayoutList = new ArrayList<>();
        for (Fichero fichero: ficheroList) {
            LayoutElementFichero elementFichero = new LayoutElementFichero(getApplication());
            elementFichero.fillData(fichero);
            ficheroLayoutList.add(elementFichero);
        }
        contentListView.fillData(ficheroLayoutList,selectedAmigo);
    }

    private void onClickButtonDownload(){
        //TODO Pone la lista de elementos seleccionados a descargar
        for (LayoutElementFichero layoutElementFichero:contentListView.getSelected()){
            System.out.println(layoutElementFichero.getFichero().getNombre());
        }
    }

    private void onClickButtonCerrar(){
        Intent listCarpeta = new Intent(this, MainActivity.class);
        startActivity(listCarpeta);
    }
}
