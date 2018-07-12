package com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos;


import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.Fichero;

import java.util.ArrayList;
import java.util.List;

public class GestorSistemaAmigos {

    public static final List<Amigo> amigoList = new ArrayList<>();

    public static List<Fichero> getFicheroListFromAmigo(Amigo amigo){
        // TODO Obtener la lista de ficheros de un amigo usando la conexion
        List<Fichero> ficheroList = new ArrayList<>();

        if(amigo.getNombreAmigo().equals("Manuel")){
            ficheroList.add(new Fichero("fichero1.mp3","/downloads/manuel"));
            ficheroList.add(new Fichero("fichero2.mp3","/downloads/manuel"));
            ficheroList.add(new Fichero("fichero3.mp3","/downloads/manuel"));
        }
        if(amigo.getNombreAmigo().equals("Antonio")){
            ficheroList.add(new Fichero("fichero11.mp3","/downloads/antonio"));
            ficheroList.add(new Fichero("fichero12.mp3","/downloads/antonio"));
        }
        if(amigo.getNombreAmigo().equals("Sonia")){
            ficheroList.add(new Fichero("fichero21.mp3","/downloads/sonia"));
            ficheroList.add(new Fichero("fichero22.mp3","/downloads/sonia"));
            ficheroList.add(new Fichero("fichero23.mp3","/downloads/sonia"));
            ficheroList.add(new Fichero("fichero24.mp3","/downloads/sonia"));
        }

        return ficheroList;
    }
}
