package com.tfgp2p.tfg_p2p_nsp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.Fichero;
import com.tfgp2p.tfg_p2p_nsp.Modelo.ConfigProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivitySeleccionCarpeta extends AppCompatActivity {

    private File currentDir;
    private List<Fichero> dir;
    private Fichero itemSelected;
    Map<String,Fichero> mapViewItem;

    private LinearLayout linearLayout_listaSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_seleccioncarpeta);

        mapViewItem = new HashMap<>();
        itemSelected = null;
        linearLayout_listaSelected = findViewById(R.id.selecter_lista);

        fillContent(ConfigProperties.getProperty(ConfigProperties.PROP_FILES_FOLDER));
    }

    public void fillContent(String URL){

        dir = new ArrayList<>();

        currentDir = new File(URL);

        // Si no existe creamos la carpeta
        if(!currentDir.exists()) {
            currentDir.mkdir();
        }

        fill(currentDir);
        draw();
    }

    private void draw() {

        // Elimina toodo el contenido
        linearLayout_listaSelected.removeAllViews();

        // Rellena con el nuevo contenido
        for (Fichero item: dir) {
            View newPestanya = crear_pestanya(item.getNombre());

            linearLayout_listaSelected.addView(newPestanya);
            mapViewItem.put(item.getNombre(), item);
        }
    }

    public void volverAMenu(View view){
        volverAMenu();
    }

    private void volverAMenu(){
        Intent menu = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(menu);
    }

    private void fill(File f)
    {
        dir = new ArrayList<>();

        File[] dirs = f.listFiles();
        File parent = f.getParentFile();

        Fichero item;

        try{
            for(File ff: dirs)
            {
                if(ff.isDirectory()){
                    dir.add(new Fichero(ff.getName(),ff.getAbsolutePath()));
                }
            }
        }catch(Exception e)
        {}

        Collections.sort(dir);

        if(parent != null){
            item =  new Fichero("/..",parent.getAbsolutePath());

            if(!dir.isEmpty()){
                dir.add(0, item);
            } else{
                dir.add(item);
            }
        }
    }

    public View crear_pestanya(String name){

        LayoutInflater inflater = LayoutInflater.from(this);
        View pestanya = inflater.inflate(R.layout.elemento_listaseleccionar, null, false);

        TextView textName = pestanya.findViewById(R.id.name_file);

        textName.setText(name);

        return pestanya;
    }

    public void seleccionarListaCarpetas(View view){

        String nameFileSelected;

        nameFileSelected = (String) ((TextView)view.findViewById(R.id.name_file)).getText();

        itemSelected = mapViewItem.get(nameFileSelected);

        if(itemSelected != null){

            fillContent(itemSelected.getPath());
        }
    }

    /**
     * Boton "Seleccionar"
     * @param view
     */
    public void onClickSelectedButton(View view){

        if(itemSelected != null){

            ConfigProperties.setProperty(ConfigProperties.PROP_FILES_FOLDER, itemSelected.getPath());

            fillContent(ConfigProperties.getProperty(ConfigProperties.PROP_FILES_FOLDER));

            volverAMenu();
        }

        System.out.println("URL: "+ConfigProperties.getProperty(ConfigProperties.PROP_FILES_FOLDER));
    }

    /**
     * Devuelve el Item del fichero seleccionado, si no hay nada seleccionado
     * o el elemento seleccionado no es accesible devuelve null
     * @return
     */
    public Fichero getItemSelected() {

        Fichero itemFileSelected = null;
        String nameFileSelected;
/*
        if(selected != null){
            nameFileSelected = (String) ((TextView)selected.findViewById(R.id.name_file)).getText();
            itemFileSelected = mapViewItem.get(nameFileSelected);
        }
*/
        return itemFileSelected;

    }
}
