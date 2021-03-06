package com.tfgp2p.tfg_p2p_nsp.View;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tfgp2p.tfg_p2p_nsp.Controlador.AplicacionMain;
import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.DHAmigos;
import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.DatabaseHelper;
import com.tfgp2p.tfg_p2p_nsp.R;

import com.tfgp2p.tfg_p2p_nsp.View.Activity.ActivitySeleccionCarpeta;
import com.tfgp2p.tfg_p2p_nsp.View.Fragmentos.FragmentTab;
import com.tfgp2p.tfg_p2p_nsp.View.Utils.SimpleGestureFilter;
import com.tfgp2p.tfg_p2p_nsp.View.Utils.ViewUtils;

public class MainActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener{

    private FrameLayout frameTabContent;

    private LinearLayout tabbarInicio;
    private LinearLayout tabbarAmigos;
    private LinearLayout tabbarFicheros;
    private LinearLayout tabbarConfiguracion;

    private LinearLayout currentTabbar;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private SimpleGestureFilter detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int currentLayout = 0;

        comprobarPermisos();

        setContentView(R.layout.activity_main);

        frameTabContent = (FrameLayout) findViewById(R.id.main_content_layout);

        tabbarInicio = (LinearLayout) findViewById(R.id.tabbar_tabinicio_layout);
        tabbarFicheros = (LinearLayout) findViewById(R.id.tabbar_tabficheros_layout);
        tabbarAmigos = (LinearLayout) findViewById(R.id.tabbar_tabamigos_layout);
        tabbarConfiguracion = (LinearLayout) findViewById(R.id.tabbar_tabopciones_layout);

        detector = new SimpleGestureFilter(this,this);

        //Se inicializa la aplicacion en el tab INICIO
        if(savedInstanceState != null) {
            currentLayout = savedInstanceState.getInt("CURRENT_LAYOUT");
        }

        changeFrame(getFragmentTabFromNumber(currentLayout), getTabbarFromNumber(currentLayout));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("CURRENT_LAYOUT",getCurrentLayoutNumber());
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

    /**
     * Cuando el usuario pulsa el boton de retroceso en la actividad principal cierra la aplicacion
     */
    @Override
    public void onBackPressed() {
        AlertDialog alertDialogExit = ViewUtils.createSimpleDialogOKCancelar(
                "Cerrar applicacion",
                "¿Seguro que desea cerrar la aplicación?",
                new ExitDialogMainActivityListener(),
                this
        );
        alertDialogExit.show();
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
        currentTabbar = tabbarSelected;
    }

    public int getCurrentLayoutNumber() {

        int currentLayoutNumber = 0;

        // INICIO
        // NADA

        // FICHEROS
        if(currentTabbar == tabbarFicheros){
            currentLayoutNumber = 1;
        }
        // AMIGOS
        else if(currentTabbar == tabbarAmigos){
            currentLayoutNumber = 2;
        }
        // CONFIGURACION
        else if(currentTabbar == tabbarConfiguracion){
            currentLayoutNumber = 3;
        }

        return currentLayoutNumber;
    }

    private FragmentTab getFragmentTabFromNumber(int layoutNumber){

        FragmentTab newFragment = null;

        switch(layoutNumber){
            case 1:{newFragment = FragmentTab.obtainFragment(FragmentTab.TABNAME_FICHEROS);} break; //FICHEROS
            case 2:{newFragment = FragmentTab.obtainFragment(FragmentTab.TABNAME_AMIGOS);} break; //AMIGOS
            case 3:{newFragment = FragmentTab.obtainFragment(FragmentTab.TABNAME_CONFIGURACION);} break; //CONFIGURACION
            default:{newFragment = FragmentTab.obtainFragment(FragmentTab.TABNAME_INICIO);} break; //INICIO
        }
        return newFragment;
    }

    private LinearLayout getTabbarFromNumber(int tabNumber){

        LinearLayout newLayout = null;

        switch(tabNumber){
            case 1:{newLayout = tabbarFicheros;} break; //FICHEROS
            case 2:{newLayout = tabbarAmigos;} break; //AMIGOS
            case 3:{newLayout = tabbarConfiguracion;} break; //CONFIGURACION
            default:{newLayout = tabbarInicio;} break; //INICIO
        }
        return newLayout;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        int currentLayoutNumber=getCurrentLayoutNumber();

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT : {
                currentLayoutNumber--;
                if(currentLayoutNumber<0){
                    currentLayoutNumber=3;
                }
                changeFrame(getFragmentTabFromNumber(currentLayoutNumber), getTabbarFromNumber(currentLayoutNumber));
            }break;
            case SimpleGestureFilter.SWIPE_LEFT :  {
                currentLayoutNumber++;
                if(currentLayoutNumber>3){
                    currentLayoutNumber=0;
                }
                changeFrame(getFragmentTabFromNumber(currentLayoutNumber), getTabbarFromNumber(currentLayoutNumber));
            }break;
        }
    }

    @Override
    public void onDoubleTap() {}

    public class ExitDialogMainActivityListener implements ViewUtils.IOKCancelarDialogListener {
        @Override
        public void onPossitiveButtonClick() {
            AplicacionMain.exitApp(0);
        }

        @Override
        public void onNegativeButtonClick() {
            // NADA
        }
    }
}
