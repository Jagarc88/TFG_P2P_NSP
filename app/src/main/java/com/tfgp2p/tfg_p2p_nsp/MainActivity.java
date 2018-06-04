package com.tfgp2p.tfg_p2p_nsp;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tfgp2p.tfg_p2p_nsp.Fragmentos.FragmentTab;
import com.tfgp2p.tfg_p2p_nsp.Fragmentos.InicioFragment;
import com.tfgp2p.tfg_p2p_nsp.Conexion.Cliente;

public class MainActivity extends AppCompatActivity {

    FrameLayout frameTabContent;

    private LinearLayout tabbarInicio;
    private LinearLayout tabbarAmigos;
    private LinearLayout tabbarFicheros;
    private LinearLayout tabbarConfiguracion;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        comprobarPermisos();

        //ConfigProperties.loadConfiguration(getApplicationContext());

        setContentView(R.layout.activity_main);

        frameTabContent = (FrameLayout) findViewById(R.id.main_content_layout);

        tabbarInicio = (LinearLayout) findViewById(R.id.tabbar_tabinicio_layout);
        tabbarFicheros = (LinearLayout) findViewById(R.id.tabbar_tabficheros_layout);
        tabbarAmigos = (LinearLayout) findViewById(R.id.tabbar_tabamigos_layout);
        tabbarConfiguracion = (LinearLayout) findViewById(R.id.tabbar_tabopciones_layout);

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
                //Servidor.getInstance();
				Cliente.getInstance();
			}
		}).start();
        ///////////////////////////////////////////////////////////////////////////////

        //Se inicializa la aplicacion en el tab INICIO
        changeFrame(new InicioFragment(), tabbarInicio);
    }

    private void comprobarPermisos(){

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
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
        // CONFIGURACION
        else if(view == tabbarConfiguracion){
            newFragment = FragmentTab.obtainFragment(FragmentTab.TABNAME_CONFIGURACION);
        }

        changeFrame(newFragment, (LinearLayout)view);
    }

    public void downloadSelectiorWindow(View view){
        Intent listCarpeta = new Intent(this, ActivitySeleccionCarpeta.class);
        startActivity(listCarpeta);
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
        tabbarConfiguracion.setBackgroundResource(R.drawable.tabbar_background_default);

        tabbarSelected.setBackgroundResource(R.drawable.tabbar_background_selected);
    }

    // TODO Aplicacion cuando el movil esta horizontal
}
