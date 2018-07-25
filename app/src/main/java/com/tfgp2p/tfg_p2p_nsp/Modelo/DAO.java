package com.tfgp2p.tfg_p2p_nsp.Modelo;

import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.DHAmigos;
import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.DHConfiguration;

public class DAO {

    private static DHAmigos dhAmigos;
    private static DHConfiguration dhConfiguration;

    private DAO(){}

    public static DHConfiguration databaseConfiguration(){
        if(DAO.dhConfiguration==null) {
            DAO.dhConfiguration = DHConfiguration.getInstance();
        }
        return dhConfiguration;
    }

    public static DHAmigos databaseAmigos(){
        if(DAO.dhAmigos==null) {
            DAO.dhAmigos = DHAmigos.getInstance();
        }
        return dhAmigos;
    }
}
