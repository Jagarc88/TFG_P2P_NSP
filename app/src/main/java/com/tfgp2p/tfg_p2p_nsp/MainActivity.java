package com.tfgp2p.tfg_p2p_nsp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tfgp2p.tfg_p2p_nsp.Fragmentos.AmigosFragment;
import com.tfgp2p.tfg_p2p_nsp.Fragmentos.FicherosFragment;
import com.tfgp2p.tfg_p2p_nsp.Fragmentos.FragmentTab;
import com.tfgp2p.tfg_p2p_nsp.Fragmentos.InicioFragment;
import com.tfgp2p.tfg_p2p_nsp.Fragmentos.PestanaFragments.PestanaFragment;
import com.tfgp2p.tfg_p2p_nsp.Gnutella.Cliente;
import com.tfgp2p.tfg_p2p_nsp.Gnutella.Echoer;
import com.tfgp2p.tfg_p2p_nsp.Gnutella.Servidor;

public class MainActivity extends AppCompatActivity {

    FrameLayout frameTabContent;

    private LinearLayout tabbarInicio;
    private LinearLayout tabbarAmigos;
    private LinearLayout tabbarFicheros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameTabContent = (FrameLayout) findViewById(R.id.main_content_layout);

        tabbarInicio = (LinearLayout) findViewById(R.id.tabbar_tabinicio_layout);
        tabbarFicheros = (LinearLayout) findViewById(R.id.tabbar_tabficheros_layout);
        tabbarAmigos = (LinearLayout) findViewById(R.id.tabbar_tabamigos_layout);

        ////////////////////////////// PRUEBA DEL ECHOER///////////////////////////////
        //new Echoer(1100, 1101, 5);
        /*new Thread(new Runnable(){
           public void run(){
               // 1103 al ejecutar en mi movil.
               new Echoer(1103, 1104, 5);
           }
        }).start();
        */
        new Thread(new Runnable(){
            public void run(){
                // 1103 al ejecutar en mi móvil.
				/*
				 * PARA LAS PRUEBAS PRIMERO INICIO EL SERVIDOR Y LUEGO EL CLIENTE.
				 */
                Servidor.getInstance(1103);
				Cliente.getInstance();
			}
        }).start();
        ///////////////////////////////////////////////////////////////////////////////

        //Se inicializa la aplicacion en el tab INICIO
        changeFrame(new InicioFragment(), tabbarInicio);
    }

    public void onClickTabbar(View view){

        FragmentTab newFragment = null;

        // INICIO
        if(view == tabbarInicio){
            newFragment = FragmentTab.obtainFragment(FragmentTab.TABNAME_INICIO);
        }
        // FICHEROS
        else if(view == tabbarFicheros){
            newFragment = FragmentTab.obtainFragment(FragmentTab.TABNAME_FICHEROS);
        }
        // AMIGOS
        else if(view == tabbarAmigos){
            newFragment = FragmentTab.obtainFragment(FragmentTab.TABNAME_AMIGOS);
        }

        changeFrame(newFragment, (LinearLayout)view);
    }

    private void changeFrame(FragmentTab fragmentTab, LinearLayout tabbarSelected){

        FragmentManager fragMan = getFragmentManager();
        FragmentTransaction fragTransaction = fragMan.beginTransaction();
        fragTransaction.replace(frameTabContent.getId(), fragmentTab, "fragment");
        fragTransaction.commit();

        // Cambia el tabbar seleccionado
        tabbarInicio.setBackgroundResource(R.drawable.tabbar_background_default);
        tabbarFicheros.setBackgroundResource(R.drawable.tabbar_background_default);
        tabbarAmigos.setBackgroundResource(R.drawable.tabbar_background_default);

        tabbarSelected.setBackgroundResource(R.drawable.tabbar_background_selected);
    }

    // TODO Aplicacion cuando el movil esta horizontal
}
