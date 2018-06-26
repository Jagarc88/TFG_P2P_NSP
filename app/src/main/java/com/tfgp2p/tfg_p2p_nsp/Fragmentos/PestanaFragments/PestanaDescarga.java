package com.tfgp2p.tfg_p2p_nsp.Fragmentos.PestanaFragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.tfgp2p.tfg_p2p_nsp.Fragmentos.FragmentElements.pestanaDescarga.FragmentElementFicheroDescarga;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.Fichero;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.GestorSistemaFicheros;
import com.tfgp2p.tfg_p2p_nsp.R;

import java.util.List;

/**
 * Created by Deekin on 02/12/2017.
 */

public class PestanaDescarga extends PestanaFragment{

    LinearLayout linearLayout_listaDescargados;

    @Override
    protected void rellenaVariables(View view) {
        imageHeadPestana.setImageResource(R.drawable.ic_file_download_black_24dp);
        textTitutloHeadPestana.setText(getText(R.string.pestana_titulo_descarga));

        linearLayout_listaDescargados = createConteiner(layoutRellenoPestana);

        loadFragmentAPestanya();
    }

    private void loadFragmentAPestanya(){

        List<Fichero> ficheroList = GestorSistemaFicheros.extraerIncompletos();

        FragmentManager fragMan = getFragmentManager();
        FragmentTransaction fragTransaction;
        for(int i=0;i<ficheroList.size();i++) {
            fragTransaction = fragMan.beginTransaction();
            FragmentElementFicheroDescarga fragmentED = new FragmentElementFicheroDescarga();
            Fichero aFichero = ficheroList.get(i);
            fragmentED.setFicheroCargado(aFichero);
            if (i == 0) {
                fragTransaction.replace(linearLayout_listaDescargados.getId(), fragmentED, null);
            } else {
                fragTransaction.add(linearLayout_listaDescargados.getId(), fragmentED, null);
            }
            fragTransaction.commit();
        }
    }


    /**
     *   <ScrollView
     *        android:layout_width="match_parent"
     *        android:layout_height="match_parent">

     <LinearLayout
     android:id="@+id/elemento_listafileslots"
     android:layout_width="match_parent"
     android:layout_height="wrap_content"
     android:orientation="vertical" />
     </ScrollView>
     * @param linearLayoutCompartiendo
     * @return
     */
    private LinearLayout createConteiner(LinearLayout linearLayoutCompartiendo){

        Context context = linearLayoutCompartiendo.getContext();

        //Scroller que contendra los ficheros
        ScrollView scrollView = new ScrollView(context);
        ScrollView.LayoutParams layoutParamsScrollView =
                new ScrollView.LayoutParams(
                        ScrollView.LayoutParams.MATCH_PARENT,
                        ScrollView.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(layoutParamsScrollView);

        //
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setId(R.id.elemento_listaseleccionar);
        LinearLayout.LayoutParams layoutParamsLinearLayout =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(layoutParamsLinearLayout);

        scrollView.addView(linearLayout);
        linearLayoutCompartiendo.addView(scrollView);
        return linearLayout;
    }

}
